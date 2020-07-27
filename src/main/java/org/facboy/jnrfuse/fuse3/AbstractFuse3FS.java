package org.facboy.jnrfuse.fuse3;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.mapper.FromNativeConverter;
import jnr.ffi.provider.jffi.ClosureHelper;
import jnr.ffi.types.off_t;
import jnr.ffi.util.EnumMapper;
import ru.serce.jnrfuse.AbstractBaseFuseFS;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.NotImplemented;
import org.facboy.jnrfuse.fuse3.flags.Fuse3ReaddirFlags;
import org.facboy.jnrfuse.fuse3.flags.Fuse3RenameFlags;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.Flock;
import org.facboy.jnrfuse.fuse3.struct.Fuse3Operations;
import ru.serce.jnrfuse.struct.FuseBufvec;
import ru.serce.jnrfuse.struct.FuseFileInfo;
import ru.serce.jnrfuse.struct.FusePollhandle;
import ru.serce.jnrfuse.struct.Statvfs;
import ru.serce.jnrfuse.struct.Timespec;

public abstract class AbstractFuse3FS extends AbstractBaseFuseFS<LibFuse3, Fuse3Operations> implements Fuse3FS {

    public AbstractFuse3FS() {
    }

    @Override
    protected Class<LibFuse3> libFuseClass() {
        return LibFuse3.class;
    }

    @Override
    protected String mappedLibraryName() {
        return "fuse3";
    }

    @Override
    protected String libraryFilename() {
        return "libfuse3.so.3";
    }

    @Override
    protected Fuse3Operations createFuseOperations(Runtime runtime) {
        return new Fuse3Operations(runtime);
    }

    @Override
    protected void init(Fuse3Operations fuseOperations) {
        notImplementedMethods = Arrays.stream(getClass().getMethods())
            .filter(method -> method.getAnnotation(NotImplemented.class) != null)
            .map(Method::getName)
            .collect(Collectors.toSet());

        AbstractFuse3FS fuse = this;
        if (isImplemented("getattr")) {
            fuseOperations.getattr.set(this::doGetattr);
        }
        if (isImplemented("readlink")) {
            fuseOperations.readlink.set(fuse::readlink);
        }
        if (isImplemented("mknod")) {
            fuseOperations.mknod.set(fuse::mknod);
        }
        if (isImplemented("mkdir")) {
            fuseOperations.mkdir.set(fuse::mkdir);
        }
        if (isImplemented("unlink")) {
            fuseOperations.unlink.set(fuse::unlink);
        }
        if (isImplemented("rmdir")) {
            fuseOperations.rmdir.set(fuse::rmdir);
        }
        if (isImplemented("symlink")) {
            fuseOperations.symlink.set(fuse::symlink);
        }
        if (isImplemented("rename")) {
            fuseOperations.rename.set(this::doRename);
        }
        if (isImplemented("link")) {
            fuseOperations.link.set(fuse::link);
        }
        if (isImplemented("chmod")) {
            fuseOperations.chmod.set(this::doChmod);
        }
        if (isImplemented("chown")) {
            fuseOperations.chown.set(this::doChown);
        }
        if (isImplemented("truncate")) {
            fuseOperations.truncate.set(this::doTruncate);
        }
        if (isImplemented("open")) {
            fuseOperations.open.set((path, fi) -> fuse.open(path, FuseFileInfo.of(fi)));
        }
        if (isImplemented("read")) {
            fuseOperations.read.set((path, buf, size, offset, fi) -> fuse.read(path, buf, size, offset, FuseFileInfo.of(fi)));
        }
        if (isImplemented("write")) {
            fuseOperations.write.set((path, buf, size, offset, fi) -> fuse.write(path, buf, size, offset, FuseFileInfo.of(fi)));
        }
        if (isImplemented("statfs")) {
            fuseOperations.statfs.set((path, stbuf) -> fuse.statfs(path, Statvfs.of(stbuf)));
        }
        if (isImplemented("flush")) {
            fuseOperations.flush.set((path, fi) -> fuse.flush(path, FuseFileInfo.of(fi)));
        }
        if (isImplemented("release")) {
            fuseOperations.release.set((path, fi) -> fuse.release(path, FuseFileInfo.of(fi)));
        }
        if (isImplemented("fsync")) {
            fuseOperations.fsync.set((path, isdatasync, fi) -> fuse.fsync(path, isdatasync, FuseFileInfo.of(fi)));
        }
        if (isImplemented("setxattr")) {
            fuseOperations.setxattr.set(fuse::setxattr);
        }
        if (isImplemented("getxattr")) {
            fuseOperations.getxattr.set(fuse::getxattr);
        }
        if (isImplemented("listxattr")) {
            fuseOperations.listxattr.set(fuse::listxattr);
        }
        if (isImplemented("removexattr")) {
            fuseOperations.removexattr.set(fuse::removexattr);
        }
        if (isImplemented("opendir")) {
            fuseOperations.opendir.set((path, fi) -> fuse.opendir(path, FuseFileInfo.of(fi)));
        }
        if (isImplemented("readdir")) {
            fuseOperations.readdir.set(this::doReaddir);
        }
        if (isImplemented("releasedir")) {
            fuseOperations.releasedir.set((path, fi) -> fuse.releasedir(path, FuseFileInfo.of(fi)));
        }
        if (isImplemented("fsyncdir")) {
            fuseOperations.fsyncdir.set((path, fi) -> fuse.fsyncdir(path, FuseFileInfo.of(fi)));
        }
        fuseOperations.init.set(this::doInit);
        if (isImplemented("destroy")) {
            fuseOperations.destroy.set(fuse::destroy);
        }
        if (isImplemented("access")) {
            fuseOperations.access.set(fuse::access);
        }
        if (isImplemented("create")) {
            fuseOperations.create.set((path, mode, fi) -> fuse.create(path, mode, FuseFileInfo.of(fi)));
        }
        if (isImplemented("lock")) {
            fuseOperations.lock.set((path, fi, cmd, flock) -> fuse.lock(path, FuseFileInfo.of(fi), cmd, Flock.of(flock)));
        }
        if (isImplemented("utimens")) {
            fuseOperations.utimens.set(this::doUtimens);
        }
        if (isImplemented("bmap")) {
            fuseOperations.bmap.set((path, blocksize, idx) -> fuse.bmap(path, blocksize, idx.getLong(0)));
        }
        if (isImplemented("ioctl")) {
            fuseOperations.ioctl.set((path, cmd, arg, fi, flags, data) -> fuse.ioctl(path, cmd, arg, FuseFileInfo.of(fi), flags, data));
        }
        if (isImplemented("poll")) {
            fuseOperations.poll.set((path, fi, ph, reventsp) -> fuse.poll(path, FuseFileInfo.of(fi), FusePollhandle.of(ph), reventsp));
        }
        if (isImplemented("write_buf")) {
            fuseOperations.write_buf.set((path, buf, off, fi) -> fuse.write_buf(path, FuseBufvec.of(buf), off, FuseFileInfo.of(fi)));
        }
        if (isImplemented("read_buf")) {
            fuseOperations.read_buf.set((path, bufp, size, off, fi) -> fuse.read_buf(path, bufp, size, off, FuseFileInfo.of(fi)));
        }
        if (isImplemented("flock")) {
            fuseOperations.flock.set((path, fi, op) -> fuse.flock(path, FuseFileInfo.of(fi), op));
        }
        if (isImplemented("fallocate")) {
            fuseOperations.fallocate.set((path, mode, off, length, fi) -> fuse.fallocate(path, mode, off, length, FuseFileInfo.of(fi)));
        }
    }

