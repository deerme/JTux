/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import java.io.Serializable;

/**
 * Java version of Tuxedo's TPTRANID data structure.
 */
public class TPTRANID implements Serializable
{
    private int info0;

    private int info1;

    private int info2;

    private int info3;

    private int info4;

    private int info5;

    TPTRANID()
    {
	// Nothing to do, just make sure it is not public.
    }

    /**
     * Tests whether this TPTRANID equals another object.
     *
     * @param other
     * The other object to compare to.
     *
     * @return
     * True if the other object is a TPTRANID that has the same value
     * as this TPTRANID.
     */
    public boolean equals(Object other)
    {
	if ((other != null) && (other instanceof TPTRANID)) {
	    TPTRANID otherTPTRANID = (TPTRANID) other;
	    return (info0 == otherTPTRANID.info0)
		&& (info1 == otherTPTRANID.info1)
		&& (info2 == otherTPTRANID.info2)
		&& (info3 == otherTPTRANID.info3)
		&& (info4 == otherTPTRANID.info4)
		&& (info5 == otherTPTRANID.info5);
	} else {
	    return false;
	}
    }

    /**
     * Returns the hash code of this TPTRANID.
     *
     * @return
     * The hash code of this TPTRANID.
     */
    public int hashCode()
    {
	// We use ^ as this is apparently a suitable operator for
	// this purpose (see java.util.Map.Entry.hashCode())
	
	return info0 ^ info1 ^ info2 ^ info3 ^ info4 ^ info5;
    }
}
