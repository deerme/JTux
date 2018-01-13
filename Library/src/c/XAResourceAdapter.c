/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "XAResourceAdapter.h"

#include "OTPString.h"
#include "OTPUserlog.h"

#include "JTuxJNU.h"

#include <atmi.h>

#include <jni.h>

#include <string.h>

typedef struct XAResourceAdapter {
    int debug;
    jclass cls;
    jmethodID m_open;
    jmethodID m_close;
    char *name;
} XAResourceAdapter;

static XAResourceAdapter theXAResourceAdapter;

OTP_FUNC_DEF
int XAResourceAdapter_init(JNIEnv *env, const char *name)
{
    jclass cls;

    if (theXAResourceAdapter.cls != NULL) {
	return 0;
    }
   
    cls = JTuxFindClassByName(env, name);
    if (cls == NULL) {
	goto exception;
    }

    if ((*env)->MonitorEnter(env, cls) != 0) {
	goto exception_after_cls;
    }

    // Extra check to prevent possible race condition
 
    if (theXAResourceAdapter.cls == NULL) {

	char *debug = tuxgetenv("JTUX_DEBUG");
	if ((debug != NULL) && (strstr(debug, "XA") != NULL)) {
	    theXAResourceAdapter.debug = 1;
	}

        theXAResourceAdapter.m_open = (*env)->GetStaticMethodID(env, 
	    cls, "open",
	    "(Ljava/lang/String;)Ljavax/transaction/xa/XAResource;");
	if (theXAResourceAdapter.m_open == NULL) {
	    goto exception_after_monitor;
	}

        theXAResourceAdapter.m_close = (*env)->GetStaticMethodID(env,
	    cls, "close", "()V");
	if (theXAResourceAdapter.m_close == NULL) {
	    goto exception_after_monitor;
	}

	theXAResourceAdapter.cls = (*env)->NewGlobalRef(env, cls);
	if (theXAResourceAdapter.cls == NULL) {
	    goto exception_after_monitor;
	}

	theXAResourceAdapter.name = OTPStringDuplicate(name, NULL);

	if (theXAResourceAdapter.debug) {
	    OTPUserlog("DEBUG [XA]: Loaded XA resource adapter class '%s'",
	        theXAResourceAdapter.name);
	}
    }

    (*env)->MonitorExit(env, cls);

    (*env)->DeleteLocalRef(env, cls);

    return 0;

exception_after_monitor:

    (*env)->MonitorExit(env, cls);

exception_after_cls:

    (*env)->DeleteLocalRef(env, cls);

exception:

    return -1;
}

OTP_FUNC_DEF
jobject XAResourceAdapter_call_open(JNIEnv *env, const char *xaInfo)
{
    jstring jxaInfo;

    jobject xaResource;

    if (xaInfo == NULL) {
	jxaInfo = NULL;
    } else {
	jxaInfo = (*env)->NewStringUTF(env, xaInfo);
	if (jxaInfo == NULL) {
	    return NULL;
	}
    }

    if (theXAResourceAdapter.debug) {
	OTPUserlog("DEBUG [XA]: Calling %s.open()", theXAResourceAdapter.name);
    }

    xaResource = (*env)->CallStaticObjectMethod(env, theXAResourceAdapter.cls,
	theXAResourceAdapter.m_open, jxaInfo);

    if (jxaInfo != NULL) {
	(*env)->DeleteLocalRef(env, jxaInfo);
    }

    if ((*env)->ExceptionCheck(env)) {
	return NULL;
    }

    if (theXAResourceAdapter.debug) {
	OTPUserlog("DEBUG [XA]: %s.open() OK", theXAResourceAdapter.name);
    }

    return xaResource;
}

OTP_FUNC_DEF
void XAResourceAdapter_call_close(JNIEnv *env)
{
    if (theXAResourceAdapter.debug) {
	OTPUserlog("DEBUG [XA]: Calling %s.close()", 
	    theXAResourceAdapter.name);
    }

    (*env)->CallStaticVoidMethod(env, theXAResourceAdapter.cls,
	theXAResourceAdapter.m_close);

    if ((*env)->ExceptionCheck(env)) {
	return;
    }

    if (theXAResourceAdapter.debug) {
	OTPUserlog("DEBUG [XA]: %s.close() OK", theXAResourceAdapter.name);
    }
}
