/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

/**
 * Adapts a JMS 1.1 ServerSession to a JMS 1.0 ServerSession
 */
class ReverseServerSessionAdapter extends Adapter implements ServerSession
{
    private ServerSession serverSession;

    ReverseServerSessionAdapter(ServerSession serverSession)
    {
	super(serverSession);

	this.serverSession = serverSession;
    }

    public Session getSession() throws JMSException
    {
	SessionAdapter sessionAdapter = (SessionAdapter)
	    serverSession.getSession();

	return sessionAdapter.getAdaptedSession();
    }

    public void start() throws JMSException
    {
	serverSession.start();
    }
}
