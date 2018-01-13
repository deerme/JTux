/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a timeout occurred.
 */
public class TPETIME extends TPException
{
    TPETIME()
    {
	super(ATMI.TPETIME);
    }
}
