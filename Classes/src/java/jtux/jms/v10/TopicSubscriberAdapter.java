/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class TopicSubscriberAdapter extends MessageConsumerAdapter
    implements TopicSubscriber
{
    private TopicSubscriber subscriber;

    TopicSubscriberAdapter(TopicSubscriber subscriber)
    {
	super(subscriber);

	this.subscriber = subscriber;
    }

    public boolean getNoLocal() throws JMSException
    {
	return subscriber.getNoLocal();
    }

    public Topic getTopic() throws JMSException
    {
	return subscriber.getTopic();
    }
}
