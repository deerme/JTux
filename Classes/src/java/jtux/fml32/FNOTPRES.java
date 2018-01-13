/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if a requested field is not present in an FML32 buffer.
 */
public class FNOTPRES extends FException
{
    FNOTPRES()
    {
	super(FML32.FNOTPRES);
    }
}
