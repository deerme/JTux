/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

import jtux.holders.IntHolder;
import jtux.holders.StringHolder;
import jtux.holders.ByteBufferHolder;

import jtux.JTuxInterop;
import jtux.TUX;

import java.nio.ByteBuffer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import java.util.Map;
import java.util.HashMap;

/**
 * Provides methods and constants for executing ATMI system calls.
 */
public class ATMI
{
    private ATMI() { /* Nothing to do, just make sure it is not public. */ }

    // The symbol table below was generated using OTPTuxSymbolDumper

    /** ATMI system call flag */
    public final static int TPNOFLAGS = 0x0;

    /** ATMI system call flag */
    public final static int TPNOBLOCK = 0x1;

    /** ATMI system call flag */
    public final static int TPSIGRSTRT = 0x2;

    /** ATMI system call flag */
    public final static int TPNOREPLY = 0x4;

    /** ATMI system call flag */
    public final static int TPNOTRAN = 0x8;

    /** ATMI system call flag */
    public final static int TPTRAN = 0x10;

    /** ATMI system call flag */
    public final static int TPNOTIME = 0x20;

    /** ATMI system call flag */
    public final static int TPABSOLUTE = 0x40;

    /** ATMI system call flag */
    public final static int TPGETANY = 0x80;

    /** ATMI system call flag */
    public final static int TPNOCHANGE = 0x100;

    /** ATMI system call flag */
    public final static int TPCONV = 0x400;

    /** ATMI system call flag */
    public final static int TPSENDONLY = 0x800;

    /** ATMI system call flag */
    public final static int TPRECVONLY = 0x1000;

    /** ATMI system call flag */
    public final static int TPACK = 0x2000;

    /** Return value for service routines */
    public final static int TPFAIL = 0x1;

    /** Return value for service routines */
    public final static int TPEXIT = 0x8000000;

    /** Return value for service routines */
    public final static int TPSUCCESS = 0x2;

    /** Possible TP_COMMIT_CONTROL value */
    public final static int TP_CMT_LOGGED = 1;

    /** Possible TP_COMMIT_CONTROL value */
    public final static int TP_CMT_COMPLETE = 2;

    /** Unsolicited notification mode */
    public final static int TPU_MASK = 0x47;

    /** Unsolicited notification mode */
    public final static int TPU_SIG = 0x1;

    /** Unsolicited notification mode */
    public final static int TPU_DIP = 0x2;

    /** Unsolicited notification mode */
    public final static int TPU_IGN = 0x4;

    /** Unsolicited notification mode */
    public final static int TPU_THREAD = 0x40;

    /** System access mode */
    public final static int TPSA_FASTPATH = 0x8;

    /** System access mode */
    public final static int TPSA_PROTECTED = 0x10;

    /** Multicontexts mode */
    public final static int TPMULTICONTEXTS = 0x20;

    /** Tuxedo/Q flag */
    public final static int TPQCORRID = 0x1;

    /** Tuxedo/Q flag */
    public final static int TPQFAILUREQ = 0x2;

    /** Tuxedo/Q flag */
    public final static int TPQBEFOREMSGID = 0x4;

    /** Tuxedo/Q flag */
    public final static int TPQMSGID = 0x10;

    /** Tuxedo/Q flag */
    public final static int TPQPRIORITY = 0x20;

    /** Tuxedo/Q flag */
    public final static int TPQTOP = 0x40;

    /** Tuxedo/Q flag */
    public final static int TPQWAIT = 0x80;

    /** Tuxedo/Q flag */
    public final static int TPQREPLYQ = 0x100;

    /** Tuxedo/Q flag */
    public final static int TPQTIME_ABS = 0x200;

    /** Tuxedo/Q flag */
    public final static int TPQTIME_REL = 0x400;

    /** Tuxedo/Q flag */
    public final static int TPQPEEK = 0x1000;

    /** Tuxedo/Q flag */
    public final static int TPQDELIVERYQOS = 0x2000;

    /** Tuxedo/Q flag */
    public final static int TPQREPLYQOS = 0x4000;

    /** Tuxedo/Q flag */
    public final static int TPQEXPTIME_ABS = 0x8000;

    /** Tuxedo/Q flag */
    public final static int TPQEXPTIME_REL = 0x10000;

    /** Tuxedo/Q flag */
    public final static int TPQEXPTIME_NONE = 0x20000;

    /** Tuxedo/Q flag */
    public final static int TPQGETBYMSGID = 0x40008;

    /** Tuxedo/Q flag */
    public final static int TPQGETBYCORRID = 0x80800;

    /** Tuxedo/Q QoS */
    public final static int TPQQOSDEFAULTPERSIST = 1;

    /** Tuxedo/Q QoS */
    public final static int TPQQOSPERSISTENT = 2;

    /** Tuxedo/Q QoS */
    public final static int TPQQOSNONPERSISTENT = 4;

    /** ATMI error code */
    public final static int TPEABORT = 1;

    /** ATMI error code */
    public final static int TPEBADDESC = 2;

    /** ATMI error code */
    public final static int TPEBLOCK = 3;

    /** ATMI error code */
    public final static int TPEINVAL = 4;

    /** ATMI error code */
    public final static int TPELIMIT = 5;

    /** ATMI error code */
    public final static int TPENOENT = 6;

    /** ATMI error code */
    public final static int TPEOS = 7;

    /** ATMI error code */
    public final static int TPEPERM = 8;

    /** ATMI error code */
    public final static int TPEPROTO = 9;

    /** ATMI error code */
    public final static int TPESVCERR = 10;

    /** ATMI error code */
    public final static int TPESVCFAIL = 11;

    /** ATMI error code */
    public final static int TPESYSTEM = 12;

    /** ATMI error code */
    public final static int TPETIME = 13;

    /** ATMI error code */
    public final static int TPETRAN = 14;

    /** ATMI error code */
    public final static int TPGOTSIG = 15;

    /** ATMI error code */
    public final static int TPERMERR = 16;

    /** ATMI error code */
    public final static int TPEITYPE = 17;

    /** ATMI error code */
    public final static int TPEOTYPE = 18;

    /** ATMI error code */
    public final static int TPERELEASE = 19;

    /** ATMI error code */
    public final static int TPEHAZARD = 20;

    /** ATMI error code */
    public final static int TPEHEURISTIC = 21;

    /** ATMI error code */
    public final static int TPEEVENT = 22;

    /** ATMI error code */
    public final static int TPEMATCH = 23;

    /** ATMI error code */
    public final static int TPEDIAGNOSTIC = 24;

    /** ATMI error code */
    public final static int TPEMIB = 25;

    /** Conversational event code */
    public final static int TPEV_DISCONIMM = 1;

