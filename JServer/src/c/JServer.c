/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

#include "JTuxJTA.h"
#include "JTuxJVM.h"
#include "JTuxJNU.h"

#include "jtux_atmi_CLIENTID.h"
#include "jtux_atmi_TPSVCINFO.h"
#include "java_lang_NoSuchMethodError.h"
#include "java_lang_Thread.h"
#include "java_lang_ClassLoader.h"
#include "jtux_JTuxInterop.h"

#include "OTPJavaVM.h"

#include "OTPTuxServerImpl.h"
#include "OTPTuxServer.h"
#include "OTPTuxConfig.h"

#include "OTPLibrary.h"
#include "OTPString.h"
#include "OTPThread.h"
#include "OTPUunix.h"
#include "OTPUserlog.h"

#include <atmi.h>
#include <fml32.h>

#include <jni.h>

#include <stdio.h>
#include <stdlib.h>

/*------------*/
/* Java tools */
/*------------*/

static jobject newCLIENTID(JNIEnv *env, CLIENTID *cltid)
{
    jobject obj = jtux_atmi_CLIENTID_new(env);
    if (obj != NULL) {
        jtux_atmi_CLIENTID_set_clientdata0(env, obj, cltid->clientdata[0]);
        jtux_atmi_CLIENTID_set_clientdata1(env, obj, cltid->clientdata[1]);
        jtux_atmi_CLIENTID_set_clientdata2(env, obj, cltid->clientdata[2]);
        jtux_atmi_CLIENTID_set_clientdata3(env, obj, cltid->clientdata[3]);
    }
    return obj;
}

static jobject newTPSVCINFO(JNIEnv *env, TPSVCINFO *svcinfo)
{
    jstring name;
    jobject data;
    jobject cltid;

    jobject obj = jtux_atmi_TPSVCINFO_new(env);
    if (obj == NULL) {
	goto exception;
    }

    name = (*env)->NewStringUTF(env, svcinfo->name);
    if (name == NULL) {
	goto exception_after_obj;
    }
    jtux_atmi_TPSVCINFO_set_name(env, obj, name);
    (*env)->DeleteLocalRef(env, name);

    if (svcinfo->data == NULL) {
	jtux_atmi_TPSVCINFO_set_data(env, obj, NULL);
    } else {
	data = JTuxNewTypedBuffer(env, svcinfo->data, 0, svcinfo->len);
	if (data == NULL) {
	    goto exception_after_obj;
	}
	jtux_atmi_TPSVCINFO_set_data(env, obj, data);
	(*env)->DeleteLocalRef(env, data);
    }

    jtux_atmi_TPSVCINFO_set_len(env, obj, svcinfo->len);

    jtux_atmi_TPSVCINFO_set_flags(env,obj, svcinfo->flags);

    jtux_atmi_TPSVCINFO_set_cd(env, obj, svcinfo->cd);

    jtux_atmi_TPSVCINFO_set_appkey(env, obj, svcinfo->appkey);

    cltid = newCLIENTID(env, &svcinfo->cltid);
    if (cltid == NULL) {
	goto exception_after_obj;
    }
    jtux_atmi_TPSVCINFO_set_cltid(env, obj, cltid);
    (*env)->DeleteLocalRef(env, cltid);

    return obj;

exception_after_obj:

    (*env)->DeleteLocalRef(env, obj);

exception:

    return NULL;
}

/*------------*/
/* JavaServer */
/*------------*/

typedef struct JavaServer {
    jclass cls;
    jmethodID m_tpsvrinit;
    jmethodID m_tpsvrdone;
    jmethodID m_tpsvrthrinit;
    jmethodID m_tpsvrthrdone;
    char *className;
} JavaServer;

static JavaServer javaServer;

static int initJavaServerMethod(JNIEnv *env, jclass cls, const char *name, 
    const char *sig, jmethodID *methodPtr)
{
    jmethodID method = (*env)->GetStaticMethodID(env, cls, name, sig);
    if (method == NULL) {
	jclass noSuchMethodErrorClass;
	jthrowable exception = (*env)->ExceptionOccurred(env);
	(*env)->ExceptionClear(env);
	noSuchMethodErrorClass = java_lang_NoSuchMethodError_class(env);
	if (noSuchMethodErrorClass == NULL) {
	    (*env)->DeleteLocalRef(env, exception);
	    return -1;
	}
	if ((*env)->IsInstanceOf(env, exception, noSuchMethodErrorClass)) {
	    *methodPtr = NULL;
	    (*env)->DeleteLocalRef(env, exception);
	    return 0;
	} else {
	    (*env)->Throw(env, exception);
	    (*env)->DeleteLocalRef(env, exception);
	    return -1;
	}
    } else {
        *methodPtr = method;
        return 0;
    }
}

