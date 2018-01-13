/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class TopicSessionAdapter extends SessionAdapter
    implements TopicSession
{
    private TopicSession topicSession;

    TopicSessionAdapter(TopicSession topicSession, boolean transacted,
	int ackMode)
    {
	super(topicSession, transacted, ackMode);

	this.topicSession = topicSession;
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name)
	throws JMSException
    {
	return new TopicSubscriberAdapter(
	    topicSession.createDurableSubscriber(topic, name));
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name,
	String messageSelector, boolean noLocal) throws JMSException
    {
	return new TopicSubscriberAdapter(
	    topicSession.createDurableSubscriber(topic, name,
		messageSelector, noLocal));
    }

    public TopicPublisher createPublisher(Topic topic) throws JMSException
    {
	return new TopicPublisherAdapter(topicSession.createPublisher(topic));
    }

    public TopicSubscriber createSubscriber(Topic topic) throws JMSException
    {
	return new TopicSubscriberAdapter(
	    topicSession.createSubscriber(topic));
    }

    public TopicSubscriber createSubscriber(Topic topic,
	String messageSelector, boolean noLocal) throws JMSException
    {
	return new TopicSubscriberAdapter(topicSession.createSubscriber(
	    topic, messageSelector, noLocal));
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException
    {
	return topicSession.createTemporaryTopic();
    }

    public Topic createTopic(String topicName) throws JMSException
    {
	return topicSession.createTopic(topicName);
    }

    public void unsubscribe(String name) throws JMSException
    {
	topicSession.unsubscribe(name);
    }

    // Supported JMS 1.1 operation if destination is a Topic
 
    public MessageConsumer createConsumer(Destination dest)
	throws JMSException
    {
	if ((dest == null) || (dest instanceof Topic)) {
	    return createSubscriber((Topic) dest);
	} else {
	    throw new UnsupportedOperationException("Only Topic supported");
	}
    }

    public MessageConsumer createConsumer(Destination dest,
	String messageSelector) throws JMSException
    {
	if ((dest == null) || (dest instanceof Topic)) {
	    return createSubscriber((Topic) dest, messageSelector, false);
	} else {
	    throw new UnsupportedOperationException("Only Topic supported");
	}
    }

    public MessageConsumer createConsumer(Destination dest,
	String messageSelector, boolean noLocal) throws JMSException
    {
	if ((dest == null) || (dest instanceof Topic)) {
	    return createSubscriber((Topic) dest, messageSelector, noLocal);
	} else {
	    throw new UnsupportedOperationException("Only Topic supported");
	}
    }

    public MessageProducer createProducer(Destination dest) throws JMSException
    {
	if ((dest == null) || (dest instanceof Topic)) {
	    return createProducer((Topic) dest);
	} else {
	    throw new UnsupportedOperationException("Only Topic supported");
	}
    }

    // Unsupported JMS 1.1 Queue operations

    public QueueBrowser createBrowser(Queue queue) throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public QueueBrowser createBrowser(Queue queue, String messageSelector)
	throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public Queue createQueue(String queueName) throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public QueueReceiver createReceiver(Queue queue, String messageSelector)
	throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public QueueSender createSender(Queue queue) throws JMSException
    {
	throw new UnsupportedOperationException();
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException
    {
	throw new UnsupportedOperationException();
    }
}