    /** Conversational event code */
    public final static int TPEV_SVCERR = 2;

    /** Conversational event code */
    public final static int TPEV_SVCFAIL = 4;

    /** Conversational event code */
    public final static int TPEV_SVCSUCC = 8;

    /** Conversational event code */
    public final static int TPEV_SENDONLY = 32;

    /** Tuxedo/Q error code */
    public final static int QMEINVAL = -1;

    /** Tuxedo/Q error code */
    public final static int QMEBADRMID = -2;

    /** Tuxedo/Q error code */
    public final static int QMENOTOPEN = -3;

    /** Tuxedo/Q error code */
    public final static int QMETRAN = -4;

    /** Tuxedo/Q error code */
    public final static int QMEBADMSGID = -5;

    /** Tuxedo/Q error code */
    public final static int QMESYSTEM = -6;

    /** Tuxedo/Q error code */
    public final static int QMEOS = -7;

    /** Tuxedo/Q error code */
    public final static int QMEABORTED = -8;

    /** Tuxedo/Q error code */
    public final static int QMENOTA = -8;

    /** Tuxedo/Q error code */
    public final static int QMEPROTO = -9;

    /** Tuxedo/Q error code */
    public final static int QMEBADQUEUE = -10;

    /** Tuxedo/Q error code */
    public final static int QMENOMSG = -11;

    /** Tuxedo/Q error code */
    public final static int QMEINUSE = -12;

    /** Tuxedo/Q error code */
    public final static int QMENOSPACE = -13;

    /** Tuxedo/Q error code */
    public final static int QMERELEASE = -14;

    /** Tuxedo/Q error code */
    public final static int QMEINVHANDLE = -15;

    /** Tuxedo/Q error code */
    public final static int QMESHARE = -16;

    /** Event broker flag */
    public final static int TPEVSERVICE = 1;

    /** Event broker flag */
    public final static int TPEVQUEUE = 2;

    /** Event broker flag */
    public final static int TPEVTRAN = 4;

    /** Event broker flag */
    public final static int TPEVPERSIST = 8;

    /** Authentication level */
    public final static int TPNOAUTH = 0;

    /** Authentication level */
    public final static int TPSYSAUTH = 1;

    /** Authentication level */
    public final static int TPAPPAUTH = 2;

    /** Context identifier */
    public final static int TPSINGLECONTEXT = 0;

    /** Context identifier */
    public final static int TPNULLCONTEXT = -2;

    /** Context identifier */
    public final static int TPINVALIDCONTEXT = -1;

    /** ATMI error detail code */
    public final static int TPED_CLIENTDISCONNECTED = 6;

    /** ATMI error detail code */
    public final static int TPED_DECRYPTION_FAILURE = 11;

    /** ATMI error detail code */
    public final static int TPED_DOMAINUNREACHABLE = 5;

    /** ATMI error detail code */
    public final static int TPED_INVALID_CERTIFICATE = 9;

    /** ATMI error detail code */
    public final static int TPED_INVALID_SIGNATURE = 10;

    /** ATMI error detail code */
    public final static int TPED_INVALIDCONTEXT = 12;

    /** ATMI error detail code */
    public final static int TPED_INVALID_XA_TRANSACTION = 13;

    /** ATMI error detail code */
    public final static int TPED_NOCLIENT = 4;

    /** ATMI error detail code */
    public final static int TPED_NOUNSOLHANDLER = 3;

    /** ATMI error detail code */
    public final static int TPED_SVCTIMEOUT = 1;

    /** ATMI error detail code */
    public final static int TPED_TERM = 2;

    /** Remove encoding flag */
    public final static int RM_ENC = 0x1;

    static TPException newTPException(int err)
    {
	switch (err) {
	    case ATMI.TPEABORT:
		return new TPEABORT();
	    case ATMI.TPEBADDESC:
		return new TPEBADDESC();
	    case ATMI.TPEBLOCK:
		return new TPEBLOCK();
	    case ATMI.TPEINVAL:
		return new TPEINVAL();
	    case ATMI.TPELIMIT:
		return new TPELIMIT();
	    case ATMI.TPENOENT:
		return new TPENOENT();
	    case ATMI.TPEOS:
		return new TPEOS();
	    case ATMI.TPEPERM:
		return new TPEPERM();
	    case ATMI.TPEPROTO:
		return new TPEPROTO();
	    case ATMI.TPESVCERR:
		return new TPESVCERR();
	    case ATMI.TPESVCFAIL:
		return new TPESVCFAIL();
	    case ATMI.TPESYSTEM:
		return new TPESYSTEM();
	    case ATMI.TPETIME:
		return new TPETIME();
	    case ATMI.TPETRAN:
		return new TPETRAN();
	    case ATMI.TPGOTSIG:
		return new TPGOTSIG();
	    case ATMI.TPERMERR:
		return new TPERMERR();
	    case ATMI.TPEITYPE:
		return new TPEITYPE();
	    case ATMI.TPEOTYPE:
		return new TPEOTYPE();
	    case ATMI.TPERELEASE:
		return new TPERELEASE();
	    case ATMI.TPEHAZARD:
		return new TPEHAZARD();
	    case ATMI.TPEHEURISTIC:
		return new TPEHEURISTIC();
	    case ATMI.TPEMATCH:
		return new TPEMATCH();
	    case ATMI.TPEMIB:
		return new TPEMIB();
	    default:
		return new TPException(err);
	}
    }

    static TPException newTPException()
    {
	return newTPException(JTuxInterop.tperrno());
    }

    static TPEEVENT newTPEEVENT(int event)
    {
	switch (event) {
	    case ATMI.TPEV_DISCONIMM:
	        return new TPEV_DISCONIMM();
	    case ATMI.TPEV_SVCERR:
	        return new TPEV_SVCERR();
	    case ATMI.TPEV_SVCFAIL:
	        return new TPEV_SVCFAIL();
	    case ATMI.TPEV_SVCSUCC:
	        return new TPEV_SVCSUCC();
	    case ATMI.TPEV_SENDONLY:
	        return new TPEV_SENDONLY();
	    default:
	        return new TPEEVENT(event);
	}
    }