static int initJavaServer(JNIEnv *env, const char *className)
{
    jclass cls = JTuxFindClassByName(env, className);
    if (cls == NULL) {
	goto exception;
    }

    if (initJavaServerMethod(env, cls, "tpsvrinit", 
	    "([Ljava/lang/String;)I", &javaServer.m_tpsvrinit) == -1) {
	goto exception_after_cls;
    }

    if (initJavaServerMethod(env, cls, "tpsvrdone", 
	    "()V", &javaServer.m_tpsvrdone) == -1) {
	goto exception_after_cls;
    }

    if (initJavaServerMethod(env, cls, "tpsvrthrinit", 
	    "([Ljava/lang/String;)I", &javaServer.m_tpsvrthrinit) == -1) {
	goto exception_after_cls;
    }

    if (initJavaServerMethod(env, cls, "tpsvrthrdone", 
	    "()V", &javaServer.m_tpsvrthrdone) == -1) {
	goto exception_after_cls;
    }

    javaServer.cls = (*env)->NewGlobalRef(env, cls);
    if (javaServer.cls == NULL) {
	goto exception_after_cls;
    }

    javaServer.className = OTPStringDuplicate(className, NULL);

    (*env)->DeleteLocalRef(env, cls);

    return 0;

exception_after_cls:

    (*env)->DeleteLocalRef(env, cls);

exception:

    return -1;
}

static int JavaServer_call_tpsvrinit(JNIEnv *env, int argc, char *argv[])
{
    jarray args = JTuxNewArgs(env, argc, argv);
    if (args == NULL) {
	return 0; /* exception => return anything */
    } else {
        int result = (*env)->CallStaticIntMethod(env, javaServer.cls,
	    javaServer.m_tpsvrinit, args);
        (*env)->DeleteLocalRef(env, args);
        return result;
    }
}

static void JavaServer_call_tpsvrdone(JNIEnv *env)
{
    (*env)->CallStaticVoidMethod(env, javaServer.cls, 
	javaServer.m_tpsvrdone);
}

static int JavaServer_call_tpsvrthrinit(JNIEnv *env, int argc, char *argv[])
{
    jarray args = JTuxNewArgs(env, argc, argv);
    if (args == NULL) {
	return 0; /* exception => return anything */
    } else {
        int result = (*env)->CallStaticIntMethod(env, javaServer.cls,
	    javaServer.m_tpsvrthrinit, args);
        (*env)->DeleteLocalRef(env, args);
        return result;
    }
}

static void JavaServer_call_tpsvrthrdone(JNIEnv *env)
{
    (*env)->CallStaticVoidMethod(env, javaServer.cls, 
	javaServer.m_tpsvrthrdone);
}

/*----------------------------*/
/* OTPTuxServerImpl functions */
/*----------------------------*/

static JavaVM *theJvm;

static JNIEnv *mainThreadEnv;

void tpservice(TPSVCINFO *svcInfo)
{
    OTPTuxServerDispatch(svcInfo);
}

static int getDispatchInfo(const char *serviceRoutineName, 
    void **dispatchInfo)
{
    /* This function is called from the main thread */
    /* so we can use mainThreadEnv */

    JNIEnv *env = mainThreadEnv;

    jmethodID method = (*env)->GetStaticMethodID(env, javaServer.cls, 
	serviceRoutineName, "(Ljtux/atmi/TPSVCINFO;)V");
    if (method == NULL) {
	OTPUserlog("ERROR: Exception getting service routine '%s'",
	    serviceRoutineName);
	JTuxLogAndClearException(env);
	return -1;
    }

    *dispatchInfo = method;

    return 0;
}

static void freeDispatchInfo(void *dispatchInfo)
{
    /* Nothing to do */
}

