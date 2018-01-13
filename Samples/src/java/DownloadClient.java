/*
 * DownloadClient.java
 * 
 * The DownloadClient class shown below is a conversational client that downloads a 
 * file from a download server. This class shows how to write a conversational
 * Tuxedo client in Java. In particular, this class shows how to use the 
 * jtux.atmi.Conversation class.
 * 
 * A download session proceeds as follows:
 * 
 *   1. The client connects to a conversational DOWNLOAD service, sending the name 
 *      of the remote file to download as part of the connect request.
 * 
 *   2. The DOWNLOAD service sends back the contents of the requested file in fixed
 *      size chunks. The client writes these chunks to a local file.
 * 
 *   3. The DOWNLOAD service terminates by returning a NULL message after sending 
 *      the full contents of the requested file.
 */

import jtux.atmi.TPEV_SVCSUCC;
import jtux.atmi.TPEV_SVCFAIL;
import jtux.atmi.Conversation;
import jtux.atmi.ATMI;
import jtux.holders.IntHolder;
import jtux.holders.ByteBufferHolder;
import java.nio.channels.WritableByteChannel;
import java.nio.ByteBuffer;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadClient
{
    // Connects to the DOWNLOAD service, passing the name of the file to download in the connect request
    
    static Conversation connect(String remoteFileName)
    {
	ByteBuffer request = StringUtils.newStringBuffer(remoteFileName);
	try {
	    return new Conversation("DOWNLOAD", request, 0, ATMI.TPRECVONLY);
	} finally {
	    ATMI.tpfree(request);
	}
    }
    
    // Receives data from the conversation and writes it to the given file
    
    static void receive(Conversation conv, FileOutputStream fos) throws IOException
    {
	WritableByteChannel channel = fos.getChannel();

	ByteBufferHolder buffer = new ByteBufferHolder();
	IntHolder len = new IntHolder();
	buffer.value = ATMI.tpalloc("CARRAY", null, 4096);
	try {
	    while (true) {
		try {
		    conv.receive(buffer, len, 0);
		} catch (TPEV_SVCSUCC eServiceSuccess) {
		    System.out.println();
		    break;
		}
		System.out.print('.');
		channel.write(buffer.value);
	    }
	} catch (TPEV_SVCFAIL eServiceFailure) {
	    throw new IOException(StringUtils.readStringBuffer(buffer.value, len.value));
	} finally {
	    ATMI.tpfree(buffer.value);
	}
    }

    public static void main(String[] args) 
    {
	if (args.length != 2) {
	    System.err.println("Usage: DownloadClient <remote file> <local file>");
	    System.exit(1);
	}
	
	String remoteFileName = args[0];
	String localFileName = args[1];

	ATMI.tpinit(null);
	try {
	    Conversation conv = connect(remoteFileName);
	    try {
	        FileOutputStream fos = new FileOutputStream(localFileName);
	        try {
	            receive(conv, fos);
	        } finally {
	            fos.close();
	        }
	    } finally {
	        conv.close();
	    }
	} catch (IOException eIO) {
	    System.err.println("ERROR: " + eIO.getMessage());
	    System.exit(1);
	} finally {
	    ATMI.tpterm();
	}
    }
}
