/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a transaction could have been heuristically completed.
 */
public class TPEHAZARD extends TPException
{
    TPEHAZARD()
    {
	super(ATMI.TPEHAZARD);
    }
}
