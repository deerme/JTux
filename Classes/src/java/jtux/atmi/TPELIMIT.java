/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a system resource limit was exceeded.
 */
public class TPELIMIT extends TPException
{
    TPELIMIT()
    {
	super(ATMI.TPELIMIT);
    }
}
