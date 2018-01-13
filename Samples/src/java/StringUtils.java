/*
 * StringUtils.java
 * 
 * The StringUtils class shown below provides a collection of utility methods
 * for dealing with Tuxedo STRING buffers in Java.
 */

import jtux.atmi.ATMI;
import java.nio.ByteBuffer;

public class StringUtils
{
    // Creates a STRING buffer and fills it with the given string
    
    public static ByteBuffer newStringBuffer(String s)
    {
        byte[] bytes = s.getBytes(); // Convert to single-byte character sequence
        ByteBuffer buffer = ATMI.tpalloc("STRING", null, bytes.length + 1); // One byte extra for zero-terminator
        buffer.put(bytes);
        buffer.put((byte) 0); // Add zero-terminator
        return buffer;
    }
    
    // Reads a string from a STRING buffer
    
    public static String readStringBuffer(ByteBuffer buffer, int len)
    {
        byte[] bytes = new byte[len - 1]; // Don't read zero-terminator
        buffer.get(bytes);
        return new String(bytes);
    }
}
