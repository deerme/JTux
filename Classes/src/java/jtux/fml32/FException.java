/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

import jtux.JTuxInterop;

/**
 * Thrown if an FML32 system call failed.
 *
 * @remarks
 * An FException carries an FML32 error code that identifies the reason why 
 * the exception was thrown. 
 * The symbolic constants for these error codes are defined in the FML32 class. 
 * 
 */
public class FException extends RuntimeException
{
    private int err;

    FException(int err)
    {
	this.err = err;
    }

    /**
     * @return
     * The FML32 error code that identifies the reason why this exception
     * was thrown.
     */
    public int Ferror()
    {
	return err;
    }

    /**
     * @return
     * A short message that describes the reason why this exception
     * was thrown.
     */
    public String getMessage()
    {
	String message = JTuxInterop.Fstrerror32(err);
	if ((message == null) || (message.equals(""))) {
	    message = "Ferror32 " + err;
	}
	return message;
    }
}
