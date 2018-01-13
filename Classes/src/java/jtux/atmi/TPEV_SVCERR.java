/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a conversational service routine terminated without sending
 * a reply.
 */
public class TPEV_SVCERR extends TPEEVENT
{
    TPEV_SVCERR()
    {
	super(ATMI.TPEV_SVCERR);
    }
}
