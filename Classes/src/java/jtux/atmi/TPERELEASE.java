/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a Tuxedo version mismatch was detected.
 */
public class TPERELEASE extends TPException
{
    TPERELEASE()
    {
	super(ATMI.TPERELEASE);
    }
}
