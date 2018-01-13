/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if an incorrectly aligned buffer was passed to an FML32
 * system call.
 */
public class FALIGNERR extends FException
{
    FALIGNERR()
    {
	super(FML32.FALIGNERR);
    }
}
