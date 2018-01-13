/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if an invalid field name was passed to an FML32 system call.
 */
public class FBADNAME extends FException
{
    FBADNAME()
    {
	super(FML32.FBADNAME);
    }
}
