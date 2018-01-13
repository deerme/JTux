/* 
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.jms;

import javax.jms.Session;
import javax.jms.Message;
import javax.jms.JMSException;

/**
 * @internal
 */
public interface MessageProcessor
{
    public void process(Session session, Message message) throws JMSException;
}
