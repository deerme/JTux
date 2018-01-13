/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import java.io.Serializable;

/**
 * Java version of Tuxedo's TPQCTL data structure.
 */
public class TPQCTL implements Serializable
{
    /**
     * Indicates which fields are set.
     */
    public int flags;

    /**
     * Absolute or relative dequeuing time for queued message.
     */
    public int deq_time;

    /**
     * Priority of queued message.
     */
    public int priority;

    /**
     * Reason for failure of queuing operation.
     */
    public int diagnostic;

    /**
     * Message id of queued message.
     */
    public byte[] msgid;

    /**
     * Correlation id of queued message.
     */
    public byte[] corrid;

    /**
     * Queue name for reply message.
     */
    public String replyqueue;

    /**
     * Failure queue name for queued message.
     */
    public String failurequeue;

    /**
     * Client identifier of originating client.
     */
    public CLIENTID cltid;

    /**
     * Application return code for queued message.
     */
    public int urcode;

    /**
     * Application authentication key of the originating client.
     */
    public int appkey;

    /**
     * Quality-of-service for queued message.
     */
    public int delivery_qos;

    /**
     * Quality-of-service for reply message.
     */
    public int reply_qos;

    /**
     * Expiration time of queued message.
     */
    public int exp_time;

    /**
     * Initializes a new TPQCTL object.
     */
    public TPQCTL()
    {
	// Nothing to do
    }
}
