/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "JTuxJTA.c"

#include "JTuxJTA.h"

#include "XAResourceAdapter.h"

#include "javax_transaction_xa_Xid.h"
#include "javax_transaction_xa_XAResource.h"
#include "javax_transaction_xa_XAException.h"
#include "jtux_xa_XID.h"

#include "JTuxJNU.h"
#include "JTuxJVM.h"

#include "OTPJavaVM.h"

#include "OTPUserlog.h"

#include "OTPError.h"
#include "OTPString.h"
#include "OTPThread.h"

#include <jni.h>

#include <xa.h>
#include <userlog.h>

#include <string.h>
#include <assert.h>

/*
 * If the JTuxJTA library is not explicitly initialized then it will be
 * initialized the first time JTuxJTAOpen() is called. In this case, the
 * JTuxJTA library will run in single-threaded mode. If the JTuxJTA library
 * must run in multi-threaded mode, or other JTuxJTA operations must be
 * carried out before JTuxJTAOpen() is called then JTuxJTAInitialize()
 * should be called explicitly.
 */

static JavaVM *theJvm = NULL;

static JNIEnv *globalEnv = NULL;

static int initialized = 0;

static int isMultiThreaded = 0;

static int trace = 0;

static char *serverAddress = NULL;

static int connectionExpiryTime = 0;

static jobject globalXAResource = NULL;

static OTPThreadVar threadLocalXAResource;

static jclass xaExceptionClass = NULL;

int JTuxJTAInitialize(JavaVM *jvm, int multiThreaded, char **errorInfo)
{
    JNIEnv *env;

    if (jvm == NULL) {
        if (JTuxJVMInit(NULL, &theJvm, &globalEnv, errorInfo) == -1) {
	    return -1;
	}
    } else {
        theJvm = jvm;
    }

    if (multiThreaded) {
        if (OTPThreadVarCreate(&threadLocalXAResource, errorInfo) == -1) {
	    OTPErrorAddInfo(errorInfo, "Error creating TLS "
		"for thread-local XA resource");
	    return -1;
	}
        isMultiThreaded = 1;
    }

    if (OTPJavaVMGetEnv(theJvm, JNI_VERSION_1_4, &env, errorInfo) == -1) {
	return -1;
    }

    xaExceptionClass = javax_transaction_xa_XAException_class(env);
    if (xaExceptionClass == NULL) {
	SET_ERROR("Exception loading class javax.transaction.xa.XAException");
	JTuxLogAndClearException(env);
        return -1;
    }

    initialized = 1;

    return 0;
}

static void setXAResource(jobject xaResource)
{
    if (isMultiThreaded) {
        OTPThreadVarSet(threadLocalXAResource, xaResource, NULL);
    } else {
	globalXAResource = xaResource;
    }
}

static jobject getXAResource()
{
    if (isMultiThreaded) {
        jobject xaResource;
        OTPThreadVarGet(threadLocalXAResource, (void **) &xaResource, NULL);
        return xaResource;
    } else {
        return globalXAResource;
    }
}

static int getJNIEnv(JNIEnv **env, char **errorInfo)
{
    if (isMultiThreaded) {
        return OTPJavaVMGetEnv(theJvm, JNI_VERSION_1_4, env, errorInfo);
    } else {
       	*env = globalEnv;
	return 0;
    }
}

/*---------*/
/* XID/Xid */
/*---------*/

static jobject newXID(JNIEnv *env, XID *xid)
{
    jobject jxid = jtux_xa_XID_new(env);
    if (jxid == NULL) {
	return NULL;
    }

    jtux_xa_XID_set_formatID(env, jxid, (jint) xid->formatID);

    if (xid->formatID == -1) {
	jtux_xa_XID_set_gtrid(env, jxid, NULL);
    } else {
        jbyteArray gtrid = JTuxNewByteArray(env, (jbyte *) xid->data, 
	    (jsize) xid->gtrid_length);
	if (gtrid == NULL) {
	    (*env)->DeleteLocalRef(env, jxid);
	    return NULL;
        }
        jtux_xa_XID_set_gtrid(env, jxid, gtrid);
        (*env)->DeleteLocalRef(env, gtrid);
    }

    if (xid->formatID == -1) {
	jtux_xa_XID_set_bqual(env, jxid, NULL);
    } else {
	jbyte *bqualBytes = (jbyte *) xid->data + xid->gtrid_length;
        jbyteArray bqual = JTuxNewByteArray(env, bqualBytes,
	    (jsize) xid->bqual_length);
	if (bqual == NULL) {
	    (*env)->DeleteLocalRef(env, jxid);
	    return NULL;
        }
        jtux_xa_XID_set_bqual(env, jxid, bqual);
        (*env)->DeleteLocalRef(env, bqual);
    }

    return jxid;
}

