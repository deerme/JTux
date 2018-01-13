/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.jms;

import jtux.atmi.TPINIT;
import jtux.atmi.ATMI;

import jtux.TUX;

import javax.jms.*;

import java.util.LinkedList;

import java.io.PrintWriter;

/**
 * @internal
 */
public class TuxedoSessionPool implements ServerSessionPool
{
    private static PrintWriter debug = TUX.getDebugWriter("JMS");

    private TuxedoSession[] tuxedoSessions;

    private LinkedList terminatedTuxedoSessions;

    private LinkedList idleTuxedoSessions;

    private String serverGroupName;

    private TuxedoCredentials clientCredentials;

    public TuxedoSessionPool(int poolSize, String serverGroupName,
	TuxedoCredentials clientCredentials)
    {
	tuxedoSessions = new TuxedoSession[poolSize];
	for (int i = 0; i < poolSize; i++) {
	    tuxedoSessions[i] = new TuxedoSession(this);
	}

	idleTuxedoSessions = new LinkedList();

	terminatedTuxedoSessions = new LinkedList();

	this.serverGroupName = serverGroupName;

	this.clientCredentials = clientCredentials;
    }

    String getServerGroupName()
    {
	return serverGroupName;
    }

    TuxedoCredentials getCredentials()
    {
	return clientCredentials;
    }

    void putTerminatedTuxedoSession(TuxedoSession terminatedTuxedoSession)
    {
	if (debug != null) {
	    debug.println("" + this + ".putTerminatedTuxedoSession() adding "
		+ terminatedTuxedoSession + " to terminated pool");
	}

	synchronized (this) {
	    terminatedTuxedoSessions.add(terminatedTuxedoSession);
	    notify();
	}
    }

    void putIdleTuxedoSession(TuxedoSession idleTuxedoSession)
    {
	if (debug != null) {
	    debug.println("" + this + ".putIdleTuxedoSession() adding "
		+ idleTuxedoSession + " to idle pool");
	}

	synchronized (this) {
	    idleTuxedoSessions.add(idleTuxedoSession);
	    notify();
	}
    }

    private boolean running = false;

    public boolean start()
    {
	for (int i = 0; i < tuxedoSessions.length; i++) {
	    Thread thread = new Thread(tuxedoSessions[i]);
	    thread.setName("jtux.jms.TuxedoSessionThread" + i);
	    thread.start();
	}

	// Wait for all threads to start or terminate.

	synchronized (this) {
	    while (idleTuxedoSessions.size() + terminatedTuxedoSessions.size()
		    < tuxedoSessions.length) {
		try {
		    wait();
		} catch (InterruptedException eInterrupted) {
		    break;
		}
	    }
	}

	if (terminatedTuxedoSessions.size() > 0) {
	    stop();
	    return false;
	}

	running = true;

	return true;
    }

    public void stop()
    {
	synchronized (this) {
	    running = false;
	    notify();
	}

	for (int i = 0; i < tuxedoSessions.length; i++) {
	    tuxedoSessions[i].terminate();
	}
    }

    public TuxedoSession getIdleTuxedoSession() throws JMSException
    {
	TuxedoSession idleTuxedoSession;

	synchronized (this) {
	    while (running && idleTuxedoSessions.isEmpty()) {
		try {
		    wait();
		} catch (InterruptedException eInterrupted) {
		    throw new JMSException("Interrupted");
		}
	    }
	    if (!running) {
	        throw new JMSException("Not running");
	    }
	    idleTuxedoSession = (TuxedoSession)
		idleTuxedoSessions.removeLast();
	}

	if (debug != null) {
	    debug.println("" + this + ".getIdleTuxedoSession() returning "
		+ idleTuxedoSession + " from idle pool");
	}

	return idleTuxedoSession;
    }

    public ServerSession getServerSession() throws JMSException
    {
	if (debug != null) {
	    debug.println("" + this + ".getServerSession() called by "
		+ Thread.currentThread().getName());
	}
	return getIdleTuxedoSession();
    }
}
