/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

abstract class MessageProducerAdapter extends Adapter
    implements MessageProducer
{
    private MessageProducer messageProducer;

    MessageProducerAdapter(MessageProducer messageProducer)
    {
	super(messageProducer);

	this.messageProducer = messageProducer;
    }

    public void setDisableMessageID(boolean value) throws JMSException
    {
	messageProducer.setDisableMessageID(value);
    }

    public boolean getDisableMessageID() throws JMSException
    {
	return messageProducer.getDisableMessageID();
    }

    public void setDisableMessageTimestamp(boolean value) throws JMSException
    {
	messageProducer.setDisableMessageTimestamp(value);
    }

    public boolean getDisableMessageTimestamp() throws JMSException
    {
	return messageProducer.getDisableMessageTimestamp();
    }

    public void setDeliveryMode(int deliveryMode) throws JMSException
    {
	messageProducer.setDeliveryMode(deliveryMode);
    }

    public int getDeliveryMode() throws JMSException
    {
	return messageProducer.getDeliveryMode();
    }

    public void setPriority(int priority) throws JMSException
    {
	messageProducer.setPriority(priority);
    }

    public int getPriority() throws JMSException
    {
	return messageProducer.getPriority();
    }

    public void setTimeToLive(long timeToLive) throws JMSException
    {
	messageProducer.setTimeToLive(timeToLive);
    }

    public long getTimeToLive() throws JMSException
    {
	return messageProducer.getTimeToLive();
    }

    public void close() throws JMSException
    {
	messageProducer.close();
    }
}
