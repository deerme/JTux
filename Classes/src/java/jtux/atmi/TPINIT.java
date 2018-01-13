/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import jtux.TUX;

import java.io.Serializable;

/**
 * Java version of Tuxedo's TPINIT data structure.
 */
public class TPINIT implements Serializable
{
    /**
     * User name for logon.
     */
    public String usrname;

    /**
     * Client class name.
     */
    public String cltname;

    /**
     * Password for logon.
     */
    public String passwd;

    /**
     * Name of server group to run in.
     */
    public String grpname;

    /**
     * Connect flags.
     */
    public int flags;

    /**
     * Additional authentication data for logon.
     */
    public byte[] data;

    /**
     * Initializes a new TPINIT object.
     */
    public TPINIT()
    {
	// Nothing to do
    }

    /**
     * @internal
     */
    public String toString()
    {
	StringBuffer s = new StringBuffer();
	s.append(TPINIT.class.getName());
	s.append(" (usrname=" + usrname);
	s.append(", cltname=" + cltname);
	s.append(", passwd=" + passwd);
	s.append(", grpname=" + grpname);
	s.append(", flags=" + flags);
	s.append(", data=" + data);
	s.append(')');
	return s.toString();
    }
}
