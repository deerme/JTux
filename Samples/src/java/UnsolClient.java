/*
 * UnsolClient.java
 * 
 * The UnsolClient class shown below is a Tuxedo client that 
 * receives unsolicited messages and prints them to the screen. 
 * This class shows how to handle unsolicited messages using 
 * JTux and JTux Workstation. In particular, this class shows:
 * 
 *   - How to write an unsolicited message handler (tpunsol).
 * 
 *   - How to register an unsolicted message handler using the
 *     jtux.atmi.ATMI.tpsetunsol() method.
 * 
 *   - How to trigger unsolicited message handling using the
 *     jtux.atmi.ATMI.tpchkunsol() method.
 * 
 * This class is designed to work with a Tuxedo service that
 * sends an unsolicited message back to the client as part of
 * its operation.
 */

import jtux.atmi.ATMI;
import jtux.atmi.TPINIT;
import jtux.atmi.UnsolHandler;
import jtux.holders.ByteBufferHolder;
import jtux.holders.IntHolder;
import java.nio.ByteBuffer;

public class UnsolClient implements UnsolHandler
{
    // This is the unsollicited message handling method
    
    public void tpunsol(ByteBuffer data, int len, int flags)
    {
	System.out.println("Received unsolicited message: " + StringUtils.readStringBuffer(data, len));
    }

    static void waitForMessages(int timeout) throws InterruptedException
    {
	while (timeout > 0) {
	    System.out.println("Waiting for unsolicited message(s)");
	    Thread.sleep(1000);
	    if (ATMI.tpchkunsol() > 0) {
		return;
	    }
	    timeout--;
	}
	System.out.println("Giving up");
    }

    public static void main(String[] args) throws InterruptedException
    {
	if (args.length != 2) {
	    System.err.println("Usage: UnsolClient <service> <message>");
	    System.exit(1);
	}

	String service = args[0];
	String message = args[1];

	IntHolder cd = new IntHolder(); // Call descriptor for asynchronous service invocation
	ByteBufferHolder data = new ByteBufferHolder();
	IntHolder len = new IntHolder();

	TPINIT tpinfo = new TPINIT();
	tpinfo.cltname = "sample";
	tpinfo.flags = ATMI.TPU_DIP;

	ATMI.tpinit(tpinfo);
	try {
	    ATMI.tpsetunsol(new UnsolClient());
	    try {
	        data.value = StringUtils.newStringBuffer(message);
	        try {
	            System.out.println("Sending '" + message + "' to service " + service);
	            cd.value = ATMI.tpacall(service, data.value, 0, 0);
	            
	            waitForMessages(10);
	            
	            ATMI.tpgetrply(cd, data, len, 0);
	            message = StringUtils.readStringBuffer(data.value, len.value);
	            System.out.println("Returned string is: " + message);
	        } finally {
	            ATMI.tpfree(data.value);
	        }
	    } finally {
		ATMI.tpsetunsol(null);
	    }
	} finally {
	    ATMI.tpterm();
	}
    }
}
