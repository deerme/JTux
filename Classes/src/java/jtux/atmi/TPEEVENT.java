/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a conversational event occurred.
 *
 * @remarks
 * A TPEEVENT carries an ATMI event code that identifies the type of
 * event that occurred.
 * The symbolic constants for these event codes are defined in the
 * ATMI class. 
 */
public class TPEEVENT extends TPException
{
    private int event;

    TPEEVENT(int event)
    {
	super(ATMI.TPEEVENT);

	this.event = event;
    }

    /**
     * @return
     * The ATMI event code that identifies the type of event that
     * occurred.
     */
    public int getEvent()
    {
	return event;
    }

    private String getSymbolicName()
    {
	switch (event) {
	    case ATMI.TPEV_SENDONLY:
		return "TPEV_SENDONLY";
	    case ATMI.TPEV_SVCSUCC:
		return "TPEV_SVCSUCC";
	    case ATMI.TPEV_SVCFAIL:
		return "TPEV_SVCFAIL";
	    case ATMI.TPEV_SVCERR:
		return "TPEV_SVCERR";
	    case ATMI.TPEV_DISCONIMM:
		return "TPEV_DISCONIMM";
	    default:
		return Integer.toString(event);
	}
    }

    /**
     * @return
     * A short message that describes the type of event that occurred.
     */ 
    public String getMessage()
    {
	return super.getMessage() + ": " + getSymbolicName();
    }
}
