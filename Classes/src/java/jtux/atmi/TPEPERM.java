/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if there was no permission to perform a certain operation.
 */
public class TPEPERM extends TPException
{
    TPEPERM()
    {
	super(ATMI.TPEPERM);
    }
}
