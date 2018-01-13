/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an invalid Tuxedo/Q queue name was specified.
 */
public class QMEBADQUEUE extends TPEDIAGNOSTIC
{
    QMEBADQUEUE()
    {
	super(ATMI.QMEBADQUEUE);
    }
}