    static TPEDIAGNOSTIC newTPEDIAGNOSTIC(int diagnostic)
    {
	switch (diagnostic)
	{
	    case ATMI.QMEINVAL:
                return new QMEINVAL();
	    case ATMI.QMEBADRMID:
                return new QMEBADRMID();
	    case ATMI.QMENOTOPEN:
                return new QMENOTOPEN();
	    case ATMI.QMETRAN:
                return new QMETRAN();
	    case ATMI.QMEBADMSGID:
                return new QMEBADMSGID();
	    case ATMI.QMESYSTEM:
                return new QMESYSTEM();
	    case ATMI.QMEOS:
                return new QMEOS();
	    case ATMI.QMEABORTED:
                return new QMEABORTED();
	    case ATMI.QMEPROTO:
                return new QMEPROTO();
	    case ATMI.QMEBADQUEUE:
                return new QMEBADQUEUE();
	    case ATMI.QMENOMSG:
                return new QMENOMSG();
	    case ATMI.QMEINUSE:
                return new QMEINUSE();
	    case ATMI.QMENOSPACE:
                return new QMENOSPACE();
	    case ATMI.QMERELEASE:
                return new QMERELEASE();
	    case ATMI.QMEINVHANDLE:
                return new QMEINVHANDLE();
	    case ATMI.QMESHARE:
                return new QMESHARE();
	    default:
	        return new TPEDIAGNOSTIC(diagnostic);
	}
    }
  
    /**
     * Returns the application specific return code of the last
     * service invocation.
     *
     * @return
     * The application specific return code of the last
     * service invocation.
     */
    public static int tpurcode()
    {
	return JTuxInterop.tpurcode();
    }

