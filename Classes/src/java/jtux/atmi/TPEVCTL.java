/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import java.io.Serializable;

/**
 * Java version of Tuxedo's TPEVCTL data structure.
 */
public class TPEVCTL implements Serializable
{
    /**
     * Indicates which fields are set.
     */
    public int flags;

    /**
     * Service name or queue space name.
     */
    public String name1;

    /**
     * Queue name.
     */
    public String name2;

    /**
     * Queue control parameters.
     */
    public TPQCTL qctl;

    /**
     * Initializes a new TPEVCTL object.
     */
    public TPEVCTL()
    {
	// Nothing to do
    }
}
