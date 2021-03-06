package ru.serce.jnrfuse;

import jnr.ffi.Pointer;
import jnr.ffi.types.gid_t;
import jnr.ffi.types.mode_t;
import jnr.ffi.types.off_t;
import jnr.ffi.types.uid_t;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;
import ru.serce.jnrfuse.struct.Timespec;


/**
 * Fuse file system.
 * All documentation from "fuse.h"
 *
 * @author Sergey Tselovalnikov
 * @see <fuse.h>
 * <p>
 * Most of these should work very similarly to the well known UNIX
 * file system operations.  A major exception is that instead of
 * returning an error in 'errno', the operation should return the
 * negated error value (-errno) directly.
 * <p>
 * All methods are optional, but some are essential for a useful
 * filesystem (e.g. getattr).  Open, flush, release, fsync, opendir,
 * releasedir, fsyncdir, access, create, ftruncate, fgetattr, lock,
 * init and destroy are special purpose methods, without which a full
 * featured filesystem can still be implemented.
 * <p>
 * See http://fuse.sourceforge.net/wiki/ for more information.
 * @since 27.05.15
 */
public interface FuseFS extends BaseFuseFS {

    /**
     * Change the permission bits of a file
     *
     * @param mode The argument mode specifies the permissions to use in case a  new  file
     *             is created. @see ru.serce.jnrfuse.struct.FileStat flags
     */
    int chmod(String path, @mode_t long mode);

    /**
     * Change the owner and group of a file
     */
    int chown(String path, @uid_t long uid, @gid_t long gid);

    /**
     * Change the size of an open file
     * <p>
     * This method is called instead of the truncate() method if the
     * truncation was invoked from an ftruncate() system call.
     * <p>
     * If this method is not implemented or under Linux kernel
     * versions earlier than 2.6.15, the truncate() method will be
     * called instead.
     */
    int ftruncate(String path, @off_t long size, FuseFileInfo fi);

    /**
     * Get attributes from an open file
     * <p>
     * This method is called instead of the getattr() method if the
     * file information is available.
     * <p>
     * Currently this is only called after the create() method if that
     * is implemented (see above).  Later it may be called for
     * invocations of fstat() too.
     */
    int fgetattr(String path, FileStat stbuf, FuseFileInfo fi);

    /**
     * Get file attributes.
     * <p>
     * Similar to stat().  The 'st_dev' and 'st_blksize' fields are
     * ignored.	 The 'st_ino' field is ignored except if the 'use_ino'
     * mount option is given.
     */
    int getattr(String path, FileStat stat);

    /**
     * Initialize filesystem
     * <p>
     * The return value will passed in the private_data field of
     * fuse_context to all file operations and as a parameter to the
     * destroy() method.
     */
    Pointer init(Pointer conn);

    /**
     * Read directory
     * <p>
     * This supersedes the old getdir() interface.  New applications
     * should use this.
     * <p>
     * The filesystem may choose between two modes of operation:
     * <p>
     * 1) The readdir implementation ignores the offset parameter, and
     * passes zero to the filler function's offset.  The filler
     * function will not return '1' (unless an error happens), so the
     * whole directory is read in a single readdir operation.  This
     * works just like the old getdir() method.
     * <p>
     * 2) The readdir implementation keeps track of the offsets of the
     * directory entries.  It uses the offset parameter and always
     * passes non-zero offset to the filler function.  When the buffer
     * is full (or an error happens) the filler function will return
     * '1'.
     */
    int readdir(String path, Pointer buf, FuseFillDir filter, @off_t long offset, FuseFileInfo fi);

    /**
     * Rename a file
     */
    int rename(String oldpath, String newpath);

    /**
     * Change the size of a file
     */
    int truncate(String path, @off_t long size);

    /**
     * Change the access and modification times of a file with
     * nanosecond resolution
     * <p>
     * This supersedes the old utime() interface.  New applications
     * should use this.
     * <p>
     * See the utimensat(2) man page for details.
     */
    int utimens(String path, Timespec[] timespec);
}