static int getXid(JNIEnv *env, jobject jxid, XID *xid)
{
    jbyteArray gtrid;

    jbyteArray bqual;

    xid->formatID = javax_transaction_xa_Xid_call_getFormatId(env, jxid);
    if ((*env)->ExceptionCheck(env)) {
	return -1;
    }

    gtrid = javax_transaction_xa_Xid_call_getGlobalTransactionId(env, jxid);
    if ((*env)->ExceptionCheck(env)) {
	return -1;
    }

    if (gtrid != NULL) {
	xid->gtrid_length = (long) (*env)->GetArrayLength(env, gtrid);
	if (xid->gtrid_length > MAXGTRIDSIZE) {
	    JTuxThrowRuntimeException(env, "gtrid longer than MAXGTRIDSIZE");
	    (*env)->DeleteLocalRef(env, gtrid);
	    return -1;
	}
	(*env)->GetByteArrayRegion(env, gtrid, 0, (jsize) xid->gtrid_length,
	    (jbyte *) xid->data);
	(*env)->DeleteLocalRef(env, gtrid);
    } else {
	xid->gtrid_length = 0;
    }

    bqual = javax_transaction_xa_Xid_call_getBranchQualifier(env, jxid);
    if ((*env)->ExceptionCheck(env)) {
	return -1;
    }

    if (bqual != NULL) {
	xid->bqual_length = (long) (*env)->GetArrayLength(env, bqual);
	if (xid->bqual_length > MAXBQUALSIZE) {
	    JTuxThrowRuntimeException(env, "bqual longer than MAXBQUALSIZE");
	    (*env)->DeleteLocalRef(env, bqual);
	    return -1;
	}
	(*env)->GetByteArrayRegion(env, bqual, 0, (jsize) xid->bqual_length,
	    (jbyte*) (xid->data + xid->gtrid_length));
	(*env)->DeleteLocalRef(env, bqual);
    } else {
	xid->bqual_length = 0;
    }

    return 0;
}

/*-------------------*/
/* XA error handling */
/*-------------------*/

static const char *xastrerror(int errorCode)
{
    switch (errorCode) {
	case XAER_ASYNC:
	    return "XAER_ASYNC";
	case XAER_RMERR:
	    return "XAER_RMERR";
	case XAER_NOTA:
	    return "XAER_NOTA";
	case XAER_INVAL:
	    return "XAER_INVAL";
	case XAER_PROTO:
	    return "XAER_PROTO";
	case XAER_RMFAIL:
	    return "XAER_RMFAIL";
	case XAER_DUPID:
	    return "XAER_DUPID";
	case XAER_OUTSIDE:
	    return "XAER_OUTSIDE";
	case XA_RDONLY:
	    return "XA_RDONLY";
	case XA_RETRY:
	    return "XA_RETRY";
	case XA_HEURMIX:
	    return "XA_HEURMIX";
	case XA_HEURRB:
	    return "XA_HEURRB";
	case XA_HEURCOM:
	    return "XA_HEURCOM";
	case XA_HEURHAZ:
	    return "XA_HEURHAZ";
	case XA_NOMIGRATE:
	    return "XA_NOMIGRATE";
	case XA_RBCOMMFAIL:
	    return "XA_RBCOMMFAIL";
	case XA_RBDEADLOCK:
	    return "XA_RBDEADLOCK";
	case XA_RBINTEGRITY:
	    return "XA_RBINTEGRITY";
	case XA_RBOTHER:
	    return "XA_RBOTHER";
	case XA_RBPROTO:
	    return "XA_RBPROTO";
	case XA_RBROLLBACK:
	    return "XA_RBROLLBACK";
	case XA_RBTIMEOUT:
	    return "XA_RBTIMEOUT";
	case XA_RBTRANSIENT:
	    return "XA_RBTRANSIENT";
	default:
	    return "???";
    }
}

