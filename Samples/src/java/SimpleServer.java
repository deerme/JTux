/*
 * SimpleServer.java
 * 
 * The SimpleServer class shown below is the Java version of Tuxedo's well-known
 * TOUPPER server. This class shows the basics of writing Tuxedo servers using
 * JTux, including:
 *
 *   - How to write a server initialization method (tpsvrinit).
 * 
 *   - The use of the TUX.userlog() method to write to the Tuxedo ULOG.
 * 
 *   - How to write a service routine method (TOUPPER).
 * 
 *   - The use of the jtux.atmi.TPSVCINFO class for reading service request
 *     parameters.
 * 
 *   - The use of the java.nio.ByteBuffer class for reading and writing Tuxedo 
 *     buffers.
 * 
 *   - The use of the jtux.atmi.ATMI.tpreturn() method for terminating a
 *     service routine.
 * 
 * Instructions for compiling this class can be found in the manual page of the 
 * JTux class library. Instructions for running the resuling Tuxedo server class 
 * can be found in the manual page of the JServer executable.
 */

import jtux.atmi.ATMI;
import jtux.atmi.TPSVCINFO;
import jtux.TUX;
import java.nio.ByteBuffer;

public class SimpleServer
{
    public static int tpsvrinit(String[] args)
    {
        TUX.userlog("Welcome to the simple server written in Java");
        return 0;
    }
    
    public static void TOUPPER(TPSVCINFO rqst)
    {
        ByteBuffer data = rqst.data;
        
        byte[] bytes = new byte[rqst.len - 1];
        data.get(bytes);
        String s = new String(bytes);
        
        s = s.toUpperCase();
        
        data.clear();
        data.put(s.getBytes());
        data.put((byte) 0);
        
        ATMI.tpreturn(ATMI.TPSUCCESS, 0, data, 0, 0);
    }
}   
