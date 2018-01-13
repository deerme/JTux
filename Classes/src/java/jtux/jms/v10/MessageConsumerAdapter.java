/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

import java.io.Serializable;

public abstract class MessageConsumerAdapter extends Adapter
    implements MessageConsumer
{
    private MessageConsumer consumer;

    MessageConsumerAdapter(MessageConsumer consumer)
    {
	super(consumer);

	this.consumer = consumer;
    }

    public String getMessageSelector() throws JMSException
    {
	return consumer.getMessageSelector();
    }

    public MessageListener getMessageListener() throws JMSException
    {
	return MessageListenerAdapter.unwrap(consumer.getMessageListener());
    }

    public void setMessageListener(MessageListener listener)
	throws JMSException
    {
	consumer.setMessageListener(MessageListenerAdapter.wrap(listener));
    }

    public Message receive() throws JMSException
    {
	return MessageWrapper.wrap(consumer.receive());
    }

    public Message receive(long timeout) throws JMSException
    {
	return MessageWrapper.wrap(consumer.receive(timeout));
    }

    public Message receiveNoWait() throws JMSException
    {
	return MessageWrapper.wrap(consumer.receiveNoWait());
    }

    public void close() throws JMSException
    {
	consumer.close();
    }
}
