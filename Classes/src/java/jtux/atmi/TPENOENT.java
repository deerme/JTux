/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a named system resource does not exist.
 */
public class TPENOENT extends TPException
{
    TPENOENT()
    {
	super(ATMI.TPENOENT);
    }
}