static void dispatch(TPSVCINFO *svcInfo, void *dispatchInfo)
{
    /* This function is called from a service dipatch thread */
    /* so don't use mainThreadEnv here */

    char *err = NULL;

    JNIEnv *env;

    jobject jsvcInfo;

    if (OTPJavaVMGetEnv(theJvm, JNI_VERSION_1_4, &env, &err) == -1) {
	OTPUserlog("ERROR: Error getting JNIEnv: %s", err);
	free(err);
	return; /* => TPESVCERR */
    }

    jsvcInfo = newTPSVCINFO(env, svcInfo);
    if (jsvcInfo == NULL) {
	OTPUserlog("ERROR: Exception creating TPSVCINFO object");
	JTuxLogAndClearException(env);
	return; /* => TPESVCERR */
    }

    (*env)->CallStaticVoidMethod(env, javaServer.cls,
	(jmethodID) dispatchInfo, jsvcInfo);

    (*env)->DeleteLocalRef(env, jsvcInfo);

    if ((*env)->ExceptionCheck(env)) {
	OTPUserlog("ERROR: Exception handling request for service '%s'",
	    svcInfo->name);
	JTuxLogAndClearException(env);
	return; /* => TPESVCERR */
    }
}

/*-----------------------------*/
/* tpsvrinit() and tpsvrdone() */
/*-----------------------------*/

/* 
 * The Tuxedo tpsvrinit(3c) reference page states the following:
 *
 *    "If a server has been defined as a single-threaded server, the
 *     default tpsvrinit() calls tpsvrthrinit(), and the default version
 *     of tpsvrthrinit() calls tx_open(). If a server has been defined as
 *     a multithreaded server, tpsvrthrinit() is called in each server
 *     dispatch thread, but is not called from tpsvrinit(). Regardless of
 *     whether the server is single-threaded or multithreaded, the default
 *     version of tpsvrinit() calls userlog() to indicate that the server
 *     started successfully."
 *
 * The question is: When is a server defined as a multi-threaded server?
 * It turns out that being built using buildserver -t is not enough. The
 * server also needs to be actually running in multi-threaded mode by
 * having MAXDISPATCHTHREADS > 1. If this is not the case then the main
 * thread is also the service dispatch thread and tpsvrthrinit() or
 * tpopen() must be called in it if tpsvrinit() is missing. This behaviour
 * confirmed by the tpsvrthrinit(3c) reference page, which states:
 *
 *    "tpsvrthrinit() is called even in single-threaded servers. In a
 *     single-threaded server, tpsvrthrinit() is called from the default
 *     version of tpsvrinit(). In a server with the potential for multiple
 *     dispatch threads, tpsvrinit() does not call tpsvrthrinit()."
 *
 * The tpsvrthrinit(3c) reference page also states that the default version of
 * tpsvrthrinit() calls tx_open(). However, the "TX Transactions" paragraph
 * of "Introduction to the C Language Application-to-Transaction Monitor
 * Interface" says this:
 *
 *     "...service routine writers wanting to use the TX Interface must
 *     supply their own tpsvrinit() routine that calls tx_open(). The
 *     default BEA Tuxedo ATMI system-supplied tpsvrinit() calls tpopen().
 *     The same rule applies for tpsvrdone(): if the TX Interface is being
 *     used, then service routine writers must supply their own tpsvrdone()
 *     that calls tx_close()."
 *
 * Given that there has probably gone more thought about this issue into the
 * above quote than in the man pages, I conclude that tpopen and not tx_open()
 * must be called if the Java server class does not provide tpsvrthrinit().
 *
 * Similar rules apply for tpsvrdone()/tpsvrthrdone() and tpclose().
 */

static OTPTuxServerConfig self;

static int registerNativeServerMethods(JNIEnv *env);

static int serverClassIndex; /* set by tpsvrinit() */

static int call_tpsvrthrinit_or_tpopen(JNIEnv *env, int argc, char *argv[])
{
    if (javaServer.m_tpsvrthrinit == NULL) {
	/* Server class does not provide tpsvrthrinit() */
	if (tpopen() == -1) {
	    /* See above for why we call tpopen() rather than tx_open() */
	    OTPUserlog("ERROR: tpopen() failed: %s", tpstrerror(tperrno));
	    return -1;
	}
	return 0;
    } else {
	/* Server class provides tpsvrthrinit() */
	int result = JavaServer_call_tpsvrthrinit(env, 
	    argc - serverClassIndex - 1, argv + serverClassIndex + 1);
	if ((*env)->ExceptionCheck(env)) {
	    OTPUserlog("ERROR: Exception invoking %s.tpsvrthrinit()",
		javaServer.className);
	    JTuxLogAndClearException(env);
	    return -1;
	}
	return result;
    }
}

