/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a conversation was terminated abortively.
 */
public class TPEV_DISCONIMM extends TPEEVENT
{
    TPEV_DISCONIMM()
    {
	super(ATMI.TPEV_DISCONIMM);
    }
}
