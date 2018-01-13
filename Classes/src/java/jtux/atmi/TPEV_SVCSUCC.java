/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a conversational service routine returned normally.
 */
public class TPEV_SVCSUCC extends TPEEVENT
{
    TPEV_SVCSUCC()
    {
	super(ATMI.TPEV_SVCSUCC);
    }
}
