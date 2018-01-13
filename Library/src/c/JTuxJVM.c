/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "JTuxJVM.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "JTuxJVM.h"
#include "JTux.h"

#include "OTPString.h"
#include "OTPJavaVM.h"
#include "OTPUserlog.h"
#include "OTPError.h"

#include <jni.h>
#include <atmi.h> /* tuxgetenv() */
#include <string.h> /* strstr(), strrchr() */
#include <stdlib.h> /* free() */
#include <stdio.h>
#include <stdarg.h>

#ifdef _WIN32
#include <windows.h> /* GetModuleFileName(), GetLastError() */
#else
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h> /* stat() */
#endif

/*------------*/
/* JTux paths */
/*------------*/

#ifdef _WIN32
#define PS ";"
#define FS "\\"
#define DLLDIR "bin"
#else
#define PS ":"
#define FS "/"
#define DLLDIR "lib"
#endif

static char *jtuxHome = NULL;

static char *jtuxClassPath = NULL;

static char *jtuxLibraryPath = NULL;

static int initJTuxPaths(char **errorInfo)
{
    char *jtuxDir = tuxgetenv("JTUXDIR");
    if (jtuxDir == NULL) {
	SET_ERROR("Environment variable JTUXDIR not set");
	return -1;
    }

    jtuxHome = OTPStringCreate(NULL, "%s", jtuxDir);

    jtuxClassPath = OTPStringCreate(NULL, "%s" FS "lib" FS "JTux.jar"
	PS "%s" FS "lib" FS "javax.jms.jar", jtuxDir, jtuxDir);

    jtuxLibraryPath = OTPStringCreate(NULL, "%s" FS DLLDIR, jtuxDir);

    return 0;
}

/*----------------*/
/* JavaVMInitArgs */
/*----------------*/

static JavaVMInitArgs jvmArgs;

static char *libraryPath = NULL;

static int addJVMOption(char *optionString, char **errorInfo)
{
    return OTPJavaVMAddOption(&jvmArgs, optionString, NULL, errorInfo);
}

static int processJVMOption(const char *opt, int len,
    char **errorInfo)
{
    if (OTPStringStartsWith(opt, len, "-Djtux.home=")) {
	SET_ERROR("System property jtux.home should not be set "
	     "in JVM_OPTIONS");
	return -1;
    } else if (OTPStringStartsWith(opt, len, "-Djava.class.path=")) {
	SET_ERROR("System property java.class.path should not be set "
	    "in JVM_OPTIONS (use CLASSPATH or -classpath instead)");
	return -1;
    } else if (OTPStringStartsWith(opt, len, "-Djava.library.path=")) {
	if (libraryPath != NULL) {
	    free(libraryPath);
	}
	libraryPath = OTPStringDuplicateBounded(opt + 20, len - 20, NULL);
	return 0;
    } else {
	char *optionString = OTPStringDuplicateBounded(opt, len, NULL);
        return addJVMOption(optionString, errorInfo);
    }
}

static jint JNICALL vfprintf_hook(FILE *fp, const char *format, va_list args)
{
    char buf[64 * 1024];
    jint result = vsprintf(buf, format, args);
    if (result >= 0) {
        OTPUserlog("INFO [JVM] %s", buf);
    }
    return result;
}

static void JNICALL exit_hook(jint code)
{
    OTPUserlog("INFO: JVM exiting with exit code %d", code);
    exit(code);
}

static void JNICALL abort_hook()
{
    OTPUserlog("FATAL: JVM Aborting");
    abort();
}

