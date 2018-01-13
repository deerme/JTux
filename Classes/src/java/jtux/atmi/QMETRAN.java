/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if there was an error starting a Tuxedo/Q transaction.
 */
public class QMETRAN extends TPEDIAGNOSTIC
{
    QMETRAN()
    {
	super(ATMI.QMETRAN);
    }
}
