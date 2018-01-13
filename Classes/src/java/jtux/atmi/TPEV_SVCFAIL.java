/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a conversational service routine returned an application level
 * service failure.
 */
public class TPEV_SVCFAIL extends TPEEVENT
{
    TPEV_SVCFAIL()
    {
	super(ATMI.TPEV_SVCFAIL);
    }
}
