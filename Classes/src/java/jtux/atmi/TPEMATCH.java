/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if an attempt was made to reconfigure an existing resource.
 */
public class TPEMATCH extends TPException
{
    TPEMATCH()
    {
	super(ATMI.TPEMATCH);
    }
}
