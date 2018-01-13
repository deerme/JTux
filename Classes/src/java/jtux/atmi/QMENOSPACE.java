/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if there was not enough space to enqueue a Tuxedo/Q message.
 */
public class QMENOSPACE extends TPEDIAGNOSTIC
{
    QMENOSPACE()
    {
	super(ATMI.QMENOSPACE);
    }
}
