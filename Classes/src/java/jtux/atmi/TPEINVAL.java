/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an illegal argument was passed to an ATMI system call.
 */
public class TPEINVAL extends TPException
{
    TPEINVAL()
    {
	super(ATMI.TPEINVAL);
    }
}
