/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class XAQueueConnectionAdapter extends QueueConnectionAdapter
    implements XAQueueConnection
{
    private XAQueueConnection xaQueueConnection;

    XAQueueConnectionAdapter(XAQueueConnection xaQueueConnection)
    {
	super(xaQueueConnection);

	this.xaQueueConnection = xaQueueConnection;
    }

    public QueueSession createQueueSession(boolean transacted, int ackMode)
	throws JMSException
    {
	QueueSession queueSession = xaQueueConnection.createQueueSession(
	    transacted, ackMode);
	return new QueueSessionAdapter(queueSession, transacted, ackMode);
    }

    public XAQueueSession createXAQueueSession() throws JMSException
    {
	XAQueueSession xaQueueSession
	    = xaQueueConnection.createXAQueueSession();
	return new XAQueueSessionAdapter(xaQueueSession);
    }

    public Session createSession(boolean transacted, int ackMode)
	throws JMSException
    {
	return createQueueSession(transacted, ackMode);
    }

    public XASession createXASession() throws JMSException
    {
	return createXAQueueSession();
    }
}
