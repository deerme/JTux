/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import java.nio.ByteBuffer;

/**
 * Java version of Tuxedo's TPSVCINFO data structure.
 *
 * @ifndef WSCLIENT
 */
public class TPSVCINFO
{
    /**
     * The name of the called service.
     */
    public String name;

    /**
     * A typed buffer containing the request data of the service call;
     * may be null.
     * The position of this buffer is 0 and its limit is the length of the
     * message contained in it.
     */
    public ByteBuffer data;

    /**
     * The length of the data in {@link #data}.
     */
    public int len;

    /**
     * Service invocation flags.
     */
    public int flags;

    /**
     * Connection descriptor for conversation.
     */
    public int cd;

    /**
     * Application authentication key of caller.
     */
    public int appkey;

    /**
     * Client identifier of caller.
     */
    public CLIENTID cltid;

    TPSVCINFO()
    {
	// Nothing to do, just make sure it is not public.
    }
}
