/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class TopicPublisherAdapter extends MessageProducerAdapter
    implements TopicPublisher
{
    private TopicPublisher topicPublisher;

    TopicPublisherAdapter(TopicPublisher topicPublisher)
    {
	super(topicPublisher);

	this.topicPublisher = topicPublisher;
    }

    public Topic getTopic() throws JMSException
    {
	return topicPublisher.getTopic();
    }

    public void publish(Message message) throws JMSException
    {
	topicPublisher.publish(MessageWrapper.unwrap(message));
    }

    public void publish(Message message, int deliveryMode, int priority,
	long timeToLive) throws JMSException
    {
	topicPublisher.publish(MessageWrapper.unwrap(message),
	    deliveryMode, priority, timeToLive);
    }

    public void publish(Topic topic, Message message) throws JMSException
    {
	topicPublisher.publish(topic, MessageWrapper.unwrap(message));
    }

    public void publish(Topic topic, Message message, int deliveryMode,
	int priority, long timeToLive) throws JMSException
    {
	topicPublisher.publish(topic, MessageWrapper.unwrap(message),
	    deliveryMode, priority, timeToLive);
    }

    // JMS 1.1 methods

    public Destination getDestination() throws JMSException
    {
	return getTopic();
    }

    public void send(Destination dest, Message message) throws JMSException
    {
	if ((dest == null) || (dest instanceof Topic)) {
	    publish((Topic) dest, message);
	} else {
	    throw new UnsupportedOperationException("Only Topic supported");
	}
    }

    public void send(Destination dest, Message message, int deliveryMode,
	int priority, long timeToLive) throws JMSException
    {
	if ((dest == null) || (dest instanceof Topic)) {
	    publish((Topic) dest, message, deliveryMode, priority, timeToLive);
	} else {
	    throw new UnsupportedOperationException("Only Topic supported");
	}
    }

    public void send(Message message) throws JMSException
    {
	publish(message);
    }

    public void send(Message message, int deliveryMode, int priority,
	long timeToLive) throws JMSException
    {
	publish(message, deliveryMode, priority, timeToLive);
    }
}
