package org.facboy.jnrfuse.fuse3.flags;

import jnr.ffi.util.EnumMapper;

public enum Fuse3ReaddirFlags implements EnumMapper.IntegerEnum  {

    FUSE_READDIR_PLUS(1 << 0),

    /**
     * JNR does not work without null value enum
     */
    NULL_VALUE(0);

    private final int value;

    Fuse3ReaddirFlags(int value) {
        this.value = value;
    }

    /**
     * Special JNR method, see jnr.ffi.util.EnumMapper#getNumberValueMethod(java.lang.Class, java.lang.Class)
     */
    @Override
    public int intValue() {
        return value;
    }
}
