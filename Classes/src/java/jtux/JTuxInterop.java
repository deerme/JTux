/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux;

import jtux.atmi.TPQCTL;
import jtux.atmi.TPTRANID;
import jtux.atmi.TPINIT;
import jtux.atmi.TPEVCTL;
import jtux.atmi.CLIENTID;

import jtux.holders.IntHolder;
import jtux.holders.StringHolder;
import jtux.holders.ByteBufferHolder;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.File;

import java.util.HashMap;

import java.nio.ByteBuffer;

import java.lang.reflect.Method;

/**
 * @internal
 */
public class JTuxInterop
{
    static boolean isLibraryLoaded = false;

    static
    {
	if (JTuxInterop.class.getResource("JTux.tag") != null) {
	    System.loadLibrary("JTuxInterop");
	    isLibraryLoaded = true;
	} else if (JTuxInterop.class.getResource("JTuxWS.tag") != null) {
	    System.loadLibrary("JTuxWSInterop");
	    isLibraryLoaded = true;
	} else {
	    System.err.println("Warning: No JTuxInterop library loaded");
	}
    }

    public static native String tuxgetenv(String name);

    public static native int tuxputenv(String setting);

    public static native int tuxreadenv(String file, String label);

    public static native void userlog(byte[] message);

    public static native int tperrno();

    public static native String tpstrerror(int err);

    public static native int tpurcode();

    public static native ByteBuffer tpalloc(String type, String subtype,
	int size);

    public static native int tptypes(ByteBuffer buffer, StringHolder type,
	StringHolder subtype);

    public static native ByteBuffer tprealloc(ByteBuffer buffer, int size);

    public static native void tpfree(ByteBuffer buffer);

    public static native int tpadmcall(ByteBuffer in, ByteBufferHolder out,
	int flags);

    public static native int tpchkauth();

    public static native int tpinit(TPINIT tpinfo);

    public static native int tpterm();

    public static native int tpacall(String svc, ByteBuffer data, int len,
	int flags);

    public static native int tpgetrply(IntHolder cd, ByteBufferHolder data,
	IntHolder len, int flags);

    public static native int tpcancel(int cd);

    public static native int tpcall(String svc, ByteBuffer idata, int ilen,
	ByteBufferHolder odata, IntHolder olen, int flags);

    public static native int tpconnect(String svc, ByteBuffer data, int len,
	int flags);

    public static native int tpsend(int cd, ByteBuffer data, int len,
	int flags, IntHolder revent);

    public static native int tprecv(int cd, ByteBufferHolder data, 
	IntHolder len, int flags, IntHolder revent);

    public static native int tpdiscon(int cd);

    public static native int tpenqueue(String qspace, String qname, 
	TPQCTL ctl, ByteBuffer data, int len, int flags);

    public static native int tpdequeue(String qspace, String qname, 
	TPQCTL ctl, ByteBufferHolder data, IntHolder len, int flags);

    public static native int tpbegin(int timeout, int flags);

    public static native int tpabort(int flags);

    public static native int tpcommit(int flags);

    public static native int tpgetlev();

    public static native int tpscmt(int flags);

    public static native int tpsuspend(TPTRANID tranid, int flags);

    public static native int tpresume(TPTRANID tranid, int flags);

    public static native int tpopen();

    public static native int tpclose();

    public static native int tpgprio();

    public static native int tpsprio(int prio, int flags);

    public static native int tpgetctxt(IntHolder context, int flags);

    public static native int tpsetctxt(int context, int flags);

    public static native boolean tpsetunsol(boolean clear);

    public static native int tpchkunsol();

    public static native int tpbroadcast(String lmid, String usrname,
	String cltname, ByteBuffer data, int len, int flags);

    public static native int tpnotify(CLIENTID clientid, ByteBuffer data,
	int len, int flags);

    public static native int tppost(String eventname, ByteBuffer data,
	int len, int flags);

    public static native int tpsubscribe(String eventexpr, String filter,
	TPEVCTL ctl, int flags);

    public static native int tpunsubscribe(int subscription, int flags);

    public static native int tpgetmbenc(ByteBuffer buffer, StringHolder enc,
	int flags);

    public static native int tpsetmbenc(ByteBuffer buffer, String enc, 
	int flags);

    public static native int tpadvertise(String svc, String func);

    public static native int tpunadvertise(String svc);

    public static native int tpreturn(int rval, int rcode, ByteBuffer data,
	int len, int flags);

    public static native int tpforward(String svc, ByteBuffer data,
	int len, int flags);

    public static native int Ferror32();

    public static native String Fstrerror32(int errnum);

    public static native int CFaddByte32(ByteBuffer fbfr, int fldid,
	byte value);

    public static native int CFaddShort32(ByteBuffer fbfr, int fldid,
	short value);

    public static native int CFaddInt32(ByteBuffer fbfr, int fldid,
	int value);

    public static native int CFaddFloat32(ByteBuffer fbfr, int fldid,
	float value);

    public static native int CFaddDouble32(ByteBuffer fbfr, int fldid,
	double value);

    public static native int CFaddBytes32(ByteBuffer fbfr, int fldid,
	byte[] value);

    public static native int CFchgByte32(ByteBuffer fbfr, int fldid,
	int occ, byte value);

    public static native int CFchgShort32(ByteBuffer fbfr, int fldid,
	int occ, short value);

    public static native int CFchgInt32(ByteBuffer fbfr, int fldid,
	int occ, int value);

    public static native int CFchgFloat32(ByteBuffer fbfr, int fldid,
	int occ, float value);

    public static native int CFchgDouble32(ByteBuffer fbfr, int fldid,
	int occ, double value);

    public static native int CFchgBytes32(ByteBuffer fbfr, int fldid,
	int occ, byte[] value);

    public static native byte CFgetByte32(ByteBuffer fbfr, int fldid,
	int occ);

    public static native short CFgetShort32(ByteBuffer fbfr, int fldid,
	int occ);

    public static native int CFgetInt32(ByteBuffer fbfr, int fldid,
	int occ);

    public static native float CFgetFloat32(ByteBuffer fbfr, int fldid,
	int occ);

    public static native double CFgetDouble32(ByteBuffer fbfr, int fldid,
	int occ);

    public static native byte[] CFfindBytes32(ByteBuffer fbfr, int fldid,
	int occ);

    public static native int Fdel32(ByteBuffer fbfr, int fldid, int occ);

    public static native int Fdelall32(ByteBuffer fbfr, int fldid);

    public static native int Finit32(ByteBuffer fbfr, int buflen);

    public static native int Fldid32(String name);

    public static native int Fldno32(int fldid);

    public static native int Fldtype32(int fldid);

    public static native int Flen32(ByteBuffer fbfr, int fldid, int occ);

    public static native int Fmkfldid32(int fldtype, int fldno);

    public static native String Fname32(int fldid);

    // Fneeded32() skipped because I don't trust this method.

    public static native int Fnext32(ByteBuffer fbfr, IntHolder fldid,
	IntHolder occ);

    public static native int Fnum32(ByteBuffer fbfr);

    public static native int Foccur32(ByteBuffer fbfr, int fldid);

    // Fpres() skipped because of weird (i.e. absent) failure semantics

    public static native int Fsizeof32(ByteBuffer fbfr);

    public static native String Ftype32(int fldid);

    public static native int Funused32(ByteBuffer fbfr);

    public static native int Fused32(ByteBuffer fbfr);

    public static native void Fidnm_unload32();

    public static native void Fnmid_unload32();
}