    private int doChmod(String path, long mode, Pointer fi) {
        return chmod(path, mode, FuseFileInfo.of(fi));
    }

    private int doChown(String path, long uid, long gid, Pointer fi) {
        return chown(path, uid, gid, FuseFileInfo.of(fi));
    }

    private int doGetattr(String path, Pointer stat, Pointer fi) {
        return getattr(path, FileStat.of(stat), FuseFileInfo.of(fi));
    }

    private Pointer doInit(Pointer conn, Pointer cfg) {
        AbstractFuse3FS.this.fusePointer = libFuse.fuse_get_context().fuse.get();
        if (isImplemented("init")) {
            return init(conn, cfg);
        }
        return null;
    }

    private int doReaddir(String path, Pointer buf, Pointer filter, @off_t long offset, Pointer fi, int flags) {
        ClosureHelper helper = ClosureHelper.getInstance();

        FromNativeConverter<FuseFillDir, Pointer> fillDirConverter = helper.getNativeConveter(FuseFillDir.class);
        FuseFillDir filterFunc = fillDirConverter.fromNative(filter, helper.getFromNativeContext());

        Fuse3ReaddirFlags fuse3ReaddirFlags = (Fuse3ReaddirFlags) EnumMapper.getInstance(Fuse3ReaddirFlags.class).valueOf(flags);
        if (fuse3ReaddirFlags == Fuse3ReaddirFlags.NULL_VALUE) {
            fuse3ReaddirFlags = null;
        }

        return readdir(path, buf, filterFunc, offset, FuseFileInfo.of(fi), fuse3ReaddirFlags);
    }

    private int doRename(String oldpath, String newpath, int flags) {
        Fuse3RenameFlags fuse3RenameFlags = (Fuse3RenameFlags) EnumMapper.getInstance(Fuse3RenameFlags.class).valueOf(flags);
        if (fuse3RenameFlags == Fuse3RenameFlags.NULL_VALUE) {
            fuse3RenameFlags = null;
        }

        return rename(oldpath, newpath, fuse3RenameFlags);
    }

    private int doTruncate(String path, long size, Pointer fi) {
        return truncate(path, size, FuseFileInfo.of(fi));
    }

    private int doUtimens(String path, Pointer timespec, Pointer fi) {
        Timespec timespec1 = Timespec.of(timespec);
        Timespec timespec2 = Timespec.of(timespec.slice(Struct.size(timespec1)));
        return utimens(path, new Timespec[]{timespec1, timespec2}, FuseFileInfo.of(fi));
    }
}
