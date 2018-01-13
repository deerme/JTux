/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#include "JTux.h"
#include "JTuxJNU.h"
#include "JTuxJVM.h"

#include "OTPString.h"
#include "OTPUserlog.h"

#include <jni.h>

#include <userlog.h> /* proc_name */
#include <atmi.h> /* tuxputenv() */

#include <string.h> /* strrchr() */
#include <stdlib.h> /* getenv() */

typedef struct JavaClient {
    jclass cls;
    jmethodID m_main;
    char *className;
} JavaClient;

static JavaClient javaClient;

static int initJavaClient(JNIEnv *env, const char *className)
{
    jclass cls = JTuxFindClassByName(env, className);
    if (cls == NULL) {
	goto exception;
    }

    javaClient.m_main = (*env)->GetStaticMethodID(env, cls, "main",
	"([Ljava/lang/String;)V");
    if (javaClient.m_main == NULL) {
	goto exception_after_cls;
    }

    javaClient.cls = (*env)->NewGlobalRef(env, cls);
    if (javaClient.cls == NULL) {
	goto exception_after_cls;
    }

    javaClient.className = OTPStringDuplicate(className, NULL);

    (*env)->DeleteLocalRef(env, cls);

    return 0;

exception_after_cls:

    (*env)->DeleteLocalRef(env, cls);

exception:

    return -1;
}

static void JavaClient_call_main(JNIEnv *env, int argc, char *argv[])
{
    jarray args = JTuxNewArgs(env, argc, argv);
    if (args != NULL) {
        (*env)->CallStaticIntMethod(env, javaClient.cls,
	    javaClient.m_main, args);
        (*env)->DeleteLocalRef(env, args);
    }
}

static void set_ULOGPFX()
{
    /*
     * On Windows, and perhaps Unix as well, tuxgetenv("ULOGPFX") returns
     * %TUXDIR%\ULOG if ULOGPFX is not set which causes userlog() messages
     * to end up where nobody expects them. To prevent this behaviour, we
     * use getenv() to test wheter ULOGPFX is really set, and if not,
     * set it to ".\ULOG" so that userlog() messages end up in a more
     * sensible place (i.e., the directory from which JClient is run).
     */

    char *ULOGPFX = getenv("ULOGPFX");
    if (ULOGPFX == NULL) {
#ifdef _WIN32
        tuxputenv("ULOGPFX=.\\ULOG");
#else
        tuxputenv("ULOGPFX=./ULOG");
#endif
    }
}

static void set_proc_name(char *exe)
{
    /*
     * buildclient does not set proc_name so we do it here.
     * This must be done before the first userlog() call to 
     * have any effect.
     */

#ifdef _WIN32
    char *lastSlash = strrchr(exe, '\\');
#else
    char *lastSlash = strrchr(exe, '/');
#endif

    if (lastSlash == NULL) {
	proc_name = exe;
    } else {
	proc_name = lastSlash + 1;
    }
}

static JavaVM *theJvm;

static JNIEnv *mainThreadEnv;

int main(int argc, char **argv)
{
    char *err = NULL;

    char *classPath;

    int clientClassIndex;

#include "mainexit.h"

    set_ULOGPFX();

    set_proc_name(argv[0]);

    if ((argc > 1) && (strcmp(argv[1], "-classpath") == 0)) {
	if (argc == 2) {
	    OTPUserlog("ERROR: Missing argument for %s option", argv[1]);
	    exit(1);
	}
	classPath = argv[2];
	clientClassIndex = 3;
    } else {
	classPath = NULL;
	clientClassIndex = 1;
    }

    if (clientClassIndex >= argc) {
	OTPUserlog("ERROR: Missing client class name in command line");
	exit(1);
    }

    if (JTuxJVMInit(classPath, &theJvm, &mainThreadEnv, &err) == -1) {
	OTPUserlog("ERROR: Error initializing JVM: %s", err);
	free(err);
	exit(1);
    }

    if (initJavaClient(mainThreadEnv, argv[clientClassIndex]) == -1) {
	OTPUserlog("ERROR: Exception initializing Java client");
	JTuxLogAndClearException(mainThreadEnv);
	exit(1);
    }

    JavaClient_call_main(mainThreadEnv, argc - clientClassIndex - 1,
	argv + clientClassIndex + 1);
    if ((*mainThreadEnv)->ExceptionCheck(mainThreadEnv)) {
	OTPUserlog("ERROR: Exception invoking %s.main()", 
	    javaClient.className);
	JTuxLogAndClearException(mainThreadEnv);
	exit(1);
    }

    exit(0);
}
