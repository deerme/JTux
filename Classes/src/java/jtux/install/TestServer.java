/*
 * Copyright 2004 OTP Systems Oy. All right reserved.
 */

package jtux.install;

import jtux.atmi.ATMI;
import jtux.atmi.TPSVCINFO;

import java.nio.ByteBuffer;

public class TestServer
{
    public static void TEST(TPSVCINFO svcInfo)
    {
	String message = "Installation OK";
	byte[] bytes = message.getBytes();
	ByteBuffer reply = ATMI.tpalloc("STRING", null, bytes.length + 1);
	reply.put(bytes);
	reply.put((byte) 0);
	ATMI.tpreturn(ATMI.TPSUCCESS, 0, reply, 0, 0);
    }
}
