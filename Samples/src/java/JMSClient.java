/*
 * JMSClient.java
 *
 * The JMSClient class shown below provides method for invoking the JMSENQ
 * and JMSDEQ services.
 */

import jtux.fml32.FML32;
import jtux.fml32.TPFBuilder;

import jtux.atmi.ATMI;
import jtux.atmi.TPESVCFAIL;

import jtux.holders.ByteBufferHolder;
import jtux.holders.IntHolder;

import java.nio.ByteBuffer;

public class JMSClient
{
    static void enqueue(String qName, String text)
    {
	ByteBufferHolder buffer = new ByteBufferHolder();
	buffer.value = ATMI.tpalloc("FML32", null, 1024);
	try {
	    FML32.FaddString(buffer.value, JMSService.QNAME, qName);
	    TPFBuilder.I.FaddString(buffer, JMSService.TEXT, text);
	    ATMI.tpcall("JMSENQ", buffer.value, 0, buffer, new IntHolder(), 0);
	} finally {
	    ATMI.tpfree(buffer.value);
	}
    }

    static String dequeue(String qName)
    {
	ByteBufferHolder buffer = new ByteBufferHolder();
	buffer.value = ATMI.tpalloc("FML32", null, 1024);
	try {
	    FML32.FaddString(buffer.value, JMSService.QNAME, qName);
	    ATMI.tpcall("JMSDEQ", buffer.value, 0, buffer, new IntHolder(), 0);
	    return FML32.FgetString(buffer.value, JMSService.TEXT, 0);
	} finally {
	    ATMI.tpfree(buffer.value);
	}
    }

    private static void usageExit()
    {
	System.err.println("Usage: JMSClient [enqueue <qname> <text>|dequeue <qname>]");
    }

    public static void main(String[] args) throws Exception
    {
	if (args.length < 1) {
	    usageExit();
	}

	String command = args[0];

	if (command.equals("enqueue")) {
	    if (args.length != 3) {
		usageExit();
	    }
	    String qName = args[1];
	    String text = args[2];
	    ATMI.tpinit(null);
	    try {
		enqueue(qName, text);
		System.out.println("Enqueued '" + text + "' in " + qName);
	    } finally {
		ATMI.tpterm();
	    }
	    System.exit(0);
	}

	if (command.equals("dequeue")) {
	    if (args.length != 2) {
		usageExit();
	    }
	    String qName = args[1];
	    ATMI.tpinit(null);
	    try {
		String text = dequeue(qName);
		System.out.println("Dequeued '" + text + "' from " + qName);
	    } finally {
		ATMI.tpterm();
	    }
	    System.exit(0);
	}

	usageExit();
    }
}
