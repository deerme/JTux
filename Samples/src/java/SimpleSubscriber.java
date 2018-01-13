/*
 * SimpleSubscriber.java
 * 
 * The SimpleSubscriber class shown below is a Tuxedo client that subscribes to
 * events posted to the Tuxedo event broker. This class shows how to subscribe 
 * to events using JTux and JTux Workstation. In particular, this class shows 
 * how to use the jtux.atmi.ATMI.tpsubscribe() method.
 */

import jtux.atmi.ATMI;
import jtux.atmi.TPINIT;
import jtux.atmi.UnsolHandler;
import jtux.holders.IntHolder;
import jtux.holders.ByteBufferHolder;
import java.nio.ByteBuffer;

public class SimpleSubscriber implements UnsolHandler
{
    public void tpunsol(ByteBuffer data, int len, int flags)
    {
	System.out.println("Received message: " + StringUtils.readStringBuffer(data, len));
    }

    static void processMessages(int seconds) throws InterruptedException
    {
	System.out.println("Waiting for message(s)");

	for (int i = 0, reps = seconds * 10; i < reps; i++) {
	    Thread.sleep(100);
	    ATMI.tpchkunsol();
	}
    }

    public static void main(String[] args) throws InterruptedException
    {
	if ((args.length < 1) || (args.length > 2)) {
	    System.err.println("Usage: SimpleSubscriber <eventexpr> [<filter>]");
	    System.exit(1);
	}

	String eventexpr = args[0];
	String filter = (args.length < 2) ? null : args[1];

	TPINIT tpinfo = new TPINIT();
	tpinfo.flags = ATMI.TPU_DIP;

	ATMI.tpinit(tpinfo);
	try {
	    ATMI.tpsetunsol(new SimpleSubscriber());
	    try {
	        System.out.println("Subscribing to events matching '" + eventexpr + "'");
		int handle = ATMI.tpsubscribe(eventexpr, filter, null, 0);
		try {
	            processMessages(5);
		} finally {
		    ATMI.tpunsubscribe(handle, 0);
		}
	    } finally {
		ATMI.tpsetunsol(null);
	    }
	} finally {
	    ATMI.tpterm();
	}
    }
}
