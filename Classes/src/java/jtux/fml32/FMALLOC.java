/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if a memory allocation failure occurred.
 */
public class FMALLOC extends FException
{
    FMALLOC()
    {
	super(FML32.FMALLOC);
    }
}
