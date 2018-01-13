/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPJavaVM.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPJavaVM.h"
#include "OTPLibrary.h"
#include "OTPString.h"
#include "OTPError.h"

#include <jni.h>

#include <stdlib.h>

static const char* getJNIErrorString(jint errorCode)
{
    switch (errorCode) {
	case JNI_OK:
	    return "JNI_OK: Success";
	case JNI_ERR:
	    return "JNI_ERR: Unknown error";
	case JNI_EDETACHED:
	    return "JNI_EDETACHED: Thread detached from VM";
	case JNI_EVERSION:
	    return "JNI_EVERSION: JNI version error";
	case JNI_ENOMEM:
	    return "JNI_ENOMEM: Not enough memory";
	case JNI_EEXIST:
	    return "JNI_EEXIST: VM already created";
	case JNI_EINVAL:
	    return "JNI_EINVAL: Invalid arguments";
	default:
	    return "???";
    }
}

static void setJNISystemCallFailure(char **errorInfo, 
    const char *functionName, int errorCode, const char *file, int line)
{
    char buf[512];
    sprintf(buf, "%s() failed with error code %d (%s) in file %s at line %d",
	functionName, errorCode, getJNIErrorString(errorCode), file, line);
    OTPErrorSet(errorInfo, buf);
}

#define SET_JNI_SYSTEM_CALL_FAILURE(FUNC, ERR) \
    setJNISystemCallFailure(errorInfo, FUNC, ERR, OTP__FILE__, __LINE__)

typedef int (JNICALL *JVMCreator)(JavaVM **jvmPtr, void **envPtr,
    void *jvmArgs);

OTP_FUNC_DEF
int OTPJavaVMAddOption(JavaVMInitArgs *jvmArgs, char *optionString,
    void *extraInfo, char **errorInfo)
{
    JavaVMOption *options = (jvmArgs->nOptions == 0) ? NULL : jvmArgs->options;

    int newSize = (jvmArgs->nOptions + 1) * sizeof(JavaVMOption);

    jvmArgs->options = (JavaVMOption *) realloc(options, newSize);
    if (jvmArgs->options == NULL) {
	SET_MALLOC_FAILURE(newSize);
	return -1;
    }

    jvmArgs->options[jvmArgs->nOptions].optionString = optionString;
    jvmArgs->options[jvmArgs->nOptions].extraInfo = extraInfo;
    jvmArgs->nOptions++;

    return 0;
}

OTP_FUNC_DEF
int OTPJavaVMCreate(const char *jniLibraryName, JavaVMInitArgs *jvmArgs,
    JavaVM **jvmPtr, JNIEnv **envPtr, char **errorInfo)
{
    OTPLibrary jniLibrary;

    JVMCreator jvmCreator;

    int result;

    if (OTPLibraryLoad(jniLibraryName, &jniLibrary, errorInfo) == -1) {
	ADD_ERROR_CONTEXT("Error loading JNI library");
	return -1;
    }

    if (OTPLibraryGetAddress(jniLibrary, "JNI_CreateJavaVM", 
	    (void **) &jvmCreator, errorInfo) == -1) {
	OTPErrorAddInfo(errorInfo, "Error getting symbol 'JNI_CreateJavaVM' "
	    "from JNI library %s", jniLibraryName);
	return -1;
    }

    result = (*jvmCreator)(jvmPtr, (void **) envPtr, jvmArgs);
    if (result != JNI_OK) {
	SET_JNI_SYSTEM_CALL_FAILURE("JNI_CreateJavaVM", result);
	return -1;
    }

    return 0;
}

OTP_FUNC_DEF
int OTPJavaVMDestroy(JavaVM *jvm, char **errorInfo)
{
    int result = (*jvm)->DestroyJavaVM(jvm);
    if (result != JNI_OK) {
	SET_JNI_SYSTEM_CALL_FAILURE("JavaVM->DestroyJavaVM", result);
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPJavaVMGetEnv(JavaVM *jvm, jint version, JNIEnv **envPtr,
    char **errorInfo)
{
    int result = (*jvm)->GetEnv(jvm, (void **) envPtr, version);
    if (result != JNI_OK) {
	SET_JNI_SYSTEM_CALL_FAILURE("JavaVM->GetEnv", result);
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPJavaVMAttachCurrentThread(JavaVM *jvm, JavaVMAttachArgs *threadArgs,
    JNIEnv **envPtr, char **errorInfo)
{
    int result = (*jvm)->AttachCurrentThread(jvm, (void **) envPtr,
	threadArgs);
    if (result != JNI_OK) {
	SET_JNI_SYSTEM_CALL_FAILURE("JavaVM->AttachCurrentThread", result);
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPJavaVMDetachCurrentThread(JavaVM *jvm, char **errorInfo)
{
    int result = (*jvm)->DetachCurrentThread(jvm);
    if (result != JNI_OK) {
	SET_JNI_SYSTEM_CALL_FAILURE("JavaVM->DetachCurrentThread", result);
	return -1;
    } else {
	return 0;
    }
}
