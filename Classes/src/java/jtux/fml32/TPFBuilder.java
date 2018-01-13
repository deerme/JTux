/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

import jtux.atmi.ATMI;

import java.nio.ByteBuffer;

/**
 * Utility class for populating Tuxedo typed buffers of type FML32.
 *
 * @remarks
 * This class provides an implementation of the abstract
 * {@link jtux.fml32.FBuilder#realloc} method for Tuxedo typed buffers.
 * Applications do not need to instantiate this class but can directly
 * use the singleton referenced by the {@link #I} variable.
 * This is illustrated by the following code fragment.
 * <pre>
 * ByteBufferHolder fbfrRef = new ByteBufferHolder();
 * fbfrRef.value = ATMI.tpalloc("FML32", null, 1024);
 * try {
 *     TPFBuilder.I.FaddString(fbfrRef, ...);
 *     TPFBuilder.I.FaddInt(fbfrRef, ...);
 *     ...
 * } finally {
 *     ATMI.tpfree(fbfrRef.value);
 * }
 * </pre>
 */
public class TPFBuilder extends FBuilder
{
    private TPFBuilder() 
    {
    }

    /**
     * Reallocates the given buffer using {@link jtux.atmi.ATMI#tprealloc}.
     */
    public ByteBuffer realloc(ByteBuffer fbfr, int newSize)
    {
	return ATMI.tprealloc(fbfr, newSize);
    }

    /**
     * Singleton TPFBuilder for direct use.
     */
    public final static TPFBuilder I = new TPFBuilder();
}
