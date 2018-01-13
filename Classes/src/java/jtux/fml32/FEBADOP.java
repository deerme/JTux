/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if a buffer containing an unsupported field type was passed to
 * an FML32 system call.
 */
public class FEBADOP extends FException
{
    FEBADOP()
    {
	super(FML32.FEBADOP);
    }
}
