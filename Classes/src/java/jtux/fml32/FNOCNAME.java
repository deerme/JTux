/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if the name of a C struct field could not be found.
 */
public class FNOCNAME extends FException
{
    FNOCNAME()
    {
	super(FML32.FNOCNAME);
    }
}
