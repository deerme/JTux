/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if an OS level system error occurred.
 */
public class FEUNIX extends FException
{
    FEUNIX()
    {
	super(FML32.FEUNIX);
    }
}
