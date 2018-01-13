/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class QueueConnectionAdapter extends ConnectionAdapter
    implements QueueConnection
{
    private QueueConnection queueConnection;

    QueueConnectionAdapter(QueueConnection queueConnection)
    {
	super(queueConnection);

	this.queueConnection = queueConnection;
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic,
	String subscriptionName, String messageSelector, 
	ServerSessionPool sessionPool, int maxMessages) 
    {
	throw new UnsupportedOperationException();
    }

    public ConnectionConsumer createConnectionConsumer(Destination dest,
	String messageSelector, ServerSessionPool sessionPool,
	int maxMessages) throws JMSException
    {
	if (dest instanceof Queue) {
	    return createConnectionConsumer((Queue) dest, messageSelector,
		sessionPool, maxMessages);
	}
	throw new UnsupportedOperationException("Not a Queue destination");
    }

    public Session createSession(boolean transacted, int ackMode)
	throws JMSException
    {
	return createQueueSession(transacted, ackMode);
    }

    public ConnectionConsumer createConnectionConsumer(Queue queue,
	String messageSelector, ServerSessionPool sessionPool,
	int maxMessages) throws JMSException
    {
	return queueConnection.createConnectionConsumer(queue,
	    messageSelector, new ReverseServerSessionPoolAdapter(sessionPool),
	    maxMessages);
    }

    public QueueSession createQueueSession(boolean transacted, int ackMode)
	throws JMSException
    {
	QueueSession queueSession = createQueueSession(transacted, ackMode);
	return new QueueSessionAdapter(queueSession, transacted, ackMode);
    }
}
