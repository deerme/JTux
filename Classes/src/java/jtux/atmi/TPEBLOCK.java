/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an ATMI system call would block.
 */
public class TPEBLOCK extends TPException
{
    TPEBLOCK()
    {
	super(ATMI.TPEBLOCK);
    }
}
