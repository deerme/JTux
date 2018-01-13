/*
 * TimePublisher.java
 * 
 * The TimePublisher class shown below is a Tuxedo server that posts
 * TIME messages to the Tuxedo event broker in one-second intervals. 
 * This class shows how to post messages to the Tuxedo event broker 
 * using JTux. In particular, this class shows how to use the 
 * jtux.atmi.ATMI.tppost() method.
 */

import jtux.atmi.ATMI;
import jtux.atmi.TPSVCINFO;
import jtux.atmi.TPENOENT;
import jtux.TUX;
import java.nio.ByteBuffer;
import java.util.Date;

public class TimePublisher
{
    public static int tpsvrinit(String[] args)
    {
	ATMI.tpacall("PUBLISH_TIME", null, 0, ATMI.TPNOREPLY);
	return 0;
    }

    public static void PUBLISH_TIME(TPSVCINFO svcinfo) throws InterruptedException
    {
	ByteBuffer message = StringUtils.newStringBuffer("It is now " + new Date());
	try {
	    ATMI.tppost("TIME", message, 0, 0);
	} catch (TPENOENT e) {
	    // EventBroker not running yet (ignored)
	} finally {
	    ATMI.tpfree(message);
	}

	Thread.sleep(1000);

	ATMI.tpacall("PUBLISH_TIME", null, 0, ATMI.TPNOREPLY);

	ATMI.tpreturn(ATMI.TPSUCCESS, 0, null, 0, 0);
    }
}
