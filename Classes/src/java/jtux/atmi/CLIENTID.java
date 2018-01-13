/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import java.io.Serializable;

/**
 * Java version of Tuxedo's CLIENTID data structure.
 */
public class CLIENTID implements Serializable
{
    private int clientdata0;

    private int clientdata1;

    private int clientdata2;

    private int clientdata3;

    CLIENTID()
    {
	// Nothing to do, just make sure it is not public
    }

    /**
     * Tests whether this CLIENTID equals another object.
     *
     * @param other
     * The other object to compare to.
     *
     * @return
     * True if {@paramref other} is a CLIENTID that has the same value
     * as this CLIENTID, false otherwise.
     */
    public boolean equals(Object other)
    {
	if ((other != null) && (other instanceof CLIENTID)) {
	    CLIENTID otherCLIENTID = (CLIENTID) other;
	    return (clientdata0 == otherCLIENTID.clientdata0)
		&& (clientdata1 == otherCLIENTID.clientdata1)
		&& (clientdata2 == otherCLIENTID.clientdata2)
		&& (clientdata3 == otherCLIENTID.clientdata3);
	} else {
	    return false;
	}
    }

    /**
     * @return
     * The hash code of this CLIENTID.
     */
    public int hashCode()
    {
	// We use ^ as this is apparently a suitable operator for
	// this purpose (see java.util.Map.Entry.hashCode())
	
	return clientdata0 ^ clientdata1 ^ clientdata2 ^ clientdata3;
    }

    private static String hex(int i)
    {
	String hex = Integer.toHexString(i).toUpperCase();

	switch (hex.length()) {
	    case 1:
		return "000" + hex;
	    case 2:
		return "00" + hex;
	    case 3:
		return "0" + hex;
	    default:
		return hex;
	}
    }

    /**
     * @internal
     */
    public String toString()
    {
	return "[" + hex(clientdata0) + ':' + hex(clientdata1) + ':'
	    + hex(clientdata2) + ':' + hex(clientdata3) + "]";
    }
}
