/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import jtux.holders.ByteBufferHolder;
import jtux.holders.IntHolder;

import java.nio.ByteBuffer;

/**
 * Utility class for managing the client side of a conversation.
 *
 * @remarks
 * The {@link #close} method of a Conversation performs the correct
 * operation depending on the state of the conversation.
 * If it is called before the server has terminated the conversation then
 * it aborts the conversation.
 * Otherwise, it does nothing.
 * This behaviour allows the initiator of a conversation to manage the
 * conversation using a try/finally block, as shown below:
 * <pre>
 * Conversation conv = new Conversation(...);
 * try {
 *     ... // Send and receive messages using conv.send() and conv.receive()
 * } finally {
 *     conv.close();
 * }
 * </pre>
 */
public class Conversation
{
    private int cd;

    /**
     * Initializes a new Conversation object by establishing a
     * connection with a conversational service.
     *
     * @param svc
     * The name of the Tuxedo service.
     *
     * @param message
     * A typed buffer containing the message to send along with the
     * connect request; may be null.
     *
     * @param len
     * The length of the data in {@paramref message}.
     *
     * @param flags
     * Must contain either {@link ATMI#TPSENDONLY} or {@link ATMI#TPRECVONLY}.
     * See the Tuxedo tpconnect(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpconnect(3c) manual page.
     *
     * @see jtux.atmi.ATMI#tpconnect
     */
    public Conversation(String svc, ByteBuffer message, int len, int flags)
    {
	cd = ATMI.tpconnect(svc, message, len, flags);
    }

    /**
     * Sends a message as part of the conversation.
     *
     * @param message
     * A typed buffer containing the message to send; may be null.
     *
     * @param len
     * The length of the data in {@paramref message}.
     *
     * @param flags
     * Set {@link ATMI#TPRECVONLY} to give up control of the conversation.
     * See the Tuxedo tpsend(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpsend(3c) manual page.
     *
     * @see jtux.atmi.ATMI#tpsend
     */
    public void send(ByteBuffer message, int len, int flags)
    {
	try {
	    ATMI.tpsend(cd, message, len, flags);
	} catch (TPEEVENT eEvent) {
	    cd = -1;
	    throw eEvent;
	}
    }

    /**
     * Receives a message as part of a conversation.
     *
     * @param messageRef
     * A typed buffer allocated using {@link jtux.atmi.ATMI#tpalloc}.
     * Gets updated with a typed buffer containing the message received
     * as part of the conversation.
     *
     * @param lenOut
     * Gets updated with the length of the data in {@paramref messageRef}.
     * If the length is 0 then a null message was received.
     *
     * @param flags
     * See the Tuxedo tprecv(3c) manual page.
     *
     * @throws TPEV_SENDONLY
     * The other end of the connection has given up control of the
     * conversation.
     *
     * @throws TPEV_SVCSUCC
     * The service routine returned successfully.
     * In this case, {@paramref messageRef} and {@paramref lenOut} are
     * updated as described above.
     *
     * @throws TPEV_SVCFAIL
     * The service routine returned with an application level service
     * failure.
     * In this case, {@paramref messageRef} and {@paramref lenOut} are
     * updated as described above.
     *
     * @throws TPException
     * See the Tuxedo tprecv(3c) manual page.
     *
     * @see jtux.atmi.ATMI#tprecv
     */
    public void receive(ByteBufferHolder messageRef, IntHolder lenOut, 
	int flags)
    {
	try {
	    ATMI.tprecv(cd, messageRef, lenOut, flags);
	} catch (TPEV_SENDONLY eSendOnly) {
	    throw eSendOnly;
	} catch (TPEEVENT eEvent) {
	    cd = -1;
	    throw eEvent;
	}
    }

    /**
     * Closes the conversation.
     *
     * @remarks
     * If the server has not terminated the conversation at the time this
     * method is called then this method aborts the conversation. 
     * Otherwise, it does nothing.
     *
     * @throws TPException
     * See the Tuxedo tpdiscon(3c) manual page.
     *
     * @see jtux.atmi.ATMI#tpdiscon
     */
    public void close()
    {
	if (cd != -1) {
	    ATMI.tpdiscon(cd);
	    cd = -1;
	}
    }
}