int tpsvrinit(int argc, char *argv[])
{
    char *err = NULL;

    OTPTuxServerImpl serverImpl;

    int i;

    char *classPath = NULL;

    serverImpl.tpservice = tpservice;
    serverImpl.getDispatchInfo = getDispatchInfo;
    serverImpl.freeDispatchInfo = freeDispatchInfo;
    serverImpl.dispatch = dispatch;

    if (OTPTuxServerConfigSelf(&self, &err) == -1) {
	OTPUserlog("ERROR: Error retrieving server configuration: %s", err);
	free(err);
	return -1;
    }

    if (OTPTuxServerInit(&serverImpl, &err) == -1) {
	OTPUserlog("ERROR: Error initializing server: %s", err);
	free(err);
	return -1;
    }

    i = optind;

    while (i < argc) {
	char *arg = argv[i];
        if (strcmp(arg, "-s") == 0) {
	    i = i + 2; /* skip -s <arg> */
	} else if (strcmp(arg, "-classpath") == 0) {
	    if (i == argc - 1) {
		OTPUserlog("ERROR: Missing argument for %s option", arg);
		return -1;
	    }
	    classPath = argv[i + 1];
	    i = i + 2;
	} else {
	    break;
	}
    }

    if (i >= argc) {
	OTPUserlog("ERROR: Missing server class name in command line");
	return -1;
    }

    if (JTuxJVMInit(classPath, &theJvm, &mainThreadEnv, &err) == -1) {
	OTPUserlog("ERROR: Error initializing JVM: %s", err);
	free(err);
	return -1;
    }

    if (JTuxJTAInitialize(theJvm, 1, &err) == -1) {
	OTPUserlog("ERROR: Error initializing JTA: %s", err);
	free(err);
	return -1;
    }

    if (registerNativeServerMethods(mainThreadEnv) == -1) {
	OTPUserlog("ERROR: Exception registering native server methods");
	JTuxLogAndClearException(mainThreadEnv);
	return -1;
    }

    if (initJavaServer(mainThreadEnv, argv[i]) == -1) {
	OTPUserlog("ERROR: Exception initializing Java server");
	JTuxLogAndClearException(mainThreadEnv);
	return -1;
    }

    serverClassIndex = i;

    for (i = optind + 1; i < serverClassIndex; i = i + 2) {
	if ((strcmp(argv[i - 1], "-s") == 0)
	        && (OTPTuxServerSetup(argv[i], &err) == -1)) {
	    OTPUserlog("ERROR: Error setting up server: %s", err);
	    free(err);
	    return -1;
	}
    }

    if (javaServer.m_tpsvrinit == NULL) {
	/* Server class does not provide tpsvrinit() */
	if (self.MAXDISPATCHTHREADS < 2) {
	    /* Server is running in single-threaded mode */
	    if (call_tpsvrthrinit_or_tpopen(mainThreadEnv, argc, argv) == -1) {
		return -1;
	    }
	}
	/* Log startup message as per tpsvrinit(3c) */
	OTPUserlog("INFO: %s started successfully", javaServer.className);
    } else {
	/* Server class provides tpsvrinit() */
	int result = JavaServer_call_tpsvrinit(mainThreadEnv, 
	    argc - serverClassIndex - 1, argv + serverClassIndex + 1);
        if ((*mainThreadEnv)->ExceptionCheck(mainThreadEnv)) {
	    OTPUserlog("ERROR: Exception invoking %s.tpsvrinit()",
		javaServer.className);
	    JTuxLogAndClearException(mainThreadEnv);
	    return -1;
	}
	return result;
    }

    return 0;
}

static void call_tpsvrdone_or_tpclose(JNIEnv *env)
{
    if (javaServer.m_tpsvrthrdone == NULL) {
	/* Server class does not provide tpsvrthrdone() */
	if (tpclose() == -1) {
	    /* See above for why we call tpclose() rather than tx_close() */
	    OTPUserlog("WARNING: tpclose() failed: %s", tpstrerror(tperrno));
	}
    } else {
	/* Server class provides tpsvrthrdone() */
	JavaServer_call_tpsvrthrdone(env);
        if ((*env)->ExceptionCheck(env)) {
	    OTPUserlog("WARNING: Exception invoking %s.tpsvrthrdone()",
		javaServer.className);
	    JTuxLogAndClearException(env);
	}
    }
}

