/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a Tuxedo/Q operation was aborted.
 */
public class QMEABORTED extends TPEDIAGNOSTIC
{
    QMEABORTED()
    {
	super(ATMI.QMEABORTED);
    }
}
