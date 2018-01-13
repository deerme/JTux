/*
 * JDBCException.java
 *
 * The JDBCException class shown below is the exception class for 
 * application level FINDDEPT and MOVEDEPT service failures.
 */

import jtux.fml32.FML32;

import java.nio.ByteBuffer;

public class JDBCException extends Exception
{
    JDBCException(ByteBuffer reply)
    {
	super(FML32.FgetString(reply, JDBCService.ERRMSG, 0));
    }
}
