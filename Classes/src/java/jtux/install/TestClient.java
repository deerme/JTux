/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.install;

import jtux.atmi.ATMI;

import jtux.holders.ByteBufferHolder;
import jtux.holders.IntHolder;

public class TestClient
{
    public static void main(String[] args)
    {
	ByteBufferHolder replyRef = new ByteBufferHolder();
	IntHolder lenOut = new IntHolder();

	ATMI.tpinit(null);
	try {
	    replyRef.value = ATMI.tpalloc("STRING", null, 1024);
	    try {
		ATMI.tpcall("TEST", null, 0, replyRef, lenOut, 0);
		byte[] bytes = new byte[lenOut.value - 1];
		replyRef.value.get(bytes);
		String message = new String(bytes);
		System.out.println(message);
	    } finally {
		ATMI.tpfree(replyRef.value);
	    }
	} finally {
	    ATMI.tpterm();
	}
    }
}
