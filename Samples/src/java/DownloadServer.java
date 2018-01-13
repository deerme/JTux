/*
 * DownloadServer.java
 * 
 * The DownloadServer class shown below is a conversational Tuxedo server that allows
 * clients to download files from the machine were the server is running. This class 
 * shows how to write conversational Tuxedo servers using JTux.
 * In particular, this class shows how to use the jtux.atmi.ATMI.tpsend() method.
 * 
 * A download session proceeds as follows:
 * 
 *   1. The client connects to the conversational DOWNLOAD service, sending the name 
 *      of the remote file to download as part of the connect request.
 * 
 *   2. The DOWNLOAD service sends back the contents of the requested file in fixed
 *      size chunks.
 * 
 *   3. The DOWNLOAD service terminates by returning a NULL message after sending 
 *      the full contents of the requested file.
 */

import jtux.atmi.ATMI;
import jtux.atmi.TPSVCINFO;
import jtux.TUX;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DownloadServer
{
    public static void DOWNLOAD(TPSVCINFO rqst) throws IOException
    {
	if ((rqst.flags & ATMI.TPSENDONLY) == 0) {
	    TUX.userlog("ERROR: Client not in receive mode"); 
	    ATMI.tpreturn(ATMI.TPFAIL, 0, null, 0, 0); // Return null message as client is not in receive mode
	    return;
	}
	
	String fileName = StringUtils.readStringBuffer(rqst.data, rqst.len);
	try {
	    FileInputStream fis = new FileInputStream(fileName);
	    try {
	        send(fis, rqst.cd);
	    } finally {
	        fis.close();
	    }
	    ATMI.tpreturn(ATMI.TPSUCCESS, 0, null, 0, 0);
	} catch (FileNotFoundException eFileNotFound) {
	    ServerUtils.returnReply(ATMI.TPFAIL, 0, eFileNotFound.getMessage());
	}
    }

    static void send(FileInputStream fis, int cd) throws IOException
    {
	ReadableByteChannel channel = fis.getChannel();

	ByteBuffer buffer = ATMI.tpalloc("CARRAY", null, 4096);
	try {
	    while (true) {
		int len = channel.read(buffer);
		if (len == -1) {
		    break;
		}
		ATMI.tpsend(cd, buffer, len, 0);
		buffer.clear(); // Clear buffer for next channel.read()
	    }
	} finally {
	    ATMI.tpfree(buffer);
	}
    }
}
