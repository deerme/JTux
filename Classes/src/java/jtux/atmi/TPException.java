/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import jtux.JTuxInterop;

/**
 * Thrown if an ATMI system call failed.
 *
 * @remarks
 * A TPException carries an ATMI error code that identifies the reason why
 * the exception was thrown. 
 * The symbolic constants for these error codes are defined in the ATMI class.
 */
public class TPException extends RuntimeException
{
    private int err;

    TPException(int err)
    {
	this.err = err;
    }

    /**
     * @return
     * The ATMI error code that identifies the reason why this exception
     * was thrown.
     */
    public int tperrno()
    {
	return err;
    }

    /**
     * @return
     * A short message that describes the reason why this exception was
     * thrown.
     */
    public String getMessage()
    {
	String message = JTuxInterop.tpstrerror(err);
	if ((message == null) || (message.equals(""))) {
	    message = "tperrno " + err;
	}
	return message;
    }
}
