/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

class MessageWrapper
{
    private MessageWrapper() { /* Hide default constructor */ }

    static Message wrap(Message message)
    {
	if ((message != null) && (message instanceof BytesMessage)) {
	    return BytesMessageAdapter.newInstance((BytesMessage) message);
	} else {
	    return message;
	}
    }

    /*
     * In theory, there is no need for an unwrap() method because section
     * 3.12 of the JMS spec mandates the following:
     *
     *    "A provider must be prepared to accept, from a client, a message
     *    whose implementation is *not* one of its own. A message with a
     *    'foreign' implementation may not be handled as efficiently as a
     *    provider's own implementation; however, it must be handled."
     *
     * This would imply that that the a provider must be able to handle our
     * proxied Message instances directly and we don't need to extract
     * the provider's native message from it. This doesn't work in practice,
     * unfortunately. For example, WebSphere MQ ignores the JMSReplyTo
     * field when we send an instance of our proxy rather than the wrapped
     * native message. The WebSphere MQ documentation states that a JMSReplyTo
     * that is set to anything other than a WebShere MQ queue destination is
     * ignored so perhaps WebSphere MQ automatically assumes that JMSReplyTo
     * is not a WebSphere MQ destination if the message is not a native
     * WebShpere MQ message. In any case, we need to unwrap the proxy to
     * make it work. But even if it would work in practice, it may still be
     * better to unwrap the proxy as, in general, native messages can be
     * handled more efficiently by JMS providers than foreign ones.
     */

    static Message unwrap(Message message)
    {
	if ((message != null) && (message instanceof Proxy)) {
	    Proxy proxy = (Proxy) message;
	    InvocationHandler handler = Proxy.getInvocationHandler(proxy);
	    if (handler instanceof BytesMessageAdapter) { 
		BytesMessageAdapter adapter = (BytesMessageAdapter) handler;
		return adapter.getAdaptedBytesMessage();
	    } else {
		return message; // Not our Proxy => return as is
	    }
	} else {
	    return message;
	}
    }
}
