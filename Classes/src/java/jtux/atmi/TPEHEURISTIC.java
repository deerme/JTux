/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a transaction was partially committed and partially aborted.
 */
public class TPEHEURISTIC extends TPException
{
    TPEHEURISTIC()
    {
	super(ATMI.TPEHEURISTIC);
    }
}
