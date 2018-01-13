/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if there was an error starting a transaction.
 */
public class TPETRAN extends TPException
{
    TPETRAN()
    {
	super(ATMI.TPETRAN);
    }
}
