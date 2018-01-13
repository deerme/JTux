/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class QueueReceiverAdapter extends MessageConsumerAdapter
    implements QueueReceiver
{
    private QueueReceiver receiver;

    QueueReceiverAdapter(QueueReceiver receiver)
    {
	super(receiver);

	this.receiver = receiver;
    }

    public Queue getQueue() throws JMSException
    {
	return receiver.getQueue();
    }
}
