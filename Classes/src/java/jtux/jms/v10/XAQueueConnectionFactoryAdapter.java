/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class XAQueueConnectionFactoryAdapter
    extends QueueConnectionFactoryAdapter implements XAQueueConnectionFactory
{
    private XAQueueConnectionFactory xaqcf;

    public XAQueueConnectionFactoryAdapter(XAQueueConnectionFactory xaqcf)
    {
	super(xaqcf);

	this.xaqcf = xaqcf;
    }

    public XAQueueConnection createXAQueueConnection() throws JMSException
    {
	return new XAQueueConnectionAdapter(xaqcf.createXAQueueConnection());
    }

    public XAQueueConnection createXAQueueConnection(String userName,
	String password) throws JMSException
    {
	return new XAQueueConnectionAdapter(xaqcf.createXAQueueConnection(
	    userName, password));
    }

    public XAConnection createXAConnection() throws JMSException
    {
	return createXAQueueConnection();
    }

    public XAConnection createXAConnection(String userName, String password)
	throws JMSException
    {
	return createXAQueueConnection(userName, password);
    }
}
