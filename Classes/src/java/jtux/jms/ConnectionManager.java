/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.jms;

import jtux.TUX;

import javax.jms.*;

import javax.transaction.xa.XAException;

import java.io.PrintWriter;

import java.util.Properties;

/**
 * @internal
 */
public class ConnectionManager
{
    public static Connection getConnection()
    {
	return JMSXAResourceAdapter.getXAConnection();
    }

    public static ConnectionConsumer createConnectionConsumer(
	Destination destination, String messageSelector,
	ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
	return getConnection().createConnectionConsumer(destination,
	    messageSelector, sessionPool, maxMessages);
    }

    public static ConnectionConsumer createDurableConnectionConsumer(
	Topic topic, String subscriptionName, String messageSelector,
	ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
	return getConnection().createDurableConnectionConsumer(topic,
	    subscriptionName, messageSelector, sessionPool, maxMessages);
    }

    public static void start() throws JMSException
    {
	getConnection().start();
    }

    public static void stop() throws JMSException
    {
	getConnection().stop();
    }

    public static String getClientID() throws JMSException
    {
	return getConnection().getClientID();
    }

    public static void setClientID(String clientID) throws JMSException
    {
	getConnection().setClientID(clientID);
    }
}
