/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

import javax.transaction.xa.XAResource;

public class XATopicSessionAdapter extends TopicSessionAdapter
    implements XATopicSession
{
    private XATopicSession xaTopicSession;

    public XATopicSessionAdapter(XATopicSession xaTopicSession)
	throws JMSException
    {
	super(xaTopicSession.getTopicSession(), true, 0);

	this.xaTopicSession = xaTopicSession;
    }

    public XAResource getXAResource()
    {
	return xaTopicSession.getXAResource();
    }

    public TopicSession getTopicSession() throws JMSException
    {
	return this;
    }

    public Session getSession() throws JMSException
    {
	return this;
    }
}
