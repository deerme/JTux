/*
 * SimpleFML32Server.java
 * 
 * The SimpleFML32Server class shown below is a variant of the SimpleServer
 * class that uses FML32 rather than STRING messages. This class shows how
 * to read and write FML32 buffers using JTux. In particular, this class 
 * shows how to use the Fnext(), Fldtype(), FgetString() and Fchg() methods 
 * of the jtux.fml32.FML32 class.
 */

import jtux.fml32.FML32;
import jtux.atmi.ATMI;
import jtux.atmi.TPSVCINFO;
import jtux.holders.IntHolder;
import java.nio.ByteBuffer;

public class SimpleFML32Server
{
    public static void FML32_TOUPPER(TPSVCINFO rqst)
    {
        ByteBuffer fbfr = rqst.data;
        
        IntHolder fldidRef = new IntHolder();
        IntHolder occRef = new IntHolder();
        
        fldidRef.value = FML32.FIRSTFLDID;
        
        while (FML32.Fnext(fbfr, fldidRef, occRef)) {
            int fldid = fldidRef.value;
            int occur = FML32.Foccur(fbfr, fldid);
            if (FML32.Fldtype(fldid) == FML32.FLD_STRING) {
                for (int i = 0; i < occur; i++) {
                    String s = FML32.FgetString(fbfr, fldid, i);
                    s = s.toUpperCase();
                    FML32.Fchg(fbfr, fldid, i, s);
                }
            }
            occRef.value = occur; // Skip to next field id
        }
        
        ATMI.tpreturn(ATMI.TPSUCCESS, 0, fbfr, 0, 0);
    }
}
