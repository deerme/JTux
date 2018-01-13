/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a transaction could not be committed.
 */
public class TPEABORT extends TPException
{
    TPEABORT()
    {
	super(ATMI.TPEABORT);
    }
}
