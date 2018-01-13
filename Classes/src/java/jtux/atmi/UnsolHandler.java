/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import java.nio.ByteBuffer;

/**
 * Unsolicited message handler interface.
 */
public interface UnsolHandler
{
    /**
     * Invoked to handle an unsolicited message.
     *
     * @param message
     * A typed buffer containing the message to handle; may be null.
     * The position of this buffer is 0 and its limit is the length
     * of the message contained in it.
     *
     * @param len
     * The length of the data in {@paramref message}.
     *
     * @param flags
     * See the Tuxedo tpsetunsol(3c) manual page.
     */
    public void tpunsol(ByteBuffer message, int len, int flags);
}
