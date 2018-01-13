/*
 * JMSServer.java
 *
 * The JMSServer class shown below implements the JMSENQ and JMSDEQ services.
 */

import jtux.jms.SessionManager;

import jtux.fml32.FML32;
import jtux.fml32.TPFBuilder;

import jtux.atmi.ATMI;
import jtux.atmi.TPSVCINFO;

import jtux.holders.ByteBufferHolder;

import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Queue;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.JMSException;

import java.nio.ByteBuffer;

public class JMSServer
{
    public static void tpservice(TPSVCINFO svcInfo) throws JMSException
    {
	Session session = SessionManager.getSession();
	boolean replyReturned = false;
	ByteBufferHolder reply = new ByteBufferHolder();
	reply.value = ATMI.tpalloc("FML32", null, 1024);
	try {
	    if (svcInfo.name.equals("JMSENQ")) {
		enqueue(session, svcInfo.data, reply);
	    } else if (svcInfo.name.equals("JMSDEQ")) {
		dequeue(session, svcInfo.data, reply);
	    } else {
		throw new IllegalArgumentException("Illegal service name: " + svcInfo.name);
	    }
	    ATMI.tpreturn(ATMI.TPSUCCESS, 0, reply.value, 0, 0);
	    replyReturned = true;
	} finally {
	    if (!replyReturned) {
		ATMI.tpfree(reply.value);
	    }
	}
    }

    static void enqueue(Session session, ByteBuffer request, ByteBufferHolder reply) throws JMSException
    {
	String qName = FML32.FgetString(request, JMSService.QNAME, 0);

	Queue queue = session.createQueue(qName);
	
	String text = FML32.FgetString(request, JMSService.TEXT, 0);

	MessageProducer producer = session.createProducer(queue);
	try {
	    producer.send(session.createTextMessage(text));
	} finally {
	    producer.close();
	}
    }

    static void dequeue(Session session, ByteBuffer request, ByteBufferHolder reply) throws JMSException
    {
	String qName = FML32.FgetString(request, JMSService.QNAME, 0);

	Queue queue = session.createQueue(qName);

	MessageConsumer consumer = session.createConsumer(queue);
	try {
	    TextMessage message = (TextMessage) consumer.receive(5000);
	    if (message != null) {
		TPFBuilder.I.FaddString(reply, JMSService.TEXT, message.getText());
	    }
	} finally {
	    consumer.close();
	}
    }
}
