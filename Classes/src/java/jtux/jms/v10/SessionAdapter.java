/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

import java.io.Serializable;

abstract class SessionAdapter extends Adapter implements Session
{
    private Session session;

    private boolean transacted;

    private int ackMode;

    SessionAdapter(Session session, boolean transacted, int ackMode)
    {
	super(session);

	this.session = session;

	this.transacted = transacted;

	this.ackMode = ackMode;
    }

    Session getAdaptedSession() // Called by ReverseServerSessionAdapter
    {
	return session;
    }

    public BytesMessage createBytesMessage() throws JMSException
    {
	return BytesMessageAdapter.newInstance(session.createBytesMessage());
    }

    public MapMessage createMapMessage() throws JMSException
    {
	return session.createMapMessage();
    }

    public Message createMessage() throws JMSException
    {
	return session.createMessage();
    }

    public ObjectMessage createObjectMessage() throws JMSException
    {
	return session.createObjectMessage();
    }

    public ObjectMessage createObjectMessage(Serializable object)
	throws JMSException
    {
	return session.createObjectMessage();
    }

    public StreamMessage createStreamMessage() throws JMSException
    {
	return session.createStreamMessage();
    }

    public TextMessage createTextMessage() throws JMSException
    {
	return session.createTextMessage();
    }

    public TextMessage createTextMessage(String text) throws JMSException
    {
	return session.createTextMessage(text);
    }

    public boolean getTransacted() throws JMSException
    {
	return session.getTransacted();
    }

    public void commit() throws JMSException
    {
	session.commit();
    }

    public void rollback() throws JMSException
    {
	session.rollback();
    }

    public void close() throws JMSException
    {
	session.close();
    }

    public void recover() throws JMSException
    {
	session.recover();
    }

    public MessageListener getMessageListener() throws JMSException
    {
	return MessageListenerAdapter.unwrap(session.getMessageListener());
    }

    public void setMessageListener(MessageListener listener)
	throws JMSException
    {
	session.setMessageListener(MessageListenerAdapter.wrap(listener));
    }

    public void run()
    {
	session.run();
    }

    // New in JMS 1.1

    public int getAcknowledgeMode()
    {
	if (transacted) {
	    return 0; /* == Session.SESSION_TRANSACTED */
	} else {
	    return ackMode;
	}
    }
}
