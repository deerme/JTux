/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages a pool of thread-specific JDBC XA Connections.
 */
public class ConnectionManager
{
    ConnectionManager()
    {
	// Nothing to do, just make sure it is not public.
    }

    /**
     * @return
     * An XA-aware JDBC Connection for peforming JDBC operations
     * from the calling thread.
     *
     * @remarks
     * The underlying JDBC XA Connection must be be opened and closed using
     * the {@link jtux.atmi.ATMI#tpopen} and {@link jtux.atmi.ATMI#tpclose}
     * methods.
     * These methods are typically called from the tpsvrthrinit() and
     * tpsvrthrdone() methods of the involved server class.
     * If the server class does not define these methods then the JServer
     * executable calls {@link jtux.atmi.ATMI#tpopen} and
     * {@link jtux.atmi.ATMI#tpclose} automatically.
     * <p> 
     * A service routine using a Connection returned by
     * {@link #getConnection} should call the Connection's close() method
     * before returning.
     * This cleans up any JDBC resources used during the service invocation
     * but does not affect the underlying JDBC XA Connection.
     *
     * @throws SQLException
     * The JDBC driver encountered a technical problem.
     */
    public static Connection getConnection() throws SQLException
    {
	return JDBCXAResourceAdapter.getXAConnection().getConnection();
    }
}
