/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

/**
 * Base class for providing a JMS 1.1 Connection interface to a
 * JMS 1.0 Connection.
 */
public abstract class ConnectionAdapter extends Adapter implements Connection
{
    private Connection connection;

    ConnectionAdapter(Connection connection)
    {
	super(connection);

	this.connection = connection;
    }

    public void close() throws JMSException
    {
	connection.close();
    }

    public String getClientID() throws JMSException
    {
	return connection.getClientID();
    }

    public ExceptionListener getExceptionListener() throws JMSException
    {
	return connection.getExceptionListener();
    }

    public ConnectionMetaData getMetaData() throws JMSException
    {
	return connection.getMetaData();
    }

    public void setClientID(String clientID) throws JMSException
    {
	connection.setClientID(clientID);
    }

    public void setExceptionListener(ExceptionListener listener)
	throws JMSException
    {
	connection.setExceptionListener(listener);
    }

    public void start() throws JMSException
    {
	connection.start();
    }

    public void stop() throws JMSException
    {
	connection.stop();
    }
}
