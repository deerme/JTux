/*
 * Copyright (c) 2003 Cansulta Oy. All rights reserved.
 */

package jtux.xa;

import jtux.TUX;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.transaction.xa.XAException;

import java.beans.Statement;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;
import java.util.Enumeration;

public class XAUtil
{
    public static XAException createXAException(int errorCode,
	String message, Throwable cause)
    {
	XAException eXA = new XAException(message);
	eXA.errorCode = errorCode;
	if (cause != null) {
	    eXA.initCause(cause);
	}
	return eXA;
    }

    public static XAException createXAException(int errorCode, String message)
    {
	return createXAException(errorCode, message, null);
    }

    private static int indexOfSeparator(String s, char separator, int fromPos)
    {
	while (true) {
	    int sepPos = s.indexOf(separator, fromPos);
	    if ((sepPos < 0) || (sepPos == s.length() - 1)
		    || (s.charAt(sepPos + 1) != separator)) {
		return sepPos;
	    }
	    fromPos = sepPos + 2;
	}
    }

    public static void parsePropertyString(String propertyString,
	char separator, Properties properties) throws XAException
    {
	String sepString = new String(new char[] { separator });

	int fromPos = 0;
	while (true) {
	    int sepPos = indexOfSeparator(propertyString, separator, fromPos);
	    String keyValue = (sepPos == -1)
		? propertyString.substring(fromPos).trim()
		: propertyString.substring(fromPos, sepPos).trim();
	    keyValue = keyValue.replaceAll(sepString + sepString, sepString);
	    int eqPos = keyValue.indexOf('=');
	    if (eqPos == -1) {
		throw createXAException(XAException.XAER_INVAL,
		    "Missing = in key/value pair '" + keyValue + "'");
	    }
	    String key = keyValue.substring(0, eqPos).trim();
	    String value = keyValue.substring(eqPos + 1).trim();
	    if (value.startsWith("$")) {
		String envarName = value.substring(1);
		value = TUX.tuxgetenv(envarName);
		if (value == null) {
		    throw createXAException(XAException.XAER_INVAL,
			"Environment variable '" + envarName + "' referenced "
			+ "by property '" + key + "' not set");
		}
	    }
	    properties.setProperty(key, value);
	    if (sepPos == -1) {
		break;
	    }
	    fromPos = sepPos + 1;
	}
    }

    public static void parsePropertyString(String propertyString,
	Properties properties) throws XAException
    {
	parsePropertyString(propertyString, ',', properties);
    }

    public static void parseConnectString(String connectString,
	Properties properties) throws XAException
    {
	if (connectString.startsWith("@")) {
	    String fileName = connectString.substring(1);
	    try {
	        FileInputStream fileStream = new FileInputStream(fileName);
		try {
		    properties.load(fileStream);
		} finally {
		    fileStream.close();
		}
	    } catch (IOException eIO) {
		throw XAUtil.createXAException(XAException.XAER_INVAL,
		    "Error reading properties file '" + fileName + "'", eIO);
	    }
	} else {
	    parsePropertyString(connectString, properties);
	}
    }

    public static String getMandatoryProperty(Properties properties,
	String propertyName) throws XAException
    {
	String propertyValue = properties.getProperty(propertyName);
	if (propertyValue == null) {
	    throw createXAException(XAException.XAER_INVAL,
		"Mandatory property '" + propertyName + "' not set");
	}
	return propertyValue;
    }

    public static Object createBean(String beanClassName) throws XAException
    {
	try {
	    Class beanClass = Class.forName(beanClassName);
	    try {
	        return beanClass.newInstance();
	    } catch (IllegalAccessException eIllegalAccess) {
		throw new RuntimeException(eIllegalAccess);
	    } catch (InstantiationException eInstantiation) {
		throw new RuntimeException(eInstantiation);
	    }
	} catch (ClassNotFoundException eClassNotFound) {
	    throw createXAException(XAException.XAER_INVAL, "Bean class '"
		+ beanClassName + "' not found");
	}
    }

