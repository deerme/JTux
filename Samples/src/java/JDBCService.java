/*
 * JDBCService.java
 *
 * The JDBCService class shown below provides the FML field definitions
 * for the FINDDEPT and MOVEDEPT services.
 */

import jtux.fml32.FML32;

public class JDBCService
{
    public final static int ERRMSG = FML32.Fmkfldid(FML32.FLD_STRING, 10000);

    public final static int DEPTNO = FML32.Fmkfldid(FML32.FLD_LONG, 10010);

    public final static int LOC = FML32.Fmkfldid(FML32.FLD_STRING, 10011);
}
