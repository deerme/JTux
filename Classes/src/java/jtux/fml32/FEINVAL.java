/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if an invalid argument was passed to an FML32 system call.
 */
public class FEINVAL extends FException
{
    FEINVAL()
    {
	super(FML32.FEINVAL);
    }
}
