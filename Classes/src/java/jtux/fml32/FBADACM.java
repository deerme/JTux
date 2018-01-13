/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if an invalid ACM was passed to an FML32 system call.
 */
public class FBADACM extends FException
{
    FBADACM()
    {
	super(FML32.FBADACM);
    }
}
