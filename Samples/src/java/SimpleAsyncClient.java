/*
 * SimpleAsyncClient.java
 * 
 * The SimpleAsyncClient class shown below is a variant of the SimpleClient
 * class that calls the TOUPPER service asynchronously. This class shows
 * how to call Tuxedo services asynchronously using JTux and JTux
 * Workstation. In particular, this class shows how to use the 
 * jtux.atmi.ATMI.tpacall() and jtux.atmi.ATMI.tpgetrply() methods.
 */

import jtux.atmi.ATMI;
import jtux.holders.ByteBufferHolder;
import jtux.holders.IntHolder;
import java.nio.ByteBuffer;

public class SimpleAsyncClient
{
    public static void main(String[] args)
    {
        if (args.length != 1) {
            System.err.println("Usage: SimpleAsyncClient <message>");
            System.exit(1);
        }

        IntHolder cdRef = new IntHolder(); // Call descriptor
        ByteBufferHolder rcvbufRef = new ByteBufferHolder();
        IntHolder rcvlenRef = new IntHolder();

        String message = args[0];
        
        ATMI.tpinit(null);
        try {
            ByteBuffer sendbuf = StringUtils.newStringBuffer(message);
            try {
                System.out.println("Sending '" + message + "' to service TOUPPER");
                cdRef.value = ATMI.tpacall("TOUPPER", sendbuf, 0, 0);
            } finally {
                ATMI.tpfree(sendbuf);
            }
            
            rcvbufRef.value = ATMI.tpalloc("STRING", null, 256);
            try {
                ATMI.tpgetrply(cdRef, rcvbufRef, rcvlenRef, 0);
                message = StringUtils.readStringBuffer(rcvbufRef.value, rcvlenRef.value);
                System.out.println("Returned string is: " + message);
            } finally {
                ATMI.tpfree(rcvbufRef.value);
            }
        } finally {
            ATMI.tpterm();
        }
    }
}
