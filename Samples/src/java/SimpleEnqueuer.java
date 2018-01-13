/*
 * SimpleEnqueuer.java
 * 
 * The SimpleEnqueuer class shown below is a Tuxedo client that enqueues a
 * message in a Tuxedo/Q queue. This class shows how to enqueue messages in 
 * Tuxedo/Q queues using JTux and JTux Workstation. In particular, this 
 * class shows how to use the jtux.atmi.TPQCTL class and the 
 * jtux.atmi.ATMI.tpenqueue() method.
 */

import jtux.atmi.ATMI;
import jtux.atmi.TPQCTL;
import java.nio.ByteBuffer;

public class SimpleEnqueuer
{
    public static void main(String[] args)
    {
	if ((args.length < 3) || (args.length > 4)) {
	    System.err.println("Usage: SimpleEnqueuer <qspace> <qname> <message> [<replyq>]");
	    System.exit(1);
	}

	String qSpace = args[0];
	String qName = args[1];
	String message = args[2];
	String replyQ = (args.length < 4) ? null : args[3];

	ATMI.tpinit(null);
	try {
	    ByteBuffer data = StringUtils.newStringBuffer(message);
	    try {
	        TPQCTL ctl = new TPQCTL();
	        ctl.flags = ATMI.TPNOFLAGS;
	        if (replyQ != null) {
	            ctl.flags = ctl.flags | ATMI.TPQREPLYQ;
	            ctl.replyqueue = replyQ;
	        }
	        ATMI.tpenqueue(qSpace, qName, ctl, data, 0, 0);
	        System.out.println("Enqueued '" + message + "' in " + qSpace + "." + qName);
	    } finally {
	        ATMI.tpfree(data);
	    }
	} finally {
	    ATMI.tpterm();
	}
    }
}
