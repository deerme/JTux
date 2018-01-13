/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class TopicConnectionAdapter extends ConnectionAdapter
    implements TopicConnection
{
    private TopicConnection topicConnection;

    TopicConnectionAdapter(TopicConnection topicConnection)
    {
	super(topicConnection);

	this.topicConnection = topicConnection;
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic,
	String subscriptionName, String messageSelector, 
	ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
	return topicConnection.createDurableConnectionConsumer(topic,
	    subscriptionName, messageSelector,
	    new ReverseServerSessionPoolAdapter(sessionPool), maxMessages);
    }

    public ConnectionConsumer createConnectionConsumer(Destination dest,
	String messageSelector, ServerSessionPool sessionPool,
	int maxMessages) throws JMSException
    {
	if (dest instanceof Topic) {
	    return createConnectionConsumer((Topic) dest, messageSelector,
		sessionPool, maxMessages);
	}
	throw new UnsupportedOperationException("Not a Topic destination");
    }

    public Session createSession(boolean transacted, int ackMode)
	throws JMSException
    {
	return createTopicSession(transacted, ackMode);
    }

    public ConnectionConsumer createConnectionConsumer(Topic topic,
	String messageSelector, ServerSessionPool sessionPool,
	int maxMessages) throws JMSException
    {
	return topicConnection.createConnectionConsumer(topic,
	    messageSelector, new ReverseServerSessionPoolAdapter(sessionPool),
	    maxMessages);
    }

    public TopicSession createTopicSession(boolean transacted, int ackMode)
	throws JMSException
    {
	TopicSession topicSession = createTopicSession(transacted, ackMode);
	return new TopicSessionAdapter(topicSession, transacted, ackMode);
    }
}