    /**
     * Allocates a typed buffer.
     *
     * @param type
     * The type of the typed buffer; must not be null.
     *
     * @param subtype 
     * The subtype of the typed buffer; may be null.
     *
     * @param size
     * The size of the typed buffer.
     *
     * @return
     * The newly allocated typed buffer.
     * The position of this buffer is 0 and its limit is {@paramref size}.
     *
     * @remarks
     * The returned buffer must be freed using {@link #tpfree} when
     * no longer used.
     *
     * @throws TPException
     * See the Tuxedo tpalloc(3c) manual page.
     *
     * @example
     * The following Java code fragment shows how to allocate a typed
     * buffer and how to make sure that it gets freed when it is not
     * used anymore. 
     * <pre>
     * ByteBuffer buffer = ATMI.tpalloc("STRING", null, 1024);
     * try {
     *     ... // Use buffer
     * } finally {
     *     ATMI.tpfree(buffer);
     * }
     * </pre>
     */
    public static ByteBuffer tpalloc(String type, String subtype,
	int size)
    {
	ByteBuffer result = JTuxInterop.tpalloc(type, subtype, size);
	if (result == null) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Returns the type and size of a typed buffer.
     *
     * @param buffer
     * The typed buffer; must have been allocated using {@link #tpalloc}.
     *
     * @param typeOut
     * Gets updated with the type of the typed buffer.
     *
     * @return
     * The size of the typed buffer.
     *
     * @throws TPException
     * See the Tuxedo tptypes(3c) manual page.
     *
     * @example
     * The following Java code fragment shows how to get the
     * type and size of a typed buffer. 
     * <pre>
     * ByteBuffer buffer = ...;
     * StringHolder type = new StringHolder();
     * int size = ATMI.tptypes(buffer, type);
     * System.out.println("type=" + type.value);
     * System.out.println("size=" + size);
     * </pre>
     */
    public static int tptypes(ByteBuffer buffer, StringHolder typeOut)
    {
	int result = JTuxInterop.tptypes(buffer, typeOut, null);
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Returns the type, subtype and size of a typed buffer.
     *
     * @param buffer
     * The typed buffer; must have been allocated using {@link #tpalloc}.
     *
     * @param typeOut
     * Gets updated with the type of the typed buffer.
     *
     * @param subtypeOut
     * Gets updated with the subtype of the typed buffer.
     *
     * @return
     * The size of the typed buffer.
     *
     * @throws TPException
     * See the Tuxedo tptypes(3c) manual page.
     *
     * @example
     * The following Java code fragment shows how to get the
     * type, subtype and size of a typed buffer. 
     * <pre>
     * ByteBuffer buffer = ...;
     * StringHolder type = new StringHolder();
     * StringHolder subtype = new StringHolder();
     * int size = ATMI.tptypes(buffer, type, subtype);
     * System.out.println("type=" + type.value);
     * System.out.println("subtype=" + subtype.value);
     * System.out.println("size=" + size);
     * </pre>
     */
    public static int tptypes(ByteBuffer buffer, StringHolder typeOut,
	StringHolder subtypeOut)
    {
	int result = JTuxInterop.tptypes(buffer, typeOut, subtypeOut);
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Reallocates a typed buffer.
     *
     * @param buffer
     * The typed buffer to reallocate; must have been allocated using
     * {@link #tpalloc}.
     *
     * @param newSize
     * The new size of the buffer.
     *
     * @return
     * The reallocated buffer.
     * The position of this buffer is the same as the position of the
     * original buffer and its limit is {@paramref newSize}.
     *
     * @throws TPException
     * See the Tuxedo tprealloc(3c) manual page.
     */
    public static ByteBuffer tprealloc(ByteBuffer buffer, int newSize)
    {
	ByteBuffer result = JTuxInterop.tprealloc(buffer, newSize);
	if (result == null) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Frees a typed buffer.
     *
     * @param buffer
     * The typed buffer to free; must have been allocated using
     * {@link #tpalloc}.
     */
    public static void tpfree(ByteBuffer buffer)
    {
	JTuxInterop.tpfree(buffer);
    }

    /**
     * Peforms an administrative Tuxedo operation.
     *
     * @ifndef WSCLIENT
     *
     * @param request
     * A typed buffer of type FML32 containing the administrative request.
     *
     * @param replyRef
     * A typed buffer of type FML32 allocated using using {@link #tpalloc}.
     * Gets updated with a buffer containing the reply received in response
     * to the administrative request.
     * The position of this buffer is 0 and its limit is the length of the
     * FML32 message contained in it.
     *
     * @param flags
     * See the Tuxedo tpadmcall(3c) manual page.
     * 
     * @throws TPEMIB
     * The administrative request failed.
     * In this case, {@paramref replyRef} is updated as described above.
     *
     * @throws TPException
     * See the Tuxedo tpadmcall(3c) manual page.
     */
    public static void tpadmcall(ByteBuffer request,
	ByteBufferHolder replyRef, int flags)
    {
	if (JTuxInterop.tpadmcall(request, replyRef, flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Returns the application context of the current thread.
     *
     * @param flags
     * See the Tuxedo tpgetctxt(3c) manual page.
     *
     * @return
     * The application context of the current thread.
     *
     * @throws TPException
     * See the Tuxedo tpgetctxt(3c) manual page.
     */
    public static int tpgetctxt(int flags)
    {
	IntHolder ctxtOut = new IntHolder();
	if (JTuxInterop.tpgetctxt(ctxtOut, flags) == -1) {
	    throw newTPException();
	}
	return ctxtOut.value;
    }

    /**
     * Sets the application context of the current thread.
     *
     * @param ctxt
     * The application context to set.
     *
     * @param flags
     * See the Tuxedo tpsetctxt(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpsetctxt(3c) manual page.
     */
    public static void tpsetctxt(int ctxt, int flags)
    {
	if (JTuxInterop.tpsetctxt(ctxt, flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Returns the authentication level required to join a running
     * Tuxedo application.
     *
     * @return
     * The authentication level required to join the application.
     *
     * @throws TPException
     * See the Tuxedo tpchkauth(3c) manual page.
     */
    public static int tpchkauth()
    {
	int result = JTuxInterop.tpchkauth();
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /*
     * About tpinit() and unsolicited message handling:
     *
     * The tpsetunsol(3c) entry of the Tuxedo ATMI reference guide talks
     * about a 'per-process default unsolicited message handler'. If you
     * read the documentation carefully, you can conclude that this
     * per-process default unsolicited message handler gets copied to 
     * the context by tpinit(). This means that you can change the
     * per-process default unsolicited message handlers without affecting
     * existing contexts. Existing contexts keep working with the previous
     * per-process default unsolicited message handler (unless overridden
     * by a call to tpsetunsol() in that context).
     * 
     * But now the tricky part. It turns out that tpterm() does *not* clear
     * the unsol handler for the context (you can test this with a simple
     * test client). Thus subsequent incarnations of the same context inherit
     * the unsol handler left by the previous incarnation.
     * It also turns out that a NULL unsol handler is not the same as no
     * context handler. For example, if you set the per-process default
     * unsolicited message handler before creating any contexts and then
     * set the unsol handler of a particular context to NULL, the next
     * incarnation of that context will have a NULL context handler, not the
     * per-process default one (another simple test client will show this).
     */

    private static HashMap unsolHandlers = new HashMap();
        // Accepts null handlers to differentiate from no handler

    /*
     * This method gets called by the tpunsol() function defined
     * in the JTuxInterop DLL.
     */
    private static void tpunsol(ByteBuffer data, int len, int flags)
    {
	int ctxt = tpgetctxt(0);
	Integer key = new Integer(ctxt);

	UnsolHandler handler;

	synchronized (unsolHandlers) {
	    if (!unsolHandlers.containsKey(key)) { // No handler
	        throw new IllegalStateException(ATMI.class.getName()
		    + ".tpunsol() called but no handler in this context");
	    }
	    handler = (UnsolHandler) unsolHandlers.get(key);
	    if (handler == null) { // NULL handler
	        throw new IllegalStateException(ATMI.class.getName()
		    + ".tpunsol() called but handler in this context is null");
	    }
	}

	try {
	    handler.tpunsol(data, len, flags);
	} catch (Exception e) {
	    TUX.userlog("ERROR: Exception handling unsolicited message:");
	    e.printStackTrace(TUX.ULOG);
	}
    }

    private static UnsolHandler perProcessDefaultUnsolHandler = null;

    /**
     * Sets the unsolicited message handler of the current application
     * context.
     *
     * @param handler
     * The unsolicited message handler to set; may be null.
     *
     * @return
     * The previous unsolicited message handler of the current application
     * context; may be null.
     *
     * @throws TPException
     * See the Tuxedo tpsetunsol(3c) manual page.
     *
     * @example
     * The following Java code fragment shows how to set the unsolicited
     * message handler of the current application context:
     * <pre>
     * class MyUnsol implements UnsolHandler
     * {
     *     public void tpunsol(ByteBuffer message, int len, int flags)
     *     {
     *         ... // Handle unsolicited message
     *     }   
     * }
     *
     * ATMI.tpsetunsol(new MyUnsol());
     * </pre>
     */
    public static UnsolHandler tpsetunsol(UnsolHandler handler)
    {
	if (!JTuxInterop.tpsetunsol(handler == null)) {
	    throw newTPException();
	}

	int ctxt = tpgetctxt(0);
	if (ctxt == TPNULLCONTEXT) {
	    UnsolHandler oldHandler = perProcessDefaultUnsolHandler;
	    perProcessDefaultUnsolHandler = handler;
	    return oldHandler;
	} else {
	    Integer key = new Integer(ctxt);
	    synchronized (unsolHandlers) {
		// handler == null means NULL handler, not no handler (!)
		return (UnsolHandler) unsolHandlers.put(key, handler);
	    }
	}
    }

    /**
     * Logs a client on to a running Tuxedo application.
     *
     * @param tpinfo
     * Client identification data; may be null.
     *
     * @remarks
     * The client must be logged off using {@link #tpterm} when it
     * doesn't use the application's services anymore.
     *
     * @throws TPEPERM
     * The client does not have permission to join the application.
     *
     * @throws TPException
     * See the Tuxedo tpinit(3c) manual page.
     * 
     * @example
     * The following Java code fragment shows how to log a client on
     * to a running Tuxedo application and how to make sure that
     * it gets logged off when it doesn't use the application's services
     * anymore.
     * <pre>
     * TPINIT tpinfo = new TPINIT();
     * tpinfo.usrname = ...;
     * tpinfo.passwd = ...;
     *
     * ATMI.tpinit(tpinfo);
     * try {
     *     ... // Use application services
     * } finally {
     *     ATMI.tpterm();
     * }
     * </pre>
     */
    public static void tpinit(TPINIT tpinfo)
    {
	if (JTuxInterop.tpinit(tpinfo) == -1) {
	    throw newTPException();
	}

	int ctxt = tpgetctxt(0); // Must get context *after* tpinit()
	Integer key = new Integer(ctxt);

	synchronized (unsolHandlers) {
	    if (!unsolHandlers.containsKey(key)) { // No handler
	        if (perProcessDefaultUnsolHandler != null) {
	            unsolHandlers.put(key, perProcessDefaultUnsolHandler);
		}
	    }
	}
    }

    /**
     * Logs a client off from a running Tuxedo application.
     *
     * @throws TPException
     * See the Tuxedo tpterm(3c) manual page.
     */
    public static void tpterm()
    {
        // Note that tpterm() does *not* clear the unsol handler.
	
	if (JTuxInterop.tpterm() == -1) {
	    throw newTPException();
	}
    }

    /**
     * Sends a request to a Tuxedo service without waiting for a reply.
     *
     * @param svc
     * The name of the Tuxedo service.
     *
     * @param request
     * A typed buffer containing the request; may be null.
     *
     * @param len
     * The length of the data in {@paramref request}.
     *
     * @param flags
     * See the Tuxedo tpacall(3c) manual page.
     *
     * @return
     * A call descriptor for use by {@link #tpgetrply} or {@link #tpcancel}.
     *
     * @remarks
     * The caller must make sure to either complete the outstanding
     * service call using {@link #tpgetrply}, or cancel it using
     * {@link #tpcancel}.
     *
     * @throws TPException
     * See the Tuxedo tpacall(3c) manual page.
     */
    public static int tpacall(String svc, ByteBuffer request, int len,
	int flags)
    {
	int result = JTuxInterop.tpacall(svc, request, len, flags);
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Waits for the reply of an outstanding service call.
     *
     * @param cdRef
     * If the {@link #TPGETANY} flag is set then the input value of this
     * parameter is ignored. On return, if a reply was retrieved,
     * this parameter is set to the call descriptor of the outstanding
     * service call whose reply was retrieved. Otherwise, it is set to 0.
     * <p>
     * If the {@link #TPGETANY} flag is not set then the input value of this 
     * parameter must be the call descriptor of an outstanding service
     * call returned by {@link #tpacall}. On return, if a reply was
     * retrieved, the call descriptor is invalidated (i.e., set to -1).
     * Otherwise, the call descriptor remains valid.
     *
     * @param replyRef
     * A typed buffer allocated using {@link #tpalloc}.
     * Gets updated with a typed buffer containing the reply of the
     * completed outstanding service call, if any.
     * The position of this buffer is 0 and its limit is the length of the
     * message contained in it.
     *
     * @param lenOut
     * Gets updated with the length of the data in {@paramref replyRef}.
     * If the length is 0 then the called service returned a null reply.
     *
     * @param flags
     * Set {@link #TPGETANY} to wait for the reply of any outstanding
     * service call.
     * See the Tuxedo tpgetrply(3c) manual page.
     *
     * @throws TPESVCFAIL
     * If the invoked service routine returned an application level service
     * failure.
     * In this case, {@paramref replyRef} and {@paramref lenOut} are updated
     * as described above.
     *
     * @throws TPException
     * See the Tuxedo tpgetrply(3c) manual page.
     *
     * @example 
     * The following Java code fragment shows how to send a request to a
     * Tuxedo service and wait for its reply using {@link #tpacall}
     * and {@link #tpgetrply}.
     * <pre>
     * ByteBufferHolder buffer = new ByteBufferHolder();
     * buffer.value = ATMI.tpalloc("STRING", null, 1024);
     * try {
     *     ... // Add request data to buffer
     *     IntHolder cd = new IntHolder();
     *     cd.value = ATMI.tpacall("SVC", buffer.value, 0, ATMI.TPNOTRAN);
     *     try {
     *         ... // Do some other processing
     *         IntHolder len = new IntHolder();
     *         try {
     *             ATMI.tpgetrply(cd, buffer, len, 0);
     *             ... // Process normal reply data in buffer
     *         } catch (TPESVCFAIL e) {
     *             ... // Process error reply data in buffer
     *         }
     *     } finally {
     *         if (cd.value != -1) {
     *             ATMI.tpcancel(cd.value);
     *         }
     *     }
     * } finally {
     *     ATMI.tpfree(buffer.value);
     * }
     * </pre>
     */
    public static void tpgetrply(IntHolder cdRef, ByteBufferHolder replyRef,
	IntHolder lenOut, int flags)
    {
	// The Tuxedo ATMI reference states that tpgetrply() invalidates
	// cdRef on normal return and the error cases described under
	// callDescriptorInvalidated() below. Unfortunately, it does not say 
	// which value cdRef gets in this case, so we explicitly set it 
	// to -1 ourselves.

	if (JTuxInterop.tpgetrply(cdRef, replyRef, lenOut, flags) == -1) {

	    int err = JTuxInterop.tperrno();

	    if (callDescriptorInvalidated(err)) {
		cdRef.value = -1;
	    }
                
            throw newTPException(err);
        }

        cdRef.value = -1;
    }

    private static boolean callDescriptorInvalidated(int err)
    {
	// According to the Tuxedo ATMI reference, tpgetrply() invalidates
	// the call descriptor on error, except for the following errors:
	// TPEINVAL, TPETIME (unless the caller is in transaction mode) 
	// and TPEBLOCK. 

	switch (err) {
	    case TPEINVAL:
		return false;
	    case TPETIME:
		return (JTuxInterop.tpgetlev() == 1);
	    case TPEBLOCK:
		return false;
	    default:
		return true;
        }
    }

    /**
     * Cancels an outstanding service call.
     *
     * @param cd
     * The call descriptor of the outstanding service call to cancel.
     *
     * @throws TPException
     * See the Tuxedo tpcancel(3c) manual page.
     */
    public static void tpcancel(int cd)
    {
	if (JTuxInterop.tpcancel(cd) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Sends a request to a Tuxedo service and waits for its reply.
     *
     * @param svc
     * The name of the Tuxedo service.
     *
     * @param request
     * A typed buffer containing the request; may be null.
     * 
     * @param len
     * The length of the data in {@paramref request}.
     *
     * @param replyRef
     * A typed buffer allocated using {@link #tpalloc}.
     * Gets updated with a typed buffer containing the reply 
     * returned by the called service, if any.
     * The position of this buffer is 0 and its limit is the length 
     * of the message contained in it.
     *
     * @param lenOut
     * Gets updated with the length of the data in {@paramref replyRef}.
     * If the length is 0 then the called service returned a null reply.
     *
     * @param flags
     * See the Tuxedo tpcall(3c) manual page.
     *
     * @throws TPESVCFAIL
     * The called service routine returned an application level service
     * failure.
     * In this case, {@paramref replyRef} and {@paramref lenOut} are updated
     * as described above.
     *
     * @throws TPException
     * See the Tuxedo tpcall(3c) manual page.
     * 
     * @example
     * The following Java code fragment shows how to send a request to
     * a Tuxedo service and wait for its reply using {@link #tpcall}.
     * <pre>
     * ByteBufferHolder buffer = new ByteBufferHolder();
     * buffer.value = ATMI.tpalloc("STRING", null, 1024);
     * try {
     *     ... // Add request data to buffer
     *     IntHolder len = new IntHolder();
     *     try {
     *         ATMI.tpcall("SVC", buffer.value, 0, buffer, len, 0);
     *         ... // Process normal reply data in buffer
     *     } catch (TPESVCFAIL e) {
     *         ... // Process error reply data in buffer
     *     }
     * } finally {
     *     ATMI.tpfree(buffer.value);
     * }
     * </pre>
     */
    public static void tpcall(String svc, ByteBuffer request, int len,
	ByteBufferHolder replyRef, IntHolder lenOut, int flags)
    {
	if (JTuxInterop.tpcall(svc, request, len, replyRef, lenOut,
		flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Establishes a connection with a conversational Tuxedo service.
     *
     * @param svc
     * The name of the Tuxedo service.
     *
     * @param message
     * A typed buffer containing the message to send along with the 
     * connect request; may be null.
     *
     * @param len
     * The length of the data in {@paramref message}.
     *
     * @param flags
     * Must contain either {@link #TPSENDONLY} or {@link #TPRECVONLY}.
     * See the Tuxedo tpconnect(3c) manual page.
     *
     * @return
     * A connection descriptor for use by {@link #tpsend}, {@link #tprecv}
     * and {@link #tpdiscon}.
     *
     * @remarks
     * The caller must make sure to either keep sending and receiving data
     * until the conversation terminates, or abort the conversation using
     * {@link #tpdiscon}.
     * This requires a small amount of non-trivial boilerplate code which
     * can be avoided by using the {@link Conversation} class.
     * 
     * @throws TPException
     * See the Tuxedo tpconnect(3c) manual page.
     */
    public static int tpconnect(String svc, ByteBuffer message, int len,
	int flags)
    {
	int result = JTuxInterop.tpconnect(svc, message, len, flags);
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Sends a message as part of a conversation.
     *
     * @param cd
     * The connection descriptor of the conversation.
     *
     * @param message
     * A typed buffer containing the message to send; may be null.
     *
     * @param len
     * The length of the data in {@paramref message}.
     *
     * @param flags
     * Set {@link #TPRECVONLY} to give up control of the conversation.
     * See the Tuxedo tpsend(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpsend(3c) manual page.
     */
    public static void tpsend(int cd, ByteBuffer message, int len,
	int flags)
    {
	IntHolder eventRef = new IntHolder();
	if (JTuxInterop.tpsend(cd, message, len, flags, eventRef) == -1) {
	    if (JTuxInterop.tperrno() == TPEEVENT) {
		throw newTPEEVENT(eventRef.value);
	    }
	    throw newTPException();
	}
    }

    /**
     * Receives a message as part of a conversation.
     *
     * @param cd
     * The connection descriptor of the conversation.
     *
     * @param messageRef
     * A typed buffer allocated using {@link #tpalloc}.
     * Gets updated with a typed buffer containing the message received
     * as part of the conversation.
     * The position of this buffer is 0 and its limit is the length of the
     * message contained in it.
     *
     * @param lenOut
     * Gets updated with the length of the data in {@paramref messageRef}.
     * If the length is 0 then a null message was received.
     *
     * @param flags
     * See the Tuxedo tprecv(3c) manual page.
     *
     * @throws TPEV_SENDONLY
     * The other end of the connection has given up control of the
     * conversation.
     *
     * @throws TPEV_SVCSUCC
     * The service routine returned successfully.
     * In this case, {@paramref messageRef} and {@paramref lenOut} are
     * updated as described above.
     *
     * @throws TPEV_SVCFAIL
     * The service routine returned with an application level
     * service failure.
     * In this case, {@paramref messageRef} and {@paramref lenOut} are
     * updated as described above.
     *
     * @throws TPException
     * See the Tuxedo tprecv(3c) manual page.
     */
    public static void tprecv(int cd, ByteBufferHolder messageRef,
	IntHolder lenOut, int flags)
    {
	IntHolder eventRef = new IntHolder();
	if (JTuxInterop.tprecv(cd, messageRef, lenOut, flags, eventRef) == -1) {
	    if (JTuxInterop.tperrno() == TPEEVENT) {
		throw newTPEEVENT(eventRef.value);
	    }
	    throw newTPException();
	}
    }

    /**
     * Terminates a conversation abortively.
     *
     * @param cd
     * The connection descriptor of the conversation to abort.
     *
     * @throws TPException
     * See the Tuxedo tpdiscon(3c) manual page.
     */
    public static void tpdiscon(int cd)
    {
	if (JTuxInterop.tpdiscon(cd) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Enqueues a message in a Tuxedo/Q queue.
     *
     * @param qspace
     * The name of the queue space containing the queue.
     *
     * @param qname
     * The name of the queue.
     *
     * @param qctl
     * Control parameters for the enqueue operation.
     * See the {@link TPQCTL} class and the Tuxedo tpenqueue(3c) manual page.
     *
     * @param message
     * A typed buffer containing the message to enqueue; may be null.
     *
     * @param len
     * The length of the data in {@paramref message}.
     *
     * @param flags
     * See the Tuxedo tpenqueue(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpenqueue(3c) manual page.
     *
     * @example
     * The following Java code fragment shows how to enqueue a message in
     * a Tuxedo/Q queue using {@link #tpenqueue}.
     * <pre>
     * ByteBuffer buffer = ATMI.tpalloc("STRING", null, 1024);
     * try {
     *     ... // Add message data to buffer
     *     TPQCTL qctl = new TPQCTL();
     *     qctl.flags = ATMI.TPREPLYQ;
     *     qctl.replyqueue = "REPLYQ";
     *     ATMI.tpenqueue("QSPACE", "SVC", qctl, buffer, 0, 0);
     * } finally {
     *     ATMI.tpfree(buffer);
     * }
     * </pre>
     */
    public static void tpenqueue(String qspace, String qname,
	TPQCTL qctl, ByteBuffer message, int len, int flags)
    {
	if (qctl == null) {
	    // We need a TPQCTL to be able to throw diagnostic exceptions
	    qctl = new TPQCTL();
	    qctl.flags = TPNOFLAGS;
	}

	if (JTuxInterop.tpenqueue(qspace, qname, qctl, message, len, 
		flags) == -1) {
	    if (JTuxInterop.tperrno() == TPEDIAGNOSTIC) {
		throw newTPEDIAGNOSTIC(qctl.diagnostic);
	    }
	    throw newTPException();
	}
    }

    /**
     * Dequeues a message from a Tuxedo/Q queue.
     *
     * @param qspace
     * The name of the qspace containing the queue.
     *
     * @param qname
     * The name of the queue.
     *
     * @param qctl
     * Control parameters for the dequeue operation.
     * See the {@link TPQCTL} class and the Tuxedo tpdequeue(3c) manual page.
     *
     * @param messageRef
     * A typed buffer allocated using {@link #tpalloc}.
     * Gets updated with a typed buffer containing the message dequeued
     * from the queue.
     * The position of this buffer is 0 and its limit is the length of the
     * message contained in it.
     *
     * @param lenOut
     * Gets updated with the length of the data in {@paramref messageRef}.
     * If the length is 0 then a null message was dequeued.
     *
     * @param flags
     * See the Tuxedo tpdequeue(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpdequeue(3c) manual page.
     *
     * @example
     * The following Java code fragment shows how to dequeue a message from
     * a Tuxedo/Q queue using {@link #tpdequeue}.
     * <pre>
     * ByteByfferHolder buffer = new ByteBufferHolder();
     * buffer.value = ATMI.tpalloc("STRING", null, 1024);
     * try {
     *     TPQCTL qctl = new TPQCTL();
     *     qctl.flags = ATMI.TPQWAIT;
     *     IntHolder len = new IntHolder();
     *     ATMI.tpdequeue("QSPACE", "REPLYQ", qctl, buffer, len, 0);
     *     ... // Process message data in buffer
     * } finally {
     *     ATMI.tpfree(buffer.value);
     * }
     * </pre>
     */
    public static void tpdequeue(String qspace, String qname, TPQCTL qctl, 
	ByteBufferHolder messageRef, IntHolder lenOut, int flags)
    {
	if (qctl == null) {
	    // We need a TPQCTL to be able to throw diagnostic exceptions
	    qctl = new TPQCTL();
	    qctl.flags = TPNOFLAGS;
	}

	if (JTuxInterop.tpdequeue(qspace, qname, qctl, messageRef, lenOut,
		flags) == -1) {
	    if (JTuxInterop.tperrno() == TPEDIAGNOSTIC) {
		throw newTPEDIAGNOSTIC(qctl.diagnostic);
	    }
	    throw newTPException();
	}
    }

    /**
     * Starts a global transaction.
     *
     * @param timeout
     * The transaction timeout in seconds; must not be negative.
     *
     * @param flags
     * See the Tuxedo tpbegin(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpbegin(3c) manual page.
     */
    public static void tpbegin(int timeout, int flags)
    {
	// timeout is an unsigned long in C => make sure 
	// timeout is >= 0
	
	if (timeout < 0) {
	    throw new IllegalArgumentException("timeout must not be negative");
	}
	
	if (JTuxInterop.tpbegin(timeout, flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Aborts the current global transaction.
     *
     * @param flags
     * See the Tuxedo tpabort(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpabort(3c) manual page.
     */
    public static void tpabort(int flags)
    {
	if (JTuxInterop.tpabort(flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Commits the current global transaction.
     *
     * @param flags
     * See the Tuxedo tpcommit(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpcommit(3c) manual page.
     */
    public static void tpcommit(int flags)
    {
	if (JTuxInterop.tpcommit(flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Returns the current transaction level.
     *
     * @return
     * The current transaction level (1 if the caller is in
     * transaction mode, 0 if the caller is not in transaction mode).
     * 
     * @throws TPException
     * See the Tuxedo tpgetlev(3c) manual page.
     */
    public static int tpgetlev()
    {
	int result = JTuxInterop.tpgetlev();
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Sets the TP_COMMIT_CONTROL characteristic.
     *
     * @param flags
     * The value can be either {@link #TP_CMT_LOGGED} or
     * {@link #TP_CMT_COMPLETE}.
     * See the Tuxedo tpscmt(3c) manual page.
     *
     * @return
     * The previous value of the TP_COMMIT_CONTROL characteristic.
     *
     * @throws TPException
     * See the Tuxedo tpscmt(3c) manual page.
     */
    public static int tpscmt(int flags)
    {
	int result = JTuxInterop.tpscmt(flags);
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Suspends the current global transaction.
     *
     * @param flags
     * See the Tuxedo tpsuspend(3c) manual page.
     *
     * @return
     * The transaction ID of the current global transaction for use by
     * {@link #tpresume}.
     *
     * @throws TPException
     * See the Tuxedo tpsuspend(3c) manual page.
     */
    public static TPTRANID tpsuspend(int flags)
    {
	TPTRANID tranid = new TPTRANID();
	if (JTuxInterop.tpsuspend(tranid, flags) == -1) {
	    throw newTPException();
	}
	return tranid;
    }

    /**
     * Resumes a suspended global transaction.
     *
     * @param tranid
     * The transaction ID of a suspended global transaction as
     * returned by {@link #tpsuspend}.
     *
     * @param flags
     * See the Tuxedo tpresume(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpresume(3c) manual page.
     */
    public static void tpresume(TPTRANID tranid, int flags)
    {
	if (JTuxInterop.tpresume(tranid, flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Opens the resource manager to which the caller is linked.
     *
     * @ifndef WSCLIENT
     *
     * @throws TPException
     * See the Tuxedo tpopen(3c) manual page.
     */
    public static void tpopen()
    {
	if (JTuxInterop.tpopen() == -1) {
	    throw newTPException();
	}
    }

    /**
     * Closes the resource manager to which the caller is linked.
     *
     * @ifndef WSCLIENT
     *
     * @throws TPException
     * See the Tuxedo tpclose(3c) manual page.
     */
    public static void tpclose()
    {
	if (JTuxInterop.tpclose() == -1) {
	    throw newTPException();
	}
    }

    /**
     * Returns the priority of the last request sent of received by
     * the current thread.
     *
     * @return
     * The priority level of the last request sent of received by the
     * current thread.
     *
     * @throws TPException
     * See the Tuxedo tpgprio(3c) manual page.
     */
    public static int tpgprio()
    {
	int result = JTuxInterop.tpgprio();
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Sets the priority of the next request sent of forwarded by
     * the current thread.
     *
     * @param prio
     * The priority level to set.
     *
     * @param flags
     * See the Tuxedo tpsprio(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpsprio(3c) manual page.
     */
    public static void tpsprio(int prio, int flags)
    {
	if (JTuxInterop.tpsprio(prio, flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Triggers the handling of unsolicited messages.
     *
     * @return
     * The number of unsolicited messages handled.
     *
     * @throws TPException
     * See the Tuxedo tpchkunsol(3c) manual page.
     */
    public static int tpchkunsol()
    {
	int result = JTuxInterop.tpchkunsol();
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Broadcasts an unsolicited message to a set of clients.
     *
     * @param lmid
     * The logical machine ID of the machine to which the broadcast is
     * limited.
     * If null then the broadcast is not limited to a particular machine.
     *
     * @param usrname
     * The name of the user to which the broadcast is limited. 
     * If null then the broadcast is not limited to a particular user.
     *
     * @param cltname
     * The type of client to which the broadcast is limited.
     * If null then the broadcast is not limited to a particular client type.
     *
     * @param message
     * A typed buffer containing the message to send; may be null.
     *
     * @param len
     * The length of the data in {@paramref message}.
     *
     * @param flags
     * See the Tuxedo tpbroadcast(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpbroadcast(3c) manual page.
     */
    public static void tpbroadcast(String lmid, String usrname,
	String cltname, ByteBuffer message, int len, int flags)
    {
	if (JTuxInterop.tpbroadcast(lmid, usrname, cltname, message, len,
		flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Sends an unsolicited message to an individual client.
     *
     * @param cltid
     * The client id of the client to which to send the message.
     *
     * @param message
     * A typed buffer containing the message to send; may be null.
     *
     * @param len
     * The length of the data in {@paramref message}.
     *
     * @param flags
     * See the Tuxedo tpnotify(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpnotify(3c) manual page.
     */
    public static void tpnotify(CLIENTID cltid, ByteBuffer message,
	int len, int flags)
    {
	if (JTuxInterop.tpnotify(cltid, message, len, flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Posts a message using the Tuxedo event broker.
     *
     * @param eventname
     * The name of the event causing the message to be posted.
     *
     * @param message
     * A typed buffer containing the message to post; may be null.
     *
     * @param len
     * The length of the data in {@paramref message}.
     *
     * @param flags
     * See the Tuxedo tppost(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tppost(3c) manual page.
     */
    public static void tppost(String eventname, ByteBuffer message,
	int len, int flags)
    {
	if (JTuxInterop.tppost(eventname, message, len, flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Sets up a subscription to one or more events.
     *
     * @param eventexpr
     * A regular expression specifying the events to which to subscribe.
     *
     * @param filter
     * An additional filter rule evaluated by the event broker before
     * sending an event message; may be null.
     *
     * @param evctl
     * Control parameters for the subscription operation.
     * See the {@link TPEVCTL} class and the Tuxedo tpsubscribe(3c)
     * manual page.
     *
     * @param flags
     * See the Tuxedo tpsubscribe(3c) manual page.
     *
     * @return
     * A subscription handle for use by {@link #tpunsubscribe}.
     *
     * @throws TPException
     * See the Tuxedo tpsubscribe(3c) manual page.
     */
    public static int tpsubscribe(String eventexpr, String filter,
	TPEVCTL evctl, int flags)
    {
	int result = JTuxInterop.tpsubscribe(eventexpr, filter, 
	    evctl, flags);
	if (result == -1) {
	    throw newTPException();
	}
	return result;
    }

    /**
     * Cancels an event subscription.
     *
     * @param subscription
     * The handle of the subscription to cancel as returned by
     * {@link #tpsubscribe}.
     *
     * @param flags
     * See the Tuxedo tpunsubscribe(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpunsubscribe(3c) manual page.
     */
    public static void tpunsubscribe(int subscription, int flags)
    {
	if (JTuxInterop.tpunsubscribe(subscription, flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Returns the multi-byte encoding name of a typed buffer.
     *
     * @param buffer
     * The typed buffer.
     *
     * @param flags
     * See the Tuxedo tpgetmbenc(3c) manual page.
     *
     * @return
     * The multi-byte encoding name of {@paramref buffer}.
     *
     * @throws TPException
     * See the Tuxedo tpgetmbenc(3c) manual page.
     */
    public static String tpgetmbenc(ByteBuffer buffer, int flags)
    {
	StringHolder encOut = new StringHolder();
	if (JTuxInterop.tpgetmbenc(buffer, encOut, flags) == -1) {
	    throw newTPException();
	}
	return encOut.value;
    }

    /**
     * Sets the multi-byte encoding name of a typed buffer.
     *
     * @param buffer
     * The typed buffer.
     *
     * @param enc
     * The multi-byte encoding name.
     *
     * @param flags
     * Use {@link #RM_ENC} to clear the multi-byte encoding name.
     * See the Tuxedo tpsetmbenc(3c) manual page.
     *
     * @throws TPException
     * See the Tuxedo tpsetmbenc(3c) manual page.
     */
    public static void tpsetmbenc(ByteBuffer buffer, String enc,
	int flags)
    {
	if (JTuxInterop.tpsetmbenc(buffer, enc, flags) != 0) {
	    // Not == -1 (!), see tpsetmbenc(3c)
	    throw newTPException();
	}
    }

    /**
     * Dynamically advertises a Tuxedo service.
     *
     * @ifndef WSCLIENT
     * 
     * @param svc 
     * The name of the service.
     *
     * @param func
     * The name of the service routine.
     * This must be the name of a Tuxedo server class method that has
     * been identified as a service routine using the {@code -s}
     * command line option of the JServer executable.
     *
     * @throws TPException
     * See the Tuxedo tpadvertise(3c) manual page.
     */
    public static void tpadvertise(String svc, String func)
    {
	if (JTuxInterop.tpadvertise(svc, func) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Dynamically unadvertises a service.
     *
     * @ifndef WSCLIENT
     * 
     * @param svc
     * The name of the service.
     *
     * @throws TPException
     * See the Tuxedo tpunadvertise(3c) manual page.
     */
    public static void tpunadvertise(String svc)
    {
	if (JTuxInterop.tpunadvertise(svc) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Returns a reply from a service routine.
     *
     * @ifndef WSCLIENT
     * 
     * @param rval
     * The termination status of the service routine
     * ({@link #TPSUCCESS}, {@link #TPFAIL} or {@link #TPEXIT}.
     *
     * @param rcode
     * The application return code.
     *
     * @param reply
     * A typed buffer containing the reply to return; may be null.
     *
     * @param len
     * The length of the data in {@paramref reply}.
     *
     * @param flags
     * See the Tuxedo tpreturn(3c) manual page.
     *
     * @remarks
     * This method does not actually terminate the service routine but
     * only marks the service routine as being terminated.
     * It is the responsibility of the programmer to make sure that the
     * service routine returns promplty after calling this method.
     *
     * @throws TPEPROTO
     * If the service routine was already terminated by a previous
     * call to {@link #tpreturn} or {@link #tpforward}.
     */
    public static void tpreturn(int rval, int rcode, ByteBuffer reply,
	int len, int flags)
    {
	if (JTuxInterop.tpreturn(rval, rcode, reply, len, flags) == -1) {
	    throw newTPException();
	}
    }

    /**
     * Forwards a request to another Tuxedo service.
     *
     * @ifndef WSCLIENT
     * 
     * @param svc
     * The name of the service.
     *
     * @param request
     * A typed buffer containing the request to forward; may be null.
     *
     * @param len
     * The length of the data in {@paramref request}.
     *
     * @param flags
     * See the Tuxedo tpforward(3c) manual page.
     *
     * @remarks
     * This method does not actually terminate the service routine but
     * only marks the service routine as being terminated.
     * It is the responsibility of the programmer to make sure that the
     * service routine returns promplty after calling this method.
     *
     * @throws TPEPROTO
     * If the service routine was already terminated by a previous
     * call to {@link #tpreturn} or {@link #tpforward}.
     */
    public static void tpforward(String svc, ByteBuffer request,
	int len, int flags)
    {
	if (JTuxInterop.tpforward(svc, request, len, flags) == -1) {
	    throw newTPException();
	}
    }
}
