/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

import javax.jms.*;

public class MessageListenerAdapter extends Adapter implements MessageListener
{
    private MessageListener listener;

    MessageListenerAdapter(MessageListener listener)
    {
	super(listener);

	this.listener = listener;
    }

    public void onMessage(Message message)
    {
	listener.onMessage(MessageWrapper.wrap(message));
    }

    MessageListener getListener()
    {
	return listener;
    }

    static MessageListener wrap(MessageListener listener)
    {
	if (listener == null) {
	    return null;
	} else {
	    return new MessageListenerAdapter(listener);
	}
    }

    static MessageListener unwrap(MessageListener listener)
    {
	if (listener == null) {
	    return null;
	} else {
	    return ((MessageListenerAdapter) listener).getListener();
	}
    }
}
