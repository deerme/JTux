/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a message available for dequeuing is in use  by another
 * transaction.
 */
public class QMEINUSE extends TPEDIAGNOSTIC
{
    QMEINUSE()
    {
	super(ATMI.QMEINUSE);
    }
}
