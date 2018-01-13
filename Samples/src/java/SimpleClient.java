/*
 * SimpleClient.java
 * 
 * The SimpleClient class shown below is the Java version of Tuxedo's well-known 
 * TOUPPER client. This class shows the basics of writing Tuxedo clients in Java 
 * using JTux and JTux Workstation, including:
 * 
 *   - The use of the jtux.atmi.ATMI.tpinit() and jtux.atmi.ATMI.tpterm()
 *     methods for connecting to and disconnecting from Tuxedo.
 * 
 *   - The use of the jtux.atmi.ATMI.tpalloc() and jtux.atmi.ATMI.tpfree()
 *     methods for allocating and deallocating Tuxedo buffers.
 * 
 *   - How to read and write Tuxedo buffers using the java.nio.ByteBuffer class.
 * 
 *   - The use of the jtux.atmi.ATMI.tpcall() method for calling Tuxedo
 *     services.
 * 
 *   - The absence of explicit error checking code as JTux uses exceptions to 
 *     signal the occurrence of errors.
 *
 *   - The use of the try/finally construct to ensure that Tuxedo buffers are
 *     cleaned up and the client logs off properly, even if an error occurs.
 * 
 * Instructions for compiling and running this class can be found in the 
 * manual pages of the JTux and JTux Workstation class libraries.
 */

import jtux.atmi.ATMI;
import jtux.holders.ByteBufferHolder;
import jtux.holders.IntHolder;
import java.nio.ByteBuffer;

public class SimpleClient
{
    public static void main(String[] args)
    {
        if (args.length != 1) {
            System.err.println("Usage: SimpleClient <message>");
            System.exit(1);
        }
        
        ByteBufferHolder rcvbufRef = new ByteBufferHolder();
        IntHolder rcvlenRef = new IntHolder();
      
        String message = args[0];
        
        ATMI.tpinit(null);
        try {
            byte[] bytes = message.getBytes();
            int sendlen = bytes.length + 1;
            ByteBuffer sendbuf = ATMI.tpalloc("STRING", null, sendlen);
            try {
                rcvbufRef.value = ATMI.tpalloc("STRING", null, sendlen);
                try {
                    sendbuf.put(bytes);
                    sendbuf.put((byte) 0);
                    ATMI.tpcall("TOUPPER", sendbuf, 0, rcvbufRef, rcvlenRef, 0);
                    rcvbufRef.value.get(bytes);
                    System.out.println("Returned string is: " + new String(bytes));
                } finally {
                    ATMI.tpfree(rcvbufRef.value);
                }
            } finally {
                ATMI.tpfree(sendbuf);
            }
        } finally {
            ATMI.tpterm();
        }
    }
}
