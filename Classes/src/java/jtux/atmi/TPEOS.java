/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an OS level system error occurred.
 */
public class TPEOS extends TPException
{
    TPEOS()
    {
	super(ATMI.TPEOS);
    }
}
