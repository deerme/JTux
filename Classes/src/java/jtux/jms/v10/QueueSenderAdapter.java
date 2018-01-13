/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class QueueSenderAdapter extends MessageProducerAdapter
    implements QueueSender
{
    private QueueSender queueSender;

    QueueSenderAdapter(QueueSender queueSender)
    {
	super(queueSender);

	this.queueSender = queueSender;
    }

    public Queue getQueue() throws JMSException
    {
	return queueSender.getQueue();
    }

    public void send(Message message) throws JMSException
    {
	queueSender.send(MessageWrapper.unwrap(message));
    }

    public void send(Message message, int deliveryMode, int priority,
	long timeToLive) throws JMSException
    {
	queueSender.send(MessageWrapper.unwrap(message), deliveryMode,
	    priority, timeToLive);
    }

    public void send(Queue queue, Message message) throws JMSException
    {
	queueSender.send(queue, MessageWrapper.unwrap(message));
    }

    public void send(Queue queue, Message message, int deliveryMode,
	int priority, long timeToLive) throws JMSException
    {
	queueSender.send(queue, MessageWrapper.unwrap(message),
	    deliveryMode, priority, timeToLive);
    }

    // JMS 1.1 methods

    public Destination getDestination() throws JMSException
    {
	return getQueue();
    }

    public void send(Destination dest, Message message) throws JMSException
    {
	if ((dest == null) || (dest instanceof Queue)) {
	    send((Queue) dest, message);
	} else {
	    throw new UnsupportedOperationException("Only Queue supported");
	}
    }

    public void send(Destination dest, Message message, int deliveryMode,
	int priority, long timeToLive) throws JMSException
    {
	if ((dest == null) || (dest instanceof Queue)) {
	    send((Queue) dest, message, deliveryMode, priority, timeToLive);
	} else {
	    throw new UnsupportedOperationException("Only Queue supported");
	}
    }
}