static int handleException(JNIEnv *env, int *xaResult, char **errorInfo)
{
    jthrowable exception = (*env)->ExceptionOccurred(env); 

    assert(exception != NULL);

    /*
     * All exceptions are written to the ULOG. The reason for including
     * XAExceptions is that they may contain resource manager specific
     * diagnostic information besides the XA errorCode.
     */

    JTuxLogAndClearException(env);

    if ((*env)->IsInstanceOf(env, exception, xaExceptionClass)) {
	int errorCode = javax_transaction_xa_XAException_get_errorCode(env,
	    exception);
	/* XAException.toString() does not print errorCode so do that also */
	OTPUserlog("INFO: XAException.errorCode = %d (%s)", errorCode,
	    xastrerror(errorCode));
	(*env)->DeleteLocalRef(env, exception);
	*xaResult = errorCode;
	return 0;
    }

    SET_ERROR("Unexpected exception executing XA operation");

    (*env)->DeleteLocalRef(env, exception);

    return -1;
}

int JTuxJTAOpen(const char *xaInfo, int *xaResult, char **errorInfo)
{
    int xaInfoLen = strlen(xaInfo);

    const char *xaResourceAdapterName;

    const char *connectString;

    JNIEnv *env;

    jobject xaResource;

    jobject xaResourceGlobalRef;

    if ((!initialized) && (JTuxJTAInitialize(NULL, 0, errorInfo) == -1)) {
	return -1;
    }

    if (OTPStringStartsWith(xaInfo, xaInfoLen, "JDBC:")) {
	xaResourceAdapterName = "jtux.jdbc.JDBCXAResourceAdapter";
	connectString = xaInfo + 5;
    } else if (OTPStringStartsWith(xaInfo, xaInfoLen, "JMS:")) {
	xaResourceAdapterName = "jtux.jms.JMSXAResourceAdapter";
	connectString = xaInfo + 4;
    } else {
	OTPErrorSet(errorInfo, "Invalid XA info string: %s", xaInfo);
	return -1;
    }

    if (getJNIEnv(&env, errorInfo) == -1) {
	return -1;
    }

    if (XAResourceAdapter_init(env, xaResourceAdapterName) == -1) {
	goto exception;
    }

    xaResource = XAResourceAdapter_call_open(env, connectString);

    if ((*env)->ExceptionCheck(env)) {
	goto exception;
    }

    if (xaResource == NULL) {
	SET_ERROR("XAResourceAdapter returned NULL XAResource");
	return -1;
    }

    /* 
     * xaResource must be turned into a global ref because xa_open()
     * may have been called from a native method invocation (most notably
     * ATMI.tpopen() in JServer) which invalidates all local refs
     * on completion.
     */

    xaResourceGlobalRef = (*env)->NewGlobalRef(env, xaResource);

    (*env)->DeleteLocalRef(env, xaResource);

    if (xaResourceGlobalRef == NULL) {
	goto exception;
    }

    setXAResource(xaResourceGlobalRef);

    *xaResult = XA_OK;

    return 0;

exception:

    return handleException(env, xaResult, errorInfo);
}

int JTuxJTAClose(int *xaResult, char **errorInfo)
{
    JNIEnv *env;

    jobject xaResource;

    if (getJNIEnv(&env, errorInfo) == -1) {
	return -1;
    }

    xaResource = getXAResource();

    (*env)->DeleteGlobalRef(env, xaResource);

    XAResourceAdapter_call_close(env);

    if ((*env)->ExceptionCheck(env)) {
	goto exception;
    }

    *xaResult = XA_OK;

    return 0;

exception:

    return handleException(env, xaResult, errorInfo);
}

typedef int (*XAXidMethodCaller)(JNIEnv *env, jobject xar, jobject xid, 
    long flags);

static int callXAXidMethod(XID *xid, long flags, XAXidMethodCaller caller,
    int *xaResult, char **errorInfo)
{
    JNIEnv *env;

    jobject xaResource;

    jobject jxid;

    if (getJNIEnv(&env, errorInfo) == -1) {
	return -1;
    }

    xaResource = getXAResource();

    jxid = newXID(env, xid);
    if (jxid == NULL) {
	goto exception;
    }

    *xaResult = (*caller)(env, xaResource, jxid, flags);

    (*env)->DeleteLocalRef(env, jxid);

    if ((*env)->ExceptionCheck(env)) {
	goto exception;
    }

    return 0;

exception:

    return handleException(env, xaResult, errorInfo);
}

static int callXAStart(JNIEnv *env, jobject xar, jobject xid, long flags)
{
    javax_transaction_xa_XAResource_call_start(env, xar, xid, flags);
    return XA_OK;
}

