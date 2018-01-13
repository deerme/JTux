/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.jdbc;

import jtux.xa.XAUtil;
import jtux.xa.XAInvocationLogger;

import jtux.TUX;

import javax.sql.XADataSource;
import javax.sql.XAConnection;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.XAException;

import java.sql.SQLException;

import java.io.PrintWriter;

import java.util.Properties;

// JDBCXAResourceAdapter is loaded by the native JTuxJTA module so this
// class can be kept package private for security reasons.

class JDBCXAResourceAdapter
{
    private static XADataSource xaDataSource = null;

    private static PrintWriter debug = TUX.getDebugWriter("XA");

    private static ThreadLocal threadLocalXAConnection = new ThreadLocal();

    static XAConnection getXAConnection() throws IllegalStateException
    {
    	Object xaConnection = threadLocalXAConnection.get();
	if (xaConnection == null) {
	    throw new IllegalStateException("No XAConnection in this thread");
	}
	return (XAConnection) xaConnection;
    }

    private static XADataSource createXADataSource(String configString)
	throws XAException, SQLException
    {
	if (configString == null) {
	    throw XAUtil.createXAException(XAException.XAER_INVAL,
	        "null config string");
	}

	Properties properties = new Properties();

	XAUtil.parseConnectString(configString, properties);

	String xaDataSourceClassName = properties.getProperty(
	    "XADataSourceClass");
	if (xaDataSourceClassName == null) {
	    throw XAUtil.createXAException(XAException.XAER_INVAL,
	        "Config property 'XADataSourceClass' not set");
	}
	properties.remove("XADataSourceClass");

	XADataSource dataSource = (XADataSource)
	    XAUtil.createBean(xaDataSourceClassName);

	String logTo = properties.getProperty("logTo");
	if (logTo != null) {
	    if (logTo.equals("ulog")) {
		dataSource.setLogWriter(TUX.ULOG);
	    } else {
		throw XAUtil.createXAException(XAException.XAER_INVAL,
		    "Invalid value '" + logTo + "' for config property "
		    + "'logTo' (should be 'ulog'");
	    }
	    properties.remove("logTo");
	}

	XAUtil.setBeanProperties(dataSource, properties);

	if (debug != null) {
	    debug.println("Created XADataSource: " + dataSource);
	}

	return dataSource;
    }

    public static XAResource open(String configString) throws XAException
    {
	try {
	    synchronized (JDBCXAResourceAdapter.class) {
	        if (xaDataSource == null) {
	            xaDataSource = createXADataSource(configString);
	        }
	    }

	    boolean ok = false;
	    XAConnection xaConnection = xaDataSource.getXAConnection();
	    try {
		if (debug != null) {
		    debug.println("Opened XAConnection: " + xaConnection);
		}
	        threadLocalXAConnection.set(xaConnection);
	        XAResource xaResource = xaConnection.getXAResource();
		if (debug != null) {
		    debug.println("Got XAResource: " + xaResource);
		    xaResource = XAInvocationLogger.wrap(xaResource);
		}
		ok = true;
		return xaResource;
	    } finally {
		if (!ok) {
		    xaConnection.close();
		}
	    }
	} catch (SQLException eSQL) {
	    throw XAUtil.createXAException(XAException.XAER_RMERR,
		"Error opening XA connection", eSQL);
	}
    }

    public static void close() throws XAException
    {
	XAConnection xaConnection = getXAConnection();
	try {
	    if (debug != null) {
		debug.println("Closing XAConnection: " + xaConnection);
	    }
	    xaConnection.close();
	} catch (SQLException eSQL) {
	    throw XAUtil.createXAException(XAException.XAER_RMERR,
		"Error closing XA connection", eSQL);
	} finally {
	    threadLocalXAConnection.set(null);
	}
    }
}
