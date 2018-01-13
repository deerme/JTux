/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a non-supported message type was sent to a service routine.
 */
public class TPEITYPE extends TPException
{
    TPEITYPE()
    {
	super(ATMI.TPEITYPE);
    }
}
