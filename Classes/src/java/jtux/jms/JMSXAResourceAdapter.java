/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.jms;

import jtux.jms.v10.XAQueueConnectionFactoryAdapter;
import jtux.jms.v10.XATopicConnectionFactoryAdapter;

import jtux.xa.XAUtil;
import jtux.xa.XAInvocationLogger;

import jtux.TUX;

import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XASession;
import javax.jms.XAConnectionFactory;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.XATopicConnectionFactory;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.XAException;

import java.io.PrintWriter;

import java.util.Properties;

// JMSXAResourceAdapter is loaded by the native JTuxJTA module so this
// class can be kept package private for security reasons.

class JMSXAResourceAdapter
{
    private static XAConnection globalXAConnection = null;

    private static int sessionCount = 0;

    private static PrintWriter debug = TUX.getDebugWriter("XA");

    private static ThreadLocal threadLocalXASession = new ThreadLocal();

    static XAConnection getXAConnection() throws IllegalStateException
    {
	if (globalXAConnection == null) {
	    throw new IllegalStateException("No global XAConnection");
	}
	return globalXAConnection;
    }

    static XASession getXASession() throws IllegalStateException
    {
    	XASession xaSession = (XASession) threadLocalXASession.get();
	if (xaSession == null) {
	    throw new IllegalStateException("No XASession in this thread");
	}
	return xaSession;
    }

    private static XAConnection createXAConnection(String configString) 
	throws XAException, JMSException
    {
	if (configString == null) {
	    throw XAUtil.createXAException(XAException.XAER_INVAL,
	        "null connect string");
	}

	Properties properties = new Properties();

	XAUtil.parseConnectString(configString, properties);

	String xaConnectionFactoryClassName = properties.getProperty(
	    "XAConnectionFactoryClass");
	if (xaConnectionFactoryClassName == null) {
	    throw XAUtil.createXAException(XAException.XAER_INVAL,
	        "Config property 'XAConnectionFactoryClass' not set");
	}
	properties.remove("XAConnectionFactoryClass");

	String cfType = properties.getProperty("cfType");
	if (cfType != null) {
	    properties.remove("cfType");
	}

	String userName = properties.getProperty("userName");
	if (userName != null) {
	    properties.remove("userName");
	}

	String password = properties.getProperty("password");
	if (password != null) {
	    properties.remove("password");
	}

	XAConnectionFactory xacf = (XAConnectionFactory)
	    XAUtil.createBean(xaConnectionFactoryClassName);

	XAUtil.setBeanProperties(xacf, properties);

	if (debug != null) {
	    debug.println("Created XAConnectionFactory: " + xacf);
	}

	if (cfType != null) {
	    if (cfType.equalsIgnoreCase("queue")) {
		xacf = new XAQueueConnectionFactoryAdapter(
		    (XAQueueConnectionFactory) xacf);
	    } else if (cfType.equalsIgnoreCase("topic")) {
		xacf = new XATopicConnectionFactoryAdapter(
		    (XATopicConnectionFactory) xacf);
	    } else {
	        throw XAUtil.createXAException(XAException.XAER_INVAL,
		    "Invalid value '" + cfType + "' for config property "
		    +"'cfType' (should be 'queue' or 'topic')");
	    }
	}

	XAConnection xac;

	if (userName == null) {
	    xac = xacf.createXAConnection();
	} else {
	    if (password == null) {
		throw XAUtil.createXAException(XAException.XAER_INVAL,
		    "Config property 'password' not set");
	    }
	    xac = xacf.createXAConnection(userName, password);
	}

	if (debug != null) {
	    debug.println("Opened XAConnection: " + xac);
	}

	boolean ok = false;
	try {
	    /* This would be the appropriate place for setting the
	     * connection's clientID. This is very hard, however, as
	     * each TMS, JServer and client connection must get a
	     * separate, unique and restart-insensitive clientID.
	     */
	    xac.start();
	    ok = true;
	    return xac;
	} finally {
	    if (!ok) {
		xac.close();
	    }
	}
    }

    public static XAResource open(String configString) throws XAException
    {
	try {
	    synchronized (JMSXAResourceAdapter.class) {
		if (globalXAConnection == null) {
		    globalXAConnection = createXAConnection(configString);
		}
	    }

	    boolean ok = false;
	    XASession xaSession = globalXAConnection.createXASession();
	    try {
		if (debug != null) {
		    debug.println("Opened XASession: " + xaSession);
		}
	        threadLocalXASession.set(xaSession);
		XAResource xaResource = xaSession.getXAResource();
		if (debug != null) {
		    debug.println("Got XAResource: " + xaResource);
		    xaResource = XAInvocationLogger.wrap(xaResource);
		}

		ok = true;
		return xaResource;
	    } finally {
		if (ok) {
		    synchronized (JMSXAResourceAdapter.class) {
			sessionCount++;
		    }
		} else {
		    xaSession.close();
		}
	    }
	} catch (JMSException eJMS) {
	    throw XAUtil.createXAException(XAException.XAER_RMERR,
		"Error opening XA connection", eJMS);
	}
    }

    public static void close() throws XAException
    {
	XASession xaSession = getXASession();
	try {
	    if (debug != null) {
		debug.println("Closing XASession: " + xaSession);
	    }
	    xaSession.close();
	} catch (JMSException eJMS) {
	    throw XAUtil.createXAException(XAException.XAER_RMERR,
		"Error closing XA session", eJMS);
	} finally {
	    threadLocalXASession.set(null);
	    synchronized (JMSXAResourceAdapter.class) {
		sessionCount--;
		if (sessionCount == 0) {
		    try {
			if (debug != null) {
			    debug.println("Closing XAConnection: "
				+ globalXAConnection);
			}
			globalXAConnection.close();
		    } catch (JMSException eJMS) {
			TUX.userlog("WARNING: Exception closing "
			    + globalXAConnection);
			eJMS.printStackTrace(TUX.ULOG);
		    } finally {
			globalXAConnection = null;
		    }
		}
	    }
	}
    }
}
