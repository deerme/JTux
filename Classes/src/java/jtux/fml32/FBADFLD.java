/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if an invalid field identifier was passed to an FML32 system call.
 */
public class FBADFLD extends FException
{
    FBADFLD()
    {
	super(FML32.FBADFLD);
    }
}
