/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a Tuxedo/Q resource was accessed that is in exclusive
 * use by another application.
 */
public class QMESHARE extends TPEDIAGNOSTIC
{
    QMESHARE()
    {
	super(ATMI.QMESHARE);
    }
}
