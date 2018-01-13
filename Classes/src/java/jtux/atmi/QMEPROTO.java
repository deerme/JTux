/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a Tuxedo/Q operation was executed in the wrong context.
 */
public class QMEPROTO extends TPEDIAGNOSTIC
{
    QMEPROTO()
    {
	super(ATMI.QMEPROTO);
    }
}
