/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown is a service routine terminated without returning a reply.
 */
public class TPESVCERR extends TPException
{
    TPESVCERR()
    {
	super(ATMI.TPESVCERR);
    }
}
