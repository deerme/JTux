/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class QueueSessionAdapter extends SessionAdapter
    implements QueueSession
{
    private QueueSession queueSession;

    QueueSessionAdapter(QueueSession queueSession, boolean transacted,
	int ackMode)
    {
	super(queueSession, transacted, ackMode);

	this.queueSession = queueSession;
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException
    {
	return new QueueBrowserAdapter(queueSession.createBrowser(queue));
    }

    public QueueBrowser createBrowser(Queue queue, String messageSelector)
	throws JMSException
    {
	return new QueueBrowserAdapter(queueSession.createBrowser(queue,
	    messageSelector));
    }

    public Queue createQueue(String queueName) throws JMSException
    {
	return queueSession.createQueue(queueName);
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException
    {
	return new QueueReceiverAdapter(queueSession.createReceiver(queue));
    }

    public QueueReceiver createReceiver(Queue queue, String messageSelector)
	throws JMSException
    {
	return new QueueReceiverAdapter(queueSession.createReceiver(queue,
	    messageSelector));
    }

    public QueueSender createSender(Queue queue) throws JMSException
    {
	return new QueueSenderAdapter(queueSession.createSender(queue));
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException
    {
	return queueSession.createTemporaryQueue();
    }

    // Supported JMS 1.1 operations if destination is a Queue

    public MessageConsumer createConsumer(Destination dest)
	throws JMSException
    {
	if ((dest == null) || (dest instanceof Queue)) {
	    return createReceiver((Queue) dest);
	} else {
	    throw new UnsupportedOperationException("Only Queue supported");
	}
    }

    public MessageConsumer createConsumer(Destination dest,
	String messageSelector) throws JMSException
    {
	if ((dest == null) || (dest instanceof Queue)) {
	    return createReceiver((Queue) dest, messageSelector);
	} else {
	    throw new UnsupportedOperationException("Only Queue supported");
	}
    }

    public MessageConsumer createConsumer(Destination dest,
	String messageSelector, boolean noLocal) throws JMSException
    {
	if ((dest == null) || (dest instanceof Queue)) {
	    return createReceiver((Queue) dest, messageSelector);
	} else {
	    throw new UnsupportedOperationException("Only Queue supported");
	}
    }

    public MessageProducer createProducer(Destination dest) throws JMSException
    {
	if ((dest == null) || (dest instanceof Queue)) {
	    return createSender((Queue) dest);
	} else {
	    throw new UnsupportedOperationException("Only Queue supported");
	}
    }

    // Unsupported JMS 1.1 Topic operations

    public TopicSubscriber createDurableSubscriber(Topic topic, String name)
	throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name,
	String messageSelector, boolean noLocal) throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public TopicPublisher createPublisher(Topic topic) throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public TopicSubscriber createSubscriber(Topic topic) throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public TopicSubscriber createSubscriber(Topic topic,
	String messageSelector, boolean noLocal) throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public Topic createTopic(String topicName) throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public void unsubscribe(String name) throws JMSException
    {
	throw new UnsupportedOperationException();
    }
}
