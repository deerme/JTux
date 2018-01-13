/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an administrative Tuxedo operation failed.
 */
public class TPEMIB extends TPException
{
    TPEMIB()
    {
	super(ATMI.TPEMIB);
    }
}
