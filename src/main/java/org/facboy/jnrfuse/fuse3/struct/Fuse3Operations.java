package org.facboy.jnrfuse.fuse3.struct;

import static ru.serce.jnrfuse.FuseCallbacks.AccessCallback;
import static ru.serce.jnrfuse.FuseCallbacks.BmapCallback;
import static ru.serce.jnrfuse.FuseCallbacks.CreateCallback;
import static ru.serce.jnrfuse.FuseCallbacks.DestroyCallback;
import static ru.serce.jnrfuse.FuseCallbacks.FallocateCallback;
import static ru.serce.jnrfuse.FuseCallbacks.FlockCallback;
import static ru.serce.jnrfuse.FuseCallbacks.FlushCallback;
import static ru.serce.jnrfuse.FuseCallbacks.FsyncCallback;
import static ru.serce.jnrfuse.FuseCallbacks.FsyncdirCallback;
import static ru.serce.jnrfuse.FuseCallbacks.GetxattrCallback;
import static ru.serce.jnrfuse.FuseCallbacks.IoctlCallback;
import static ru.serce.jnrfuse.FuseCallbacks.LinkCallback;
import static ru.serce.jnrfuse.FuseCallbacks.ListxattrCallback;
import static ru.serce.jnrfuse.FuseCallbacks.LockCallback;
import static ru.serce.jnrfuse.FuseCallbacks.MkdirCallback;
import static ru.serce.jnrfuse.FuseCallbacks.MknodCallback;
import static ru.serce.jnrfuse.FuseCallbacks.OpenCallback;
import static ru.serce.jnrfuse.FuseCallbacks.OpendirCallback;
import static ru.serce.jnrfuse.FuseCallbacks.PollCallback;
import static ru.serce.jnrfuse.FuseCallbacks.ReadCallback;
import static ru.serce.jnrfuse.FuseCallbacks.ReadbufCallback;
import static ru.serce.jnrfuse.FuseCallbacks.ReadlinkCallback;
import static ru.serce.jnrfuse.FuseCallbacks.ReleaseCallback;
import static ru.serce.jnrfuse.FuseCallbacks.ReleasedirCallback;
import static ru.serce.jnrfuse.FuseCallbacks.RemovexattrCallback;
import static ru.serce.jnrfuse.FuseCallbacks.RmdirCallback;
import static ru.serce.jnrfuse.FuseCallbacks.SetxattrCallback;
import static ru.serce.jnrfuse.FuseCallbacks.StatfsCallback;
import static ru.serce.jnrfuse.FuseCallbacks.SymlinkCallback;
import static ru.serce.jnrfuse.FuseCallbacks.UnlinkCallback;
import static ru.serce.jnrfuse.FuseCallbacks.WriteCallback;
import static ru.serce.jnrfuse.FuseCallbacks.WritebufCallback;
import jnr.ffi.BaseStruct;
import jnr.ffi.Runtime;
import jnr.posix.util.Platform;
import org.facboy.jnrfuse.fuse3.Fuse3Callbacks.ChmodCallback;
import org.facboy.jnrfuse.fuse3.Fuse3Callbacks.ChownCallback;
import org.facboy.jnrfuse.fuse3.Fuse3Callbacks.GetAttrCallback;
import org.facboy.jnrfuse.fuse3.Fuse3Callbacks.InitCallback;
import org.facboy.jnrfuse.fuse3.Fuse3Callbacks.ReaddirCallback;
import org.facboy.jnrfuse.fuse3.Fuse3Callbacks.RenameCallback;
import org.facboy.jnrfuse.fuse3.Fuse3Callbacks.TruncateCallback;
import org.facboy.jnrfuse.fuse3.Fuse3Callbacks.UtimensCallback;

/**
 * fuse_operations struct
 *
 * @author Sergey Tselovalnikov
 * @since 30.05.15
 */
public class Fuse3Operations extends BaseStruct {
    public Fuse3Operations(Runtime runtime) {
        super(runtime);
    }

    public final Func<GetAttrCallback> getattr = func(GetAttrCallback.class);
    public final Func<ReadlinkCallback> readlink = func(ReadlinkCallback.class);
    public final Func<MknodCallback> mknod = func(MknodCallback.class);
    public final Func<MkdirCallback> mkdir = func(MkdirCallback.class);
    public final Func<UnlinkCallback> unlink = func(UnlinkCallback.class);
    public final Func<RmdirCallback> rmdir = func(RmdirCallback.class);
    public final Func<SymlinkCallback> symlink = func(SymlinkCallback.class);

    public final Func<RenameCallback> rename = func(RenameCallback.class);
    public final Func<LinkCallback> link = func(LinkCallback.class);
    public final Func<ChmodCallback> chmod = func(ChmodCallback.class);
    public final Func<ChownCallback> chown = func(ChownCallback.class);
    public final Func<TruncateCallback> truncate = func(TruncateCallback.class);

    public final Func<OpenCallback> open = func(OpenCallback.class);
    public final Func<ReadCallback> read = func(ReadCallback.class);
    public final Func<WriteCallback> write = func(WriteCallback.class);
    public final Func<StatfsCallback> statfs = func(StatfsCallback.class);
    public final Func<FlushCallback> flush = func(FlushCallback.class);
    public final Func<ReleaseCallback> release = func(ReleaseCallback.class);
    public final Func<FsyncCallback> fsync = func(FsyncCallback.class);
    public final Func<SetxattrCallback> setxattr = func(SetxattrCallback.class);
    public final Func<GetxattrCallback> getxattr = func(GetxattrCallback.class);
    public final Func<ListxattrCallback> listxattr = func(ListxattrCallback.class);
    public final Func<RemovexattrCallback> removexattr = func(RemovexattrCallback.class);
    public final Func<OpendirCallback> opendir = func(OpendirCallback.class);
    public final Func<ReaddirCallback> readdir = func(ReaddirCallback.class);
    public final Func<ReleasedirCallback> releasedir = func(ReleasedirCallback.class);
    public final Func<FsyncdirCallback> fsyncdir = func(FsyncdirCallback.class);
    public final Func<InitCallback> init = func(InitCallback.class);
    public final Func<DestroyCallback> destroy = func(DestroyCallback.class);
    public final Func<AccessCallback> access = func(AccessCallback.class);
    public final Func<CreateCallback> create = func(CreateCallback.class);
    public final Func<LockCallback> lock = func(LockCallback.class);
    public final Func<UtimensCallback> utimens = func(UtimensCallback.class);
    public final Func<BmapCallback> bmap = func(BmapCallback.class);
    public final Func<IoctlCallback> ioctl = func(IoctlCallback.class);
    public final Func<PollCallback> poll = func(PollCallback.class);
    public final Func<WritebufCallback> write_buf = func(WritebufCallback.class);
    public final Func<ReadbufCallback> read_buf = func(ReadbufCallback.class);
    public final Func<FlockCallback> flock = func(FlockCallback.class);
    public final Func<FallocateCallback> fallocate = func(FallocateCallback.class);

    {
        if(Platform.IS_MAC) {
            // TODO implement MAC-OS specific functions
            for (int i = 0; i < 13; i++) {
                func(FallocateCallback.class);
            }
        }
    }
}
