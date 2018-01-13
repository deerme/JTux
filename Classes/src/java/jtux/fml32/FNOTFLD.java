/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if a non-FML32 buffer was passed to an FML32 system call.
 */
public class FNOTFLD extends FException
{
    FNOTFLD()
    {
	super(FML32.FNOTFLD);
    }
}
