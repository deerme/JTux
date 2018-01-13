/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a resource manager failed to open correctly.
 */
public class TPERMERR extends TPException
{
    TPERMERR()
    {
	super(ATMI.TPERMERR);
    }
}
