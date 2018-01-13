/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a Tuxedo level system error occurred.
 */
public class TPESYSTEM extends TPException
{
    TPESYSTEM()
    {
	super(ATMI.TPESYSTEM);
    }
}