    private static Class[] stringClass = { String.class };

    private static Class[] intClass = { Integer.TYPE };

    private static Class[] booleanClass = { Boolean.TYPE };

    public static void setBeanProperty(Object bean, String key, String value)
	throws XAException
    {
	String setterMethodName = "set" + Character.toUpperCase(key.charAt(0))
	    + key.substring(1);

	Class cls = bean.getClass();

	Object[] arguments = new Object[1];

	try {
	    cls.getMethod(setterMethodName, stringClass);
	    arguments[0] = value;
	} catch (NoSuchMethodException eNoSuchMethodString) {
	    try {
		cls.getMethod(setterMethodName, intClass);
		try {
		    arguments[0] = Integer.valueOf(value);
		} catch (NumberFormatException eNumberFormat) {
		    throw createXAException(XAException.XAER_INVAL,
			"Invalid value '" + value + "' for numeric property '"
			+ key + "'");
		}
	    } catch (NoSuchMethodException eNoSuchMethodInt) {
		try {
		    cls.getMethod(setterMethodName, booleanClass);
		    arguments[0] = Boolean.valueOf(value);
		} catch (NoSuchMethodException eNoSuchMethodBoolean) {
		    throw createXAException(XAException.XAER_INVAL,
			"No String, int or boolean property '" + key
			+ "' in bean class '" + cls.getName() + "'");
		}
	    }
	}

	Statement statement = new Statement(bean, setterMethodName,
	    arguments);

	try {
	    statement.execute();
	} catch (Exception eAny) {
	    // This must be a bug => don't translate to XAException
	    throw new RuntimeException("Error executing " + statement, eAny);
	}
    }

    private static Method getPropertySetter(Object bean, 
	String propertySetterName) throws XAException
    {
	Class cls = bean.getClass();
	try {
	    Class[] paramTypes = { String.class, String.class };
	    return cls.getMethod(propertySetterName, paramTypes);
	} catch (NoSuchMethodException eNoSuchMethodBoolean) {
	    throw createXAException(XAException.XAER_INVAL,
		"No property setter method '" + propertySetterName
		+ "' in bean class '" + cls.getName() + "'");
	}
    }

    private static void setBeanProperty(Object bean, Method propertySetter,
	String key, String value) throws XAException
    {
	try {
	    propertySetter.invoke(bean, new Object[] { key, value });
	} catch (IllegalAccessException eIllegalAccess) {
	    throw createXAException(XAException.XAER_RMERR, "Cannot access " 
		+ propertySetter, eIllegalAccess);
	} catch (InvocationTargetException eInvocationTarget) {
	    throw createXAException(XAException.XAER_INVAL, "Error setting "
		+ "property '" + key + "' to value '" + value + "'",
		eInvocationTarget.getTargetException());
	}
    }

    public static void setBeanProperties(Object bean, Properties properties)
	throws XAException
    {
	String propertySetterName = properties.getProperty("propertySetter");
	if (propertySetterName == null) {
	    Enumeration keys = properties.propertyNames();
	    while (keys.hasMoreElements()) {
		String key = (String) keys.nextElement();
		String value = properties.getProperty(key);
		setBeanProperty(bean, key, value);
  	    }
	} else {
	    properties.remove("propertySetter");
	    Method propertySetter = getPropertySetter(bean, 
		propertySetterName);
	    Enumeration keys = properties.propertyNames();
	    while (keys.hasMoreElements()) {
		String key = (String) keys.nextElement();
		String value = properties.getProperty(key);
		setBeanProperty(bean, propertySetter, key, value);
	    }
	}
    }

    public static Object jndiLookup(String objectName) throws XAException
    {
	try {
	    InitialContext initialContext = new InitialContext();
	    try {
	        return initialContext.lookup(objectName);
	    } finally {
		initialContext.close();
	    }
	} catch (NamingException eNaming) {
	    throw createXAException(XAException.XAER_INVAL, "JNDI lookup of"
		+ " object name '" + objectName + "' failed", eNaming);
	}
    }
}
