/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if an invalid boolean expression was passed to an
 * FML32 system call.
 */
public class FSYNTAX extends FException
{
    FSYNTAX()
    {
	super(FML32.FSYNTAX);
    }
}
