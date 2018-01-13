/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.xa;

import javax.transaction.xa.Xid;

import java.io.Serializable;

import java.util.Arrays;

/**
 * @y.exclude
 */
public class XID implements Xid, Serializable
{
    private int formatID;

    private byte[] gtrid;

    private byte[] bqual;

    // Only used for test
 
    XID(int formatID, byte[] gtrid, byte[] bqual)
    {
	this.formatID = formatID;

	this.gtrid = gtrid;

	this.bqual = bqual;
    }

    public int getFormatId()
    {
	return formatID;
    }

    public byte[] getGlobalTransactionId()
    {
	return gtrid;
    }

    public byte[] getBranchQualifier()
    {
	return bqual;
    }
 
    public boolean equals(Object object)
    {
        if ((object != null) && (object instanceof Xid)) {
            return equals(this, (Xid) object);
        } else {
            return false;
        }
    }

    public int hashCode()
    {
        return formatID + hashCode(gtrid) + hashCode(bqual);
    }
    
    public String toString()
    {
        return "XID(formatID=" + formatID + ", gtrid="
            + toString(gtrid) + ", bqual=" + toString(bqual) + ")";
    }
    
    public static boolean equals(Xid a, Xid b)
    {
        return (a.getFormatId() == b.getFormatId())
            && Arrays.equals(a.getGlobalTransactionId(), 
                b.getGlobalTransactionId())
            && Arrays.equals(a.getBranchQualifier(), 
                b.getBranchQualifier());
    }
    
    private static int hashCode(byte[] bytes)
    {
        // See Arrays.hashCode()/List.hashCode() in Java 1.5 for rationale
    
        int hashCode = 1;
        for (int i = 0; i < bytes.length; i++) {
            hashCode = 31 * hashCode + bytes[i];
        }
        return hashCode;
    }
    
    private static String toString(byte[] bytes)
    {
        if (bytes == null) {
            return null;
        } else {
            StringBuffer buffer = new StringBuffer("[");
            for (int i = 0; i < bytes.length; i++) {
                if (i > 0) {
                    buffer.append(' ');
                }
                buffer.append(Integer.toHexString(bytes[i]));
            }
            buffer.append("]");
            return buffer.toString();
        }
    }
}
