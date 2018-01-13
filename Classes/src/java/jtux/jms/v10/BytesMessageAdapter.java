/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import jtux.TUX;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

public class BytesMessageAdapter extends Adapter implements InvocationHandler
{
    // The buffer can be shared by different threads as its contents are
    // never used. The default value is quite big for performance reasons.

    private static Method getBodyLengthMethod;

    static
    {
        try {
	    getBodyLengthMethod = BytesMessage.class.getMethod(
		"getBodyLength", null);
        } catch (NoSuchMethodException eNoSuchMethod) {
	    throw new NoSuchMethodError(eNoSuchMethod.getMessage());
        }
    }

    private BytesMessage message;

    BytesMessageAdapter(BytesMessage message)
    {
	super(message);

	this.message = message;
    }

    BytesMessage getAdaptedBytesMessage()
    {
	return message;
    }

    private long getBodyLength() throws JMSException
    {
	// It is the responsibility of the caller to make sure that the
	// BytesMessage is in read-only mode when this method is called
	// as dictated by the JMS 1.1 API.

	// The implementation of this method is very tricky because we
	// need to make sure that the position of the stream is not
	// affected by the invocation of this method. Since there is
	// no way to find out what the current position of the stream
	// is, we actually need to read through the whole message twice.

        byte[] buffer = new byte[256 * 1024];
	
	// First we read from the current position to the end of the
	// message. This gives us lenMinusPosition and leaves the position
	// at the end of the stream.

	long lenMinusPosition = 0;
	while (true) {
	    int read = message.readBytes(buffer);
	    if (read == -1) {
		break;
	    }
	    lenMinusPosition = lenMinusPosition + read;
	}

	// Then we reset the message and read through the entire message.
	// This gives us len.

	message.reset();

	long len = 0;
	while (true) {
	    int read = message.readBytes(buffer);
	    if (read == -1) {
		break;
	    }
	    len = len + read;
	}

	// From len and lenMinusPosition we can determine the original
	// postion. We then reset the message again and read until the
	// position.

	long position = len - lenMinusPosition;

	message.reset();

	while (position > buffer.length) {
	    message.readBytes(buffer);
	    position = position - buffer.length;
	}
	if (position > 0) {
	    message.readBytes(buffer, (int) position);
	}

	// The message is now in the same state as before the method
	// was invoked.

	return len;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
	throws Throwable
    {
	if (method.equals(getBodyLengthMethod)) {
	    return new Long(getBodyLength());
	} else {
	    return method.invoke(message, args);
	}
    }

    static BytesMessage newInstance(BytesMessage message)
    {
	return (BytesMessage) Proxy.newProxyInstance(
	    BytesMessage.class.getClassLoader(),
	    new Class[] { BytesMessage.class },
	    new BytesMessageAdapter(message));
    }
}
