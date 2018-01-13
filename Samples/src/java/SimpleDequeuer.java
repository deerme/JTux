/*
 * SimpleDequeuer.java
 * 
 * The SimpleDequeuer class shown below is a Tuxedo client that dequeues
 * a message from a Tuxedo/Q queue. This class shows how to dequeue messages 
 * from Tuxedo/Q queues using JTux and JTux Workstation. In particular, 
 * this class shows how to use the jtux.atmi.TPQCTL class and the 
 * jtux.atmi.ATMI.tpdequeue() method.
 */

import jtux.atmi.ATMI;
import jtux.atmi.TPQCTL;
import jtux.holders.IntHolder;
import jtux.holders.ByteBufferHolder;

public class SimpleDequeuer
{
    public static void main(String[] args)
    {
	if (args.length != 2) {
	    System.err.println("Usage: SimpleDequeuer <qspace> <qname>");
	    System.exit(1);
	}
	
	ByteBufferHolder dataRef = new ByteBufferHolder();
	IntHolder lenRef = new IntHolder();

	String qSpace = args[0];
	String qName = args[1];

	ATMI.tpinit(null);
	try {
	    dataRef.value = ATMI.tpalloc("STRING", null, 512);
	    try {
	        TPQCTL ctl = new TPQCTL();
	        ctl.flags = ATMI.TPQWAIT;
	        ATMI.tpdequeue(qSpace, qName, ctl, dataRef, lenRef, 0);
	        String message = StringUtils.readStringBuffer(dataRef.value, lenRef.value);
	        System.out.println("Dequeued '" + message + "' from " + qSpace + "." + qName);
	    } finally {
	        ATMI.tpfree(dataRef.value);
	    }
	} finally {
	    ATMI.tpterm();
	}
    }
}
