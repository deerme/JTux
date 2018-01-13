/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a service routine returned an application level
 * service failure.
 */
public class TPESVCFAIL extends TPException
{
    TPESVCFAIL()
    {
	super(ATMI.TPESVCFAIL);
    }
}
