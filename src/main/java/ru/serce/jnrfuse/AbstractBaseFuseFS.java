package ru.serce.jnrfuse;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.posix.util.Platform;
import ru.serce.jnrfuse.struct.FuseContext;
import ru.serce.jnrfuse.utils.MountUtils;
import ru.serce.jnrfuse.utils.SecurityUtils;
import ru.serce.jnrfuse.utils.WinPathUtils;

public abstract class AbstractBaseFuseFS<L extends BaseLibFuse<O>, O extends Struct> implements BaseFuseFS {

    protected static final int TIMEOUT = 2000; // ms
    private static final String[] osxFuseLibraries = {"fuse4x", "osxfuse", "macfuse", "fuse"};

    protected Set<String> notImplementedMethods;
    protected final L libFuse;
    protected final O fuseOperations;
    protected final AtomicBoolean mounted = new AtomicBoolean();
    protected Path mountPoint;
    protected volatile Pointer fusePointer;

    public AbstractBaseFuseFS() {
        jnr.ffi.Platform p = jnr.ffi.Platform.getNativePlatform();
        L libFuse = null;
        switch (p.getOS()) {
            case DARWIN:
                for (String library : osxFuseLibraries) {
                    try {
                        // Regular FUSE-compatible fuse library
                        libFuse = LibraryLoader.create(libFuseClass()).failImmediately().load(library);
                        break;
                    } catch (Throwable e) {
                        // Carry on
                    }
                }
                if (libFuse == null) {
                    // Everything failed. Do a last-ditch attempt.
                    // Worst-case scenario, this causes an exception
                    // which will be more meaningful to the user than a NullPointerException on libFuse.
                    libFuse = LibraryLoader.create(libFuseClass()).failImmediately().load("fuse");
                }
                break;
            case WINDOWS:
                String winFspPath = WinPathUtils.getWinFspPath();
                libFuse = LibraryLoader.create(libFuseClass()).failImmediately().load(winFspPath);
                break;
            case LINUX:
            default: // Assume linux since we have no further OS evidence
                try {
                    // Try loading library by going through the library mapping process, see j.l.System.mapLibraryName
                    libFuse = LibraryLoader.create(libFuseClass()).failImmediately().load(mappedLibraryName());
                } catch (Throwable e) {
                    // Try loading the dynamic library directly which will end up calling dlopen directly, see
                    // com.kenai.jffi.Foreign.dlopen
                    libFuse = LibraryLoader.create(libFuseClass()).failImmediately().load(libraryFilename());
                }
        }
        this.libFuse = libFuse;

        Runtime runtime = Runtime.getSystemRuntime();
        fuseOperations = createFuseOperations(runtime);
        init(fuseOperations);
    }

    protected abstract Class<L> libFuseClass();

    protected abstract String mappedLibraryName();

    protected abstract String libraryFilename();

    protected abstract O createFuseOperations(Runtime runtime);

    public FuseContext getContext() {
        return libFuse.fuse_get_context();
    }

    protected abstract void init(O fuseOperations);

    @Override
    public void mount(Path mountPoint, boolean blocking, boolean debug, String[] fuseOpts) {
        if (!mounted.compareAndSet(false, true)) {
            throw new FuseException("Fuse fs already mounted!");
        }
        this.mountPoint = mountPoint;
        String[] arg;
        String mountPointStr = mountPoint.toAbsolutePath().toString();
        if (mountPointStr.endsWith("\\")) {
            mountPointStr = mountPointStr.substring(0, mountPointStr.length() - 1);
        }
        if (!debug) {
            arg = new String[]{getFSName(), "-f", mountPointStr};
        } else {
            arg = new String[]{getFSName(), "-f", "-d", mountPointStr};
        }
        if (fuseOpts.length != 0) {
            int argLen = arg.length;
            arg = Arrays.copyOf(arg, argLen + fuseOpts.length);
            System.arraycopy(fuseOpts, 0, arg, argLen, fuseOpts.length);
        }

        final String[] args = arg;
        try {
            if (SecurityUtils.canHandleShutdownHooks()) {
                java.lang.Runtime.getRuntime().addShutdownHook(new Thread(this::umount));
            }
            int res;
            if (blocking) {
                res = execMount(args);
            } else {
                // Create a separate thread to hold the mounted FUSE file system.
                CompletableFuture<Integer> mountResult = new CompletableFuture<>();
                Thread mountThread = new Thread(() -> {
                    try {
                        mountResult.complete(execMount(args));
                    } catch (Throwable t) {
                        mountResult.completeExceptionally(t);
                    }
                }, "jnr-fuse-mount-thread");
                mountThread.setDaemon(true);
                mountThread.start();
                try {
                    res = mountResult.get(TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    // ok
                    res = 0;
                }
            }
            if (res != 0) {
                throw new FuseException("Unable to mount FS, return code = " + res);
            }
        } catch (Exception e) {
            mounted.set(false);
            throw new FuseException("Unable to mount FS", e);
        }
    }

    protected boolean isImplemented(String funcName) {
        return !notImplementedMethods.contains(funcName);
    }

    private int execMount(String[] arg) {
        return libFuse.fuse_main_real(arg.length, arg, fuseOperations, Struct.size(fuseOperations), null);
    }

    @Override
    public void umount() {
        if (!mounted.get()) {
            return;
        }
        if (Platform.IS_WINDOWS) {
            Pointer fusePointer = this.fusePointer;
            if (fusePointer != null) {
                libFuse.fuse_exit(fusePointer);
            }
        } else {
            MountUtils.umount(mountPoint);
        }
        mounted.set(false);
    }

    protected String getFSName() {
        return "fusefs" + ThreadLocalRandom.current().nextInt();
    }
}
