/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.jms;

import jtux.atmi.ATMI;
import jtux.atmi.TPINIT;

import jtux.TUX;

import javax.jms.*;

import java.io.PrintWriter;

/**
 * @internal
 */
public class TuxedoSession implements Runnable, ServerSession, MessageListener
{
    private static PrintWriter debug = TUX.getDebugWriter("JMS");

    private TuxedoSessionPool pool;

    private Session session = null;

    private MessageProcessor processor = null;

    TuxedoSession(TuxedoSessionPool pool)
    {
	this.pool = pool;
    }

    public void setMessageProcessor(MessageProcessor processor)
    {
	this.processor = processor;
    }

    // Implementation of ServerSession.getSession()

    public Session getSession() throws JMSException
    {
	if (debug != null) {
	    debug.println("" + this + ".getSession() called by "
		+ Thread.currentThread().getName());
	}
	if (session == null) {
	    throw new javax.jms.IllegalStateException("" + this
		+ " not running");
	}
	return session;
    }

    private boolean terminated = false;

    void terminate()
    {
	if (debug != null) {
	    debug.println("" + this + ".terminate() called by "
		+ Thread.currentThread().getName());
	}

	synchronized (this) {
	    terminated = true;
	    notify();
	}
    }
    
    public void run()
    {
	try {
	    TPINIT tpinfo = new TPINIT();
	    tpinfo.grpname = pool.getServerGroupName();
	    tpinfo.flags = ATMI.TPMULTICONTEXTS; 

	    TuxedoCredentials credentials = pool.getCredentials();
	    if (credentials != null) {
		tpinfo.usrname = credentials.getUserName();
		tpinfo.passwd = credentials.getPassword();
		tpinfo.data = credentials.getAuthData();
	    }

	    ATMI.tpinit(tpinfo);
	    try {
	        ATMI.tpopen();
	        try {
		     session = SessionManager.getSession();
		     session.setMessageListener(this);
		     TUX.userlog("INFO: Tuxedo JMS Session thread ready");
		     runLoop();
		     TUX.userlog("INFO: Tuxedo JMS Session thread exiting");
		} finally {
		    ATMI.tpclose();
		}
	    } finally {
		ATMI.tpterm();
	    }
	} catch (Throwable e) {
	    TUX.userlog("ERROR: " + this + " terminated by exception:");
	    e.printStackTrace(TUX.ULOG);
	} finally {
	    pool.putTerminatedTuxedoSession(this);
	}
    }

    private boolean started = false;

    // Implementation of ServerSession.start()

    public void start()
    {
	if (debug != null) {
	    debug.println("" + this + ".start() called by "
		+ Thread.currentThread().getName());
	}

	synchronized (this) {
	    started = true;
	    notify();
	}
    }

    private boolean commit;

    private void runLoop() throws InterruptedException, JMSException
    {
	// It is not entirely clear to me at which point I should start
	// the transaction. Currently, the transaction is started after
	// the ConnectionConsumer has loaded the session with messages
	// but before these messages are consumed by the session. If this
	// does not work then an alternative may be to start the transaction
	// before the ConnectionConsumer loads the session with messages by
	// changing the ServerSessionPool.getServerSession() implementation
	// to send an event to the ServerSession to start a transaction.

	while (true) {
	    pool.putIdleTuxedoSession(this);
	    synchronized (this) {
		while ((!started) && (!terminated)) {
		    wait();
		}
	        if (terminated) {
		    if (debug != null) {
	    		debug.println("" + this + " terminating");
		    }
		    break;
	        }
	    }
	    if (debug != null) {
	    	debug.println("" + this + " starting");
	    }
	    ATMI.tpbegin(0, 0);
	    try {
		commit = false; // set to true by onMessage()
	        session.run();
		if (commit) {
		    ATMI.tpcommit(0);
		}
	    } finally {
		if (ATMI.tpgetlev() == 1) {
		    TUX.userlog("INFO: Aborting transaction");
		    ATMI.tpabort(0);
		}
	    }
	    started = false; // Reset for next iteration
	}
    }

    public void onMessage(Message message)
    {
	if (debug != null) {
	    debug.println("" + this + " onMessage() called with JMS message "
		+ message);
	}

	try {
	    processor.process(session, message);
	    commit = true;
	} catch (Exception e) {
	    TUX.userlog("WARNING: Exception invoking " + processor);
	    e.printStackTrace(TUX.ULOG);
	}
    }
}
