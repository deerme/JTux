/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an invalid Tuxedo/Q resource manager was specified.
 */
public class QMEBADRMID extends TPEDIAGNOSTIC
{
    QMEBADRMID()
    {
	super(ATMI.QMEBADRMID);
    }
}
