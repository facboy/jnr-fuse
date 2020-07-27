package org.facboy.jnrfuse.fuse3;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.Delegate;
import jnr.ffi.types.gid_t;
import jnr.ffi.types.mode_t;
import jnr.ffi.types.off_t;
import jnr.ffi.types.uid_t;

public final class Fuse3Callbacks {

    @FunctionalInterface
    public interface ChmodCallback {
        @Delegate
        int chmod(String path, @mode_t long mode, Pointer fi);
    }

    @FunctionalInterface
    public interface ChownCallback {
        @Delegate
        int chown(String path, @uid_t long uid, @gid_t long gid, Pointer fi);
    }

    @FunctionalInterface
    public interface GetAttrCallback {
        @Delegate
        int getattr(String path, Pointer stbuf, Pointer fi);
    }

    @FunctionalInterface
    public interface InitCallback {

        @Delegate
        Pointer init(Pointer conn, Pointer cfg);
    }

    @FunctionalInterface
    public interface ReaddirCallback {
        @Delegate
        int readdir(String path, Pointer buf, Pointer filter, @off_t long offset, Pointer fi, int flags);
    }

    @FunctionalInterface
    public interface RenameCallback {
        @Delegate
        int rename(String oldpath, String newpath, int flags);
    }

    @FunctionalInterface
    public interface TruncateCallback {
        @Delegate
        int truncate(String path, @off_t long size, Pointer fi);
    }

    @FunctionalInterface
    public interface UtimensCallback {
        @Delegate
        int utimens(String path, Pointer timespec, Pointer fi);
    }

    private Fuse3Callbacks() {
    }
}
