/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if the other end of a connection has given up control of the
 * conversation.
 */
public class TPEV_SENDONLY extends TPEEVENT
{
    TPEV_SENDONLY()
    {
	super(ATMI.TPEV_SENDONLY);
    }
}
