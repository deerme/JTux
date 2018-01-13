/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an ATMI system call was interrupted by an OS signal.
 */
public class TPGOTSIG extends TPException
{
    TPGOTSIG()
    {
	super(ATMI.TPGOTSIG);
    }
}