void tpsvrdone()
{
    if (javaServer.m_tpsvrdone == NULL) {
	/* Server class does not provide tpsvrdone() */
	if (self.MAXDISPATCHTHREADS < 2) {
	    /* Server is running in single-threaded mode */
	    call_tpsvrdone_or_tpclose(mainThreadEnv);
	}
	/* Log shutdown message as per tpsvrdone(3c) */
	OTPUserlog("INFO: %s exiting", javaServer.className);
    } else {
	/* Server class provides tpsvrdone() */
	JavaServer_call_tpsvrdone(mainThreadEnv);
        if ((*mainThreadEnv)->ExceptionCheck(mainThreadEnv)) {
	    OTPUserlog("WARNING: Exception invoking %s.tpsvrdone()",
		javaServer.className);
	    JTuxLogAndClearException(mainThreadEnv);
	}
    }
}

/*-----------------------------------*/
/* tpsvrthrinit() and tpsvrthrdone() */
/*-----------------------------------*/

static initThreadContextClassLoader(JNIEnv *env)
{
    /*
     * The context class loader of a native thread that gets attached
     * to the JVM is null (a simple test server will show this). In a
     * 'normal' Java environment, however, the context class loader of
     * newly created threads is the class loader that loaded the
     * application class (unless explictly set to something else).
     * Some Java APIs, most notably JNDI, use the thread's context
     * class loader for loading classes at runtime and in our case,
     * they will not be able to load any classes from the CLASSPATH
     * (the technical reason for this being that in JDK 1.4, JNDI is
     * loaded by the bootstrap classloader and not by the system
     * classloader, which would know how to load classes from
     * CLASSPATH). To solve this problem, we explicitly set the context
     * class loader of attached threads to the system class loader, which
     * is the same as the class loader that loads our server class.
     */

    jobject currentThread;
    jobject systemClassLoader;

    currentThread = java_lang_Thread_call_currentThread(env);
    if (currentThread == NULL) {
	goto exception;
    }

    systemClassLoader = java_lang_ClassLoader_call_getSystemClassLoader(env);
    if (systemClassLoader == NULL) {
	goto exception_after_currentThread;
    }

    java_lang_Thread_call_setContextClassLoader(env, currentThread, 
	systemClassLoader);
    if ((*env)->ExceptionCheck(env)) {
	goto exception_after_systemClassLoader;
    }

    (*env)->DeleteLocalRef(env, systemClassLoader);

    (*env)->DeleteLocalRef(env, currentThread);

    return 0;

exception_after_systemClassLoader:

    (*env)->DeleteLocalRef(env, systemClassLoader);

exception_after_currentThread:

    (*env)->DeleteLocalRef(env, currentThread);

exception:

    return -1;
}

int tpsvrthrinit(int argc, char **argv)
{
    /* 
     * Here is an excerpt from the Tuxedo documentation about tpsvrthrinit():
     *
     *     If an error occurs in tpsvrthrinit(), the application can cause
     *     the server dispatch thread to exit gracefully (and not take any
     *     service requests) by returning -1. 
     *
     * The question is: What does 'exit gracefully' mean? I had expected
     * this would mean that this causes the thread initialization to fail
     * so that tpsvrthrdone() is not called (just like tpsvrdone() is not
     * called if tpsvrinit() fails). But it turns out that tpsvrthrdone()
     * is called, even if tpsvrthrinit() fails. As a result, we must *not*
     * detach the thread from the JVM if tpsvrthrinit() fails, but leave
     * that for tpsvrthrdone().
     */

    char *err = NULL;

    JNIEnv *env;

    JavaVMAttachArgs args;

    args.version = JNI_VERSION_1_4;
    args.name = NULL;
    args.group = NULL;

    if (OTPJavaVMAttachCurrentThread(theJvm, &args, &env, &err) == -1) {
	OTPUserlog("ERROR: Error attaching thread to JVM: %s", err);
	free(err);
	return -1;
    }

    if (initThreadContextClassLoader(env) == -1) {
	OTPUserlog("ERROR: Exception setting thread context class loader");
	JTuxLogAndClearException(env);
	return -1;
    }

    return call_tpsvrthrinit_or_tpopen(env, argc, argv);
}

void tpsvrthrdone()
{
    char *err = NULL;

    JNIEnv *env;

    if (OTPJavaVMGetEnv(theJvm, JNI_VERSION_1_4, &env, &err) == -1) {
	OTPUserlog("WARNING: Error getting JNIEnv: %s", err);
	free(err);
	return;
    }

    call_tpsvrdone_or_tpclose(env);

    if (OTPJavaVMDetachCurrentThread(theJvm, &err) == -1) {
	OTPUserlog("WARNING: Error detaching thread from JVM: %s", err);
	free(err);
    }
}

/*-----------------------*/
/* Native server methods */
/*-----------------------*/

