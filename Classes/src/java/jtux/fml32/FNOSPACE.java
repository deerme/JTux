/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

/**
 * Thrown if there is no space for adding a field to an FML32 buffer.
 */
public class FNOSPACE extends FException
{
    FNOSPACE()
    {
	super(FML32.FNOSPACE);
    }
}
