/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if an invalid field type was passed to an FML32 system call.
 */
public class FTYPERR extends FException
{
    FTYPERR()
    {
	super(FML32.FTYPERR);
    }
}
