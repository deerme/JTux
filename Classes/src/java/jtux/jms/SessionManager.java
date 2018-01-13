/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.jms;

import javax.jms.Session;
import javax.jms.JMSException;

/**
 * Manages a pool of thread-specific JMS XA Sessions.
 */
public class SessionManager
{
    SessionManager()
    {
	// Nothing to do, just make sure it is not public.
    }

    /**
     * @return
     * An XA-aware JMS Session for performing JMS operations from
     * the calling thread.
     * This a JMS 1.1 Session, even if the JMS client library used
     * supports only JMS 1.0.2.
     *
     * @remarks
     * The underlying JMS XA Session must be opened and closed using the
     * {@link jtux.atmi.ATMI#tpopen} and {@link jtux.atmi.ATMI#tpclose}
     * methods.
     * These methods are typically called from the tpsvrthrinit() and
     * tpsvrthrdone() methods of the involved server class.
     * If the server class does not define these methods then the JServer
     * executable calls {@link jtux.atmi.ATMI#tpopen} and
     * {@link jtux.atmi.ATMI#tpclose} automatically.
     * <p>
     * A service routine using a Session returned by {@link #getSession}
     * must <em>not</em> call the Session's close() method as this would
     * close the underlying JMS XA Session as well.
     *
     * @throws JMSException
     * The JMS client library encountered a technical problem.
     */ 
    public static Session getSession() throws JMSException
    {
	// It is not possible to return an adapted version of the Session
	// object that would protect against unwanted closing as that would
	// cause us to return a 'foreign' Session implementation from
	// ServerSession.getSession(), which is probably not allowed.

	return JMSXAResourceAdapter.getXASession();
    }
}
