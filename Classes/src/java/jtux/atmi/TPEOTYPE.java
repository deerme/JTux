/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a non-supported message type was received from a service
 * routine.
 */
public class TPEOTYPE extends TPException
{
    TPEOTYPE()
    {
	super(ATMI.TPEOTYPE);
    }
}
