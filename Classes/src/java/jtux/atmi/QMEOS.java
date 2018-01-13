/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an OS level system error occurred during a Tuxedo/Q operation.
 */
public class QMEOS extends TPEDIAGNOSTIC
{
    QMEOS()
    {
	super(ATMI.QMEOS);
    }
}