static int initJVMArgs(const char *classPath, char **errorInfo)
{
    char *jvmOptions;

    char *JTUX_DEBUG = tuxgetenv("JTUX_DEBUG");
    
    int debug = (JTUX_DEBUG != NULL) && (strstr(JTUX_DEBUG, "JVM") != NULL);

    jvmArgs.version = JNI_VERSION_1_4;
    jvmArgs.nOptions = 0;
    jvmArgs.options = NULL;
    jvmArgs.ignoreUnrecognized = JNI_TRUE;

    /*
     * We set ignoreUnrecognized to true because the -Xusealtsigs option set
     * below is only recognized by the Sun JVM on Solaris. Since we cannot
     * determine which JVM we are loading, this option cannot be set
     * selectively and must therefore be ignored by other JVMs.
     */

    if (OTPJavaVMAddOption(&jvmArgs, "vfprintf", (void *) vfprintf_hook, 
            errorInfo) == -1) {
	return -1;
    }

    if (OTPJavaVMAddOption(&jvmArgs, "exit", (void *) exit_hook, 
            errorInfo) == -1) {
	return -1;
    }

    if (OTPJavaVMAddOption(&jvmArgs, "abort", (void *) abort_hook, 
            errorInfo) == -1) {
	return -1;
    }

    /*
     * Set the -Xusealtsigs property. This is needed because otherwise,
     * using the SUn JVM on Solaris, the JVM creation fails with the
     * following error message: "Signal chaining not allowed for VM
     * interrupt signal, try -Xusealtsigs." By setting -Xusealtsigs,
     * the JVM creation succeeds.
     */

    if (addJVMOption("-Xusealtsigs", errorInfo) == -1) {
        return -1;
    }

    if (initJTuxPaths(errorInfo) == -1) {
	return -1;
    }

    {   /* set jtux.home */

	char *option = OTPStringCreate(NULL, "-Djtux.home=%s", jtuxHome);

	if (addJVMOption(option, errorInfo) == -1) {
	    return -1;
	}
    }
    
    jvmOptions = tuxgetenv("JVM_OPTIONS");

    while (jvmOptions != NULL) {
	int jvmOptionLen;
        char *jvmOption = OTPStringLocateNonWhitespace(jvmOptions);
	if (jvmOption == NULL) {
	    break;
	}
	jvmOptions = OTPStringLocateWhitespace(jvmOption);
	if (jvmOptions == NULL) {
	    jvmOptionLen = strlen(jvmOption);
	} else {
	    jvmOptionLen = jvmOptions - jvmOption;
	}
	if (processJVMOption(jvmOption, jvmOptionLen, errorInfo) == -1) {
	    return -1;
	}
    }

    if (classPath != NULL) {
	if (debug) {
	    OTPUserlog("DEBUG [JVM]: Taking class path from command line");
	}
    } else {
        classPath = tuxgetenv("CLASSPATH");
	if (classPath != NULL) {
	    if (debug) {
	        OTPUserlog("DEBUG [JVM]: Taking class path from environment");
	    }
	} else {
	    classPath = ".";
	    if (debug) {
	        OTPUserlog("DEBUG [JVM]: Using default class path");
	    }
	}
    }

    {   /* set java.class.path */

	char *option = OTPStringCreate(NULL, "-Djava.class.path=%s" PS "%s",
	    jtuxClassPath, classPath);

	if (addJVMOption(option, errorInfo) == -1) {
	    return -1;
	}
    }

    if (libraryPath == NULL) {

	char *option = OTPStringCreate(NULL, "-Djava.library.path=%s",
	    jtuxLibraryPath);

	if (addJVMOption(option, errorInfo) == -1) {
	    return -1;
	}

    } else {

	char *option = OTPStringCreate(NULL, "-Djava.library.path=%s" PS "%s",
	    jtuxLibraryPath, libraryPath);

	if (addJVMOption(option, errorInfo) == -1) {
	    return -1;
	}
    }

    if (debug) {
	int i;
	OTPUserlog("DEBUG [JVM]: Creating JVM with the following %d options:",
	    jvmArgs.nOptions);
	for (i = 0; i < jvmArgs.nOptions; i++) {
	    OTPUserlog("%s", jvmArgs.options[i].optionString);
	}
    }

    return 0;
}

/*-------------*/
/* JTuxJVMInit */
/*-------------*/

OTP_FUNC_DEF
int JTuxJVMInit(const char * classPath, JavaVM **jvmPtr, JNIEnv **envPtr, 
    char **errorInfo)
{
    char *jniLibraryName = tuxgetenv("JNI_LIBRARY");

    if (jniLibraryName == NULL) {
	SET_ERROR("Environment variable JNI_LIBRARY not set");
	return -1;
    }

    if (initJVMArgs(classPath, errorInfo) == -1) {
	return -1;
    }

    return OTPJavaVMCreate(jniLibraryName, &jvmArgs, jvmPtr, envPtr, 
	errorInfo);
}
