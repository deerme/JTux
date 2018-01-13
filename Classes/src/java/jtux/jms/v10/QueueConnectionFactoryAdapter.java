/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class QueueConnectionFactoryAdapter extends Adapter
    implements QueueConnectionFactory
{
    private QueueConnectionFactory qcf;

    public QueueConnectionFactoryAdapter(QueueConnectionFactory qcf)
    {
	super(qcf);

	this.qcf = qcf;
    }

    public QueueConnection createQueueConnection() throws JMSException
    {
	return new QueueConnectionAdapter(qcf.createQueueConnection());
    }

    public QueueConnection createQueueConnection(String userName,
	String password) throws JMSException
    {
	return new QueueConnectionAdapter(qcf.createQueueConnection(userName,
	    password));
    }

    public Connection createConnection() throws JMSException
    {
	return createQueueConnection();
    }

    public Connection createConnection(String userName, String password)
	throws JMSException
    {
	return createQueueConnection(userName, password);
    }
}
