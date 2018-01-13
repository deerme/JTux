/*
 * SimpleFML32Client.java
 * 
 * The SimpleFML32Client class shown below is a variant of the SimpleClient class
 * that uses FML32 rather than STRING messages. This class shows how to construct
 * FML32 messages using JTux and JTux Workstation. In particular, this class 
 * shows how to use the jtux.fml32.TPFBuilder class.
 */

import jtux.fml32.FML32;
import jtux.fml32.TPFBuilder;
import jtux.atmi.ATMI;
import jtux.holders.IntHolder;
import jtux.holders.ByteBufferHolder;

public class SimpleFML32Client
{
    public static void main(String[] args)
    {
        if (args.length == 0) {
            System.err.println("Usage: SimpleFML32Client <field>=<value> ...");
            System.exit(1);
        }
        
        ByteBufferHolder fbfrRef = new ByteBufferHolder();
        IntHolder lenOut = new IntHolder();
        
        ATMI.tpinit(null);
        try {
            fbfrRef.value = ATMI.tpalloc("FML32", null, 512);
            try {
                for (int i = 0; i < args.length; i++) {
                    String arg = args[i];
                    int eqPos = arg.indexOf('=');
                    String key = arg.substring(0, eqPos);
                    String value = arg.substring(eqPos + 1);
                    int fldid = FML32.Fldid(key);
                    TPFBuilder.I.FaddString(fbfrRef, fldid, value);
                }
                
                ATMI.tpcall("FML32_TOUPPER", fbfrRef.value, 0, fbfrRef, lenOut, 0);
                
                System.out.println("Returned FML32 buffer is: " + FML32.toString(fbfrRef.value));
                
            } finally {
                ATMI.tpfree(fbfrRef.value);
            }
        } finally {
            ATMI.tpterm();
        }
    }
}
