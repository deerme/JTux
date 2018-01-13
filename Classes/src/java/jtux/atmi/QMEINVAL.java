/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an illegal argument was passed to a Tuxedo/Q system call.
 */
public class QMEINVAL extends TPEDIAGNOSTIC
{
    QMEINVAL()
    {
	super(ATMI.QMEINVAL);
    }
}