static jint Java_jtux_JTuxInterop_tpadvertise(JNIEnv *env, jobject obj,
    jstring svc, jstring func)
{
    const char *c_svc;
    const char *c_func;
    int result;

    if ((svc == NULL) || (func == NULL)) {
	tperrno = TPEINVAL;
	return -1; /* tperrno set => return -1 */
    }

    c_svc = (*env)->GetStringUTFChars(env, svc, NULL);
    if (c_svc == NULL) {
	return 0; /* exception => return anything */
    }

    c_func = (*env)->GetStringUTFChars(env, func, NULL);
    if (c_func == NULL) {
	(*env)->ReleaseStringUTFChars(env, svc, c_svc);
	return 0; /* exception => return anything */
    }

    result = OTPTuxServerAdvertise((char *) c_svc, (char *) c_func);

    (*env)->ReleaseStringUTFChars(env, func, c_func);

    (*env)->ReleaseStringUTFChars(env, svc, c_svc);

    return result;
}

static jint Java_jtux_JTuxInterop_tpunadvertise(JNIEnv *env, jobject obj,
    jstring svc)
{
    const char *c_svc;
    int result;

    if (svc == NULL) {
	tperrno = TPEINVAL;
	return -1; /* tperrno set => return -1 */
    }

    c_svc = (*env)->GetStringUTFChars(env, svc, NULL);
    if (c_svc == NULL) {
	return 0; /* exception => return anything */
    }

    result = unadvertise(env, c_svc);

    (*env)->ReleaseStringUTFChars(env, svc, c_svc);

    return result;
}

static jint Java_jtux_JTuxInterop_tpreturn(JNIEnv *env, jobject obj,
    jint rval, jint rcode, jobject data, jint len, jint flags)
{
    char *c_data;

    if (data == NULL) {
	c_data = NULL;
    } else {
	c_data = (char *) (*env)->GetDirectBufferAddress(env, data);
	if (c_data == NULL) {
	    return 0; /* exception => return anything */
	}
    }

    return OTPTuxServerReturn(rval, rcode, c_data, len, flags);
}

static jint Java_jtux_JTuxInterop_tpforward(JNIEnv *env, jobject obj,
    jstring svc, jobject data, jint len, jint flags)
{
    const char *c_svc;
    char *c_data;
    int result;

    if (svc == NULL) {
	tperrno = TPEINVAL;
	return -1; /* tperrno set => return -1 */
    }

    c_svc = (*env)->GetStringUTFChars(env, svc, NULL);
    if (c_svc == NULL) {
	return 0; /* exception => return anything */
    }

    if (data == NULL) {
	c_data = NULL;
    } else {
	c_data = (char *) (*env)->GetDirectBufferAddress(env, data);
	if (c_data == NULL) {
	    (*env)->ReleaseStringUTFChars(env, svc, c_svc);
	    return 0; /* exception => return anything */
	}
    }

    result = OTPTuxServerForward((char *) c_svc, c_data, len, flags);

    (*env)->ReleaseStringUTFChars(env, svc, c_svc);

    return result;
}

#define JSTRING "Ljava/lang/String;"
#define JBYTEBUFFER "Ljava/nio/ByteBuffer;"

static JNINativeMethod nativeServerMethods[] = {
    { "tpadvertise", "(" JSTRING JSTRING ")I",
       	(void *) Java_jtux_JTuxInterop_tpadvertise },
    { "tpunadvertise", "(" JSTRING ")I",
	(void *) Java_jtux_JTuxInterop_tpunadvertise },
    { "tpreturn", "(II" JBYTEBUFFER "II)I",
	(void *) Java_jtux_JTuxInterop_tpreturn },
    { "tpforward", "(" JSTRING JBYTEBUFFER "II)I",
	(void *) Java_jtux_JTuxInterop_tpforward },
    { NULL, NULL, NULL },
};

static int registerNativeServerMethods(JNIEnv *env)
{
    jclass jtuxInteropClass = jtux_JTuxInterop_class(env);
    if (jtuxInteropClass != NULL) {
        const JNINativeMethod *method = nativeServerMethods;
        while (method->name != NULL) {
	    if ((*env)->RegisterNatives(env, jtuxInteropClass,
		    method, 1) != 0) {
	        OTPUserlog("ERROR: Failed to register native method "
		    "jtux.JTuxInterop.%s", method->name);
	        return -1;
	    }
	    method++;
        }
        return 0;
    } else {
	return -1;
    }
}
