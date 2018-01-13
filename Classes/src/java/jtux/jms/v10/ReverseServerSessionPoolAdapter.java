/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

/**
 * Adapts a JMS 1.1 ServerSessionPool to a JMS 1.0 ServerSessionPool
 */
class ReverseServerSessionPoolAdapter extends Adapter
    implements ServerSessionPool
{
    private ServerSessionPool serverSessionPool;

    ReverseServerSessionPoolAdapter(ServerSessionPool serverSessionPool)
    {
	super(serverSessionPool);

	this.serverSessionPool = serverSessionPool;
    }

    public ServerSession getServerSession() throws JMSException
    {
	ServerSession serverSession = serverSessionPool.getServerSession();

	return new ReverseServerSessionAdapter(serverSession);
    }
}
