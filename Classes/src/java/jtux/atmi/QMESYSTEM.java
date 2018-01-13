/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a Tuxedo level system error occurred during a Tuxedo/Q operation.
 */
public class QMESYSTEM extends TPEDIAGNOSTIC
{
    QMESYSTEM()
    {
	super(ATMI.QMESYSTEM);
    }
}
