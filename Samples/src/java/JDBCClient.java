/*
 * JDBCClient.java
 *
 * The JDBCClient class shown below provides methods for invoking the
 * FINDDEPT and MOVEDEPT services.
 */

import jtux.atmi.ATMI;
import jtux.atmi.TPQCTL;
import jtux.atmi.TPESVCFAIL;

import jtux.fml32.FML32;
import jtux.fml32.TPFBuilder;

import jtux.holders.ByteBufferHolder;
import jtux.holders.IntHolder;

import java.nio.ByteBuffer;

public class JDBCClient
{
    static String findDept(int deptno) throws JDBCException
    {
	ByteBufferHolder buffer = new ByteBufferHolder();
	buffer.value = ATMI.tpalloc("FML32", null, 1024);
	try {
	    TPFBuilder.I.FaddInt(buffer, JDBCService.DEPTNO, deptno);
	    try {
		ATMI.tpcall("FINDDEPT", buffer.value, 0, buffer, new IntHolder(), 0);
		return FML32.FgetString(buffer.value, JDBCService.LOC, 0);
	    } catch (TPESVCFAIL eServiceFailure) {
		throw new JDBCException(buffer.value);
	    }
	} finally {
	    ATMI.tpfree(buffer.value);
	}
    }

    static void moveDept(int deptno, String loc) throws JDBCException
    {
	ByteBufferHolder buffer = new ByteBufferHolder();
	buffer.value = ATMI.tpalloc("FML32", null, 1024);
	try {
	    TPFBuilder.I.FaddInt(buffer, JDBCService.DEPTNO, deptno);
	    TPFBuilder.I.FaddString(buffer, JDBCService.LOC, loc);
	    try {
		ATMI.tpcall("MOVEDEPT", buffer.value, 0, buffer, new IntHolder(), 0);
	    } catch (TPESVCFAIL eServiceFailure) {
		throw new JDBCException(buffer.value);
	    }
	} finally {
	    ATMI.tpfree(buffer.value);
	}
    }

    static void sendMessage(String message)
    {
	ByteBuffer data = StringUtils.newStringBuffer(message);
	try {
	    TPQCTL ctl = new TPQCTL();
	    ctl.flags = ATMI.TPNOFLAGS;
	    ATMI.tpenqueue("QSPACE", "DEPTMOVES", ctl, data, 0, 0);
	} finally {
	    ATMI.tpfree(data);
	}
    }

    private static void usageExit()
    {
	System.err.println("Usage: JDBCClient [find <deptno>|move <deptno> <loc>]");
    }

    public static void main(String[] args) throws Exception
    {
	if (args.length < 1) {
	    usageExit();
	}

	String command = args[0];

	if (command.equals("find")) {
	    if (args.length != 2) {
		usageExit();
	    }
	    int deptno = Integer.parseInt(args[1]);
	    ATMI.tpinit(null);
	    try {
	        String loc = findDept(deptno);
		System.out.println("The location of department " + deptno + " is " + loc);
	    } finally {
		ATMI.tpterm();
	    }
	    System.exit(0);
	}

	if (command.equals("move")) {
	    if (args.length != 3) {
		usageExit();
	    }
	    int deptno = Integer.parseInt(args[1]);
	    String loc = args[2];
	    ATMI.tpinit(null);
	    try {
		boolean committed = false;
		ATMI.tpbegin(30, 0);
		try {
		    moveDept(deptno, loc);
		    sendMessage("Moved department " + deptno + " to " + loc);
		    if (!loc.equalsIgnoreCase("ANTARCTICA")) {
			ATMI.tpcommit(0);
			committed = true;
		    }
		} finally {
		    if (!committed) {
			ATMI.tpabort(0);
			sendMessage("Did not move department " + deptno + " to " + loc);
		    }
		}
	    } finally {
		ATMI.tpterm();
	    }
	    System.exit(0);
	}

	usageExit();
    }
}
