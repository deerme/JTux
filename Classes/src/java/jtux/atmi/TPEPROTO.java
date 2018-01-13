/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an ATMI system call was executed in the wrong context.
 */
public class TPEPROTO extends TPException
{
    TPEPROTO()
    {
	super(ATMI.TPEPROTO);
    }
}
