/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

import java.util.Enumeration;

public class QueueBrowserAdapter extends Adapter implements QueueBrowser
{
    private QueueBrowser browser;

    QueueBrowserAdapter(QueueBrowser browser)
    {
	super(browser);

	this.browser = browser;
    }

    public Queue getQueue() throws JMSException
    {
	return browser.getQueue();
    }

    public String getMessageSelector() throws JMSException
    {
	return browser.getMessageSelector();
    }

    static class EnumerationAdapter implements Enumeration
    {
	private Enumeration messages;

	EnumerationAdapter(Enumeration message)
	{
	    this.messages = messages;
	}

	public boolean hasMoreElements()
	{
	    return messages.hasMoreElements();
	}

	public Object nextElement()
	{
	    return MessageWrapper.wrap((Message) messages.nextElement());
	}
    }

    public Enumeration getEnumeration() throws JMSException
    {
	return new EnumerationAdapter(browser.getEnumeration());
    }

    public void close() throws JMSException
    {
	browser.close();
    }
}
