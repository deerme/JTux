/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class TopicConnectionFactoryAdapter extends Adapter
    implements TopicConnectionFactory
{
    private TopicConnectionFactory tcf;

    public TopicConnectionFactoryAdapter(TopicConnectionFactory tcf)
    {
	super(tcf);

	this.tcf = tcf;
    }

    public TopicConnection createTopicConnection() throws JMSException
    {
	return new TopicConnectionAdapter(tcf.createTopicConnection());
    }

    public TopicConnection createTopicConnection(String userName,
	String password) throws JMSException
    {
	return new TopicConnectionAdapter(tcf.createTopicConnection(userName,
	    password));
    }

    public Connection createConnection() throws JMSException
    {
	return createTopicConnection();
    }

    public Connection createConnection(String userName, String password)
	throws JMSException
    {
	return createTopicConnection(userName, password);
    }
}
