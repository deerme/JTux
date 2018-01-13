/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if the Tuxedo/Q resource manager was not open.
 */
public class QMENOTOPEN extends TPEDIAGNOSTIC
{
    QMENOTOPEN()
    {
	super(ATMI.QMENOTOPEN);
    }
}
