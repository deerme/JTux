/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class XATopicConnectionFactoryAdapter
    extends TopicConnectionFactoryAdapter implements XATopicConnectionFactory
{
    private XATopicConnectionFactory xatcf;

    public XATopicConnectionFactoryAdapter(XATopicConnectionFactory xatcf)
    {
	super(xatcf);

	this.xatcf = xatcf;
    }

    public XATopicConnection createXATopicConnection() throws JMSException
    {
	return new XATopicConnectionAdapter(xatcf.createXATopicConnection());
    }

    public XATopicConnection createXATopicConnection(String userName,
	String password) throws JMSException
    {
	return new XATopicConnectionAdapter(xatcf.createXATopicConnection(
	    userName, password));
    }

    public XAConnection createXAConnection() throws JMSException
    {
	return createXATopicConnection();
    }

    public XAConnection createXAConnection(String userName, String password)
	throws JMSException
    {
	return createXATopicConnection(userName, password);
    }
}
