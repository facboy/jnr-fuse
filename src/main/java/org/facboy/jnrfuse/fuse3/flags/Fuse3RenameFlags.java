package org.facboy.jnrfuse.fuse3.flags;

import jnr.ffi.util.EnumMapper;

public enum Fuse3RenameFlags implements EnumMapper.IntegerEnum  {

    RENAME_NOREPLACE(1 << 0),

    RENAME_EXCHANGE(1 << 1),

    /**
     * JNR does not work without null value enum
     */
    NULL_VALUE(0);

    private final int value;

    Fuse3RenameFlags(int value) {
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
