/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an invalid Tuxedo/Q message identifier was specified.
 */
public class QMEBADMSGID extends TPEDIAGNOSTIC
{
    QMEBADMSGID()
    {
	super(ATMI.QMEBADMSGID);
    }
}
