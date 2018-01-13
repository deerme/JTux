/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class XATopicConnectionAdapter extends TopicConnectionAdapter
    implements XATopicConnection
{
    private XATopicConnection xaTopicConnection;

    XATopicConnectionAdapter(XATopicConnection xaTopicConnection)
    {
	super(xaTopicConnection);

	this.xaTopicConnection = xaTopicConnection;
    }

    public TopicSession createTopicSession(boolean transacted, int ackMode)
	throws JMSException
    {
	TopicSession topicSession = xaTopicConnection.createTopicSession(
	    transacted, ackMode);
	return new TopicSessionAdapter(topicSession, transacted, ackMode);
    }

    public XATopicSession createXATopicSession() throws JMSException
    {
	XATopicSession xaTopicSession
	    = xaTopicConnection.createXATopicSession();
	return new XATopicSessionAdapter(xaTopicSession);
    }

    public Session createSession(boolean transacted, int ackMode)
	throws JMSException
    {
	return createTopicSession(transacted, ackMode);
    }

    public XASession createXASession() throws JMSException
    {
	return createXATopicSession();
    }
}
