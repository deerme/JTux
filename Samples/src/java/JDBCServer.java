/*
 * JDBCServer.java
 *
 * The JDBCServer class shown below implements the FINDDEPT and
 * MOVEDEPT services.
 */

import jtux.jdbc.ConnectionManager;

import jtux.fml32.FML32;
import jtux.fml32.TPFBuilder;

import jtux.atmi.ATMI;
import jtux.atmi.TPSVCINFO;

import jtux.holders.ByteBufferHolder;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.nio.ByteBuffer;

public class JDBCServer
{
    public static void tpservice(TPSVCINFO svcInfo) throws SQLException
    {
	Connection connection = ConnectionManager.getConnection();
	try {
	    boolean replyReturned = false;
	    ByteBufferHolder reply = new ByteBufferHolder();
	    reply.value = ATMI.tpalloc("FML32", null, 1024);
	    try {
		boolean ok;
		if (svcInfo.name.equals("FINDDEPT")) {
		    ok = findDept(connection, svcInfo.data, reply);
		} else if (svcInfo.name.equals("MOVEDEPT")) {
		    ok = moveDept(connection, svcInfo.data, reply);
		} else {
		    throw new IllegalArgumentException("Illegal service name: " + svcInfo.name);
		}
		ATMI.tpreturn(ok ? ATMI.TPSUCCESS : ATMI.TPFAIL, 0, reply.value, 0, 0);
		replyReturned = true;
	    } finally {
		if (!replyReturned) {
		    ATMI.tpfree(reply.value);
		}
	    }
	} finally {
	    connection.close();
	}
    }

    static boolean findDept(Connection connection, ByteBuffer request, ByteBufferHolder reply) throws SQLException
    {
	Statement statement = connection.createStatement();
	try {
	    int deptno = FML32.FgetInt(request, JDBCService.DEPTNO, 0);
	    ResultSet result = statement.executeQuery("SELECT LOC FROM DEPT WHERE DEPTNO=" + deptno);
	    if (!result.next()) {
		FML32.FaddString(reply.value, JDBCService.ERRMSG, "No such department: " + deptno);
		return false;
	    }
	    TPFBuilder.I.FchgInt(reply, JDBCService.DEPTNO, 0, deptno);
	    TPFBuilder.I.FchgString(reply, JDBCService.LOC, 0, result.getString("LOC"));
	    return true;
	} finally {
	    statement.close();
	}
    }

    static boolean moveDept(Connection connection, ByteBuffer request, ByteBufferHolder reply) throws SQLException
    {
	Statement statement = connection.createStatement();
	try {
	    int deptno = FML32.FgetInt(request, JDBCService.DEPTNO, 0);
	    String loc = FML32.FgetString(request, JDBCService.LOC, 0);
	    if (statement.executeUpdate("UPDATE DEPT SET LOC='" + loc + "'" + " WHERE DEPTNO=" + deptno) == 0) {
		TPFBuilder.I.FchgString(reply, JDBCService.ERRMSG, 0, "No such department: " + deptno);
		return false;
	    }
	    return true;
	} finally {
	    statement.close();
	}
    }
}
