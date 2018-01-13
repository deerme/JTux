/*
 * ServerUtils.java
 * 
 * The ServerUtils class shown below provides a utility method for 
 * returning a STRING message from a service routine.
 */

import jtux.atmi.ATMI;
import java.nio.ByteBuffer;

public class ServerUtils
{
    // Returns a STRING type reply message from a service routine
    
    public static void returnReply(int rval, int rcode, String message)
    {
	boolean returned = false;
	ByteBuffer reply = StringUtils.newStringBuffer(message);
	try {
	    ATMI.tpreturn(ATMI.TPFAIL, rcode, reply, 0, 0);
	    returned = true;
	} finally {
	    if (!returned) {
	        ATMI.tpfree(reply);
	    }
	}
    }
}
