/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a Tuxedo/Q version mismatch was detected.
 */
public class QMERELEASE extends TPEDIAGNOSTIC
{
    QMERELEASE()
    {
	super(ATMI.QMERELEASE);
    }
}
