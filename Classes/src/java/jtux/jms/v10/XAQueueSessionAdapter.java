/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

import javax.transaction.xa.XAResource;

public class XAQueueSessionAdapter extends QueueSessionAdapter
    implements XAQueueSession
{
    private XAQueueSession xaQueueSession;

    public XAQueueSessionAdapter(XAQueueSession xaQueueSession)
	throws JMSException
    {
	super(xaQueueSession.getQueueSession(), true, 0);

	this.xaQueueSession = xaQueueSession;
    }

    public XAResource getXAResource()
    {
	return xaQueueSession.getXAResource();
    }

    public QueueSession getQueueSession() throws JMSException
    {
	return this;
    }

    public Session getSession() throws JMSException
    {
	return this;
    }
}
