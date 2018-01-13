/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if there was no Tuxedo/Q message available for dequeuing.
 */
public class QMENOMSG extends TPEDIAGNOSTIC
{
    QMENOMSG()
    {
	super(ATMI.QMENOMSG);
    }
}