static int callXAEnd(JNIEnv *env, jobject xar, jobject xid, long flags)
{
    javax_transaction_xa_XAResource_call_end(env, xar, xid, flags);
    return XA_OK;
}

static int callXAPrepare(JNIEnv *env, jobject xar, jobject xid, long flags)
{
    return javax_transaction_xa_XAResource_call_prepare(env, xar, xid);
}

static int callXACommit(JNIEnv *env, jobject xar, jobject xid, long flags)
{
    jboolean onePhase = (flags & TMONEPHASE) ? JNI_TRUE : JNI_FALSE;
    javax_transaction_xa_XAResource_call_commit(env, xar, xid, onePhase);
    return XA_OK;
}

static int callXARollback(JNIEnv *env, jobject xar, jobject xid, long flags)
{
    javax_transaction_xa_XAResource_call_rollback(env, xar, xid);
    return XA_OK;
}

static int callXAForget(JNIEnv *env, jobject xar, jobject xid, long flags)
{
    javax_transaction_xa_XAResource_call_forget(env, xar, xid);
    return XA_OK;
}

int JTuxJTAStart(XID *xid, long flags, int *xaResult, char **errorInfo)
{
    return callXAXidMethod(xid, flags, callXAStart, xaResult, errorInfo);
}

int JTuxJTAEnd(XID *xid, long flags, int *xaResult, char **errorInfo)
{
    return callXAXidMethod(xid, flags, callXAEnd, xaResult, errorInfo);
}

int JTuxJTAPrepare(XID *xid, long flags, int *xaResult, char **errorInfo)
{
    return callXAXidMethod(xid, flags, callXAPrepare, xaResult, errorInfo);
}

int JTuxJTACommit(XID *xid, long flags, int *xaResult, char **errorInfo)
{
    return callXAXidMethod(xid, flags, callXACommit, xaResult, errorInfo);
}

int JTuxJTARollback(XID *xid, long flags, int *xaResult, char **errorInfo)
{
    return callXAXidMethod(xid, flags, callXARollback, xaResult, errorInfo);
}

int JTuxJTAForget(XID *xid, long flags, int *xaResult, char **errorInfo)
{
    return callXAXidMethod(xid, flags, callXAForget, xaResult, errorInfo);
}

int JTuxJTARecover(XID *xids, long count, long flags, int *xaResult, 
    char **errorInfo)
{
    static jarray jxids = NULL;

    static int jxidCursor;

    static int jxidCount;

    JNIEnv *env;

    jobject xaResource;

    int i;

    if (getJNIEnv(&env, errorInfo) == -1) {
	return -1;
    }

    xaResource = getXAResource();

    if (flags & TMSTARTRSCAN) {
	if (jxids != NULL) {
	    (*env)->DeleteLocalRef(env, jxids);
	}
	jxids = javax_transaction_xa_XAResource_call_recover(env,
	    getXAResource(), TMSTARTRSCAN | TMENDRSCAN);
	if ((*env)->ExceptionCheck(env)) {
	    goto exception;
	}
	if (jxids == NULL) {
	    SET_ERROR("XAResource.recover() returned null");
	    return -1;
	}
	jxidCursor = 0;
	jxidCount = (*env)->GetArrayLength(env, jxids);
    } else {
        if (jxids == NULL) {
	    OTPUserlog("ERROR: xa_recover() called without TMSTARTRSCAN "
		"but no recovery scan open");
	    *xaResult = XAER_INVAL; /* As per XA spec */
	    return 0;
	}
    }

    i = 0;

    while ((jxidCursor + i < jxidCount) && (i < count)) {
	jobject jxid = (*env)->GetObjectArrayElement(env, jxids,
	    jxidCursor + i);
	if (jxid != NULL) {
            int result = getXid(env, jxid, xids);
	    (*env)->DeleteLocalRef(env, jxid);
	    if (result != 0) {
		goto exception;
	    }
	    xids++;
	}
	i++;
    }

    /* Only update jxidCursor after no more errors can occur */

    jxidCursor = jxidCursor + i;

    /*
     * Recovery scans ends if and only if returned number of XIDs < count
     * or TPENDRSCAN flag is set (see XA spec)
     */

    if ((i < count) || (flags & TMENDRSCAN)) {
	(*env)->DeleteLocalRef(env, jxids);
	jxids = NULL;
    }

    return i;

exception:

    return handleException(env, xaResult, errorInfo);
}
