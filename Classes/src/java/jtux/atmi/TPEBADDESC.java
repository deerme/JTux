/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an invalid communication descriptor was passed to an
 * ATMI system call.
 */
public class TPEBADDESC extends TPException
{
    TPEBADDESC()
    {
	super(ATMI.TPEBADDESC);
    }
}
