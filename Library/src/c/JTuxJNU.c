/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "JTuxJNU.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "JTuxJNU.h"

#include "java_lang_String.h"
#include "java_lang_RuntimeException.h"
#include "java_lang_NullPointerException.h"
#include "java_lang_IllegalStateException.h"
#include "java_lang_IllegalArgumentException.h"
#include "java_lang_UnsupportedOperationException.h"
#include "java_nio_Buffer.h"
#include "jtux_TUX.h"

#include "OTPString.h"

#include <atmi.h> /* tuxgetenv(), tptypes() */
#include <userlog.h>
#include <jni.h>
#include <string.h> /* strlen() */
#include <stdlib.h> /* free() */

OTP_FUNC_DEF
void JTuxLogAndClearException(JNIEnv *env)
{
    jthrowable exception = (*env)->ExceptionOccurred(env);
    if (exception == NULL) {
	userlog("WARNING: JTuxLogAndClearException() called "
	    "without pending exception");
	return;
    }

    (*env)->ExceptionClear(env);

    jtux_TUX_call_logStackTrace(env, exception);
    if (!(*env)->ExceptionCheck(env)) {
	(*env)->DeleteLocalRef(env, exception);
        return;
    }

    userlog("ERROR: Failed to write exception to ULOG, see stderr");
    fprintf(stderr, "JTux: ERROR: Failed to write exception to ULOG\n");
    fprintf(stderr, "Caused by: ");
    (*env)->ExceptionDescribe(env);
    (*env)->Throw(env, exception);
    fprintf(stderr, "Original exception: ");
    (*env)->ExceptionDescribe(env);
    (*env)->DeleteLocalRef(env, exception);
}

OTP_FUNC_DEF
jclass JTuxFindClassByNameBounded(JNIEnv *env, const char *className,
    int len)
{
    char *classDescriptor;
    char *ch;
    jclass cls;

    classDescriptor = OTPStringDuplicateBounded(className, len, NULL);
    
    ch = classDescriptor;
    while (*ch != '\0') {
	if (*ch == '.') {
	    *ch = '/';
	}
	ch++;
    }

    cls = (*env)->FindClass(env, classDescriptor);
    
    free(classDescriptor);

    return cls;
}

OTP_FUNC_DEF
jclass JTuxFindClassByName(JNIEnv *env, const char *className)
{
    return JTuxFindClassByNameBounded(env, className, strlen(className));
}

#define THROW_EXCEPTION(TYPE) \
    jclass exceptionClass = TYPE ## _class(env); \
    if (exceptionClass != NULL) { \
        (*env)->ThrowNew(env, exceptionClass, message); \
    }

OTP_FUNC_DEF
void JTuxThrowRuntimeException(JNIEnv *env, const char *message)
{
    THROW_EXCEPTION(java_lang_RuntimeException)
}
 
OTP_FUNC_DEF
void JTuxThrowNullPointerException(JNIEnv *env, const char *message)
{
    THROW_EXCEPTION(java_lang_NullPointerException)
}

OTP_FUNC_DEF
void JTuxThrowIllegalStateException(JNIEnv *env, const char *message)
{
    THROW_EXCEPTION(java_lang_NullPointerException)
}

OTP_FUNC_DEF
void JTuxThrowIllegalArgumentException(JNIEnv *env, const char *message)
{
    THROW_EXCEPTION(java_lang_IllegalArgumentException)
}

OTP_FUNC_DEF
void JTuxThrowUnsupportedOperationException(JNIEnv *env, const char *message)
{
    THROW_EXCEPTION(java_lang_UnsupportedOperationException)
}

OTP_FUNC_DEF
jbyteArray JTuxNewByteArray(JNIEnv *env, jbyte *bytes, jsize length)
{
    jbyteArray result = (*env)->NewByteArray(env, length);
    if (result != NULL) {
	(*env)->SetByteArrayRegion(env, result, 0, length, bytes);
	return result;
    }
    return NULL;
}

OTP_FUNC_DEF
jobject JTuxNewTypedBuffer(JNIEnv *env, char *buffer, long position,
    long limit)
{
    jobject obj;

    jobject tmp;

    /*
     * According to the "Introduction to the C Language ATMI Interface"
     * documentation, tperrno is not cleared on successful calls. It should
     * therefore be safe to call this function while tperrno is set as the
     * tptypes() call below should not reset it. Unfortunately, this is
     * not the behaviour in practice. As a result we need to save and restore
     * tperrno before and after calling tptypes(). The good thing is that
     * restoring tperrno can be done by simply assigning to tperrno.
     */

    int old_tperrno = tperrno;

    long size = tptypes(buffer, NULL, NULL);
    if (size == -1) {
        JTuxThrowRuntimeException(env, "tptypes() failed on buffer "
	    "received from Tuxedo");
	return NULL;
    }

    tperrno = old_tperrno; /* This appears to work */

    obj = (*env)->NewDirectByteBuffer(env, buffer, (jlong) size);
    if (obj == NULL) {
	return NULL;
    }

    // Note that Buffer.position() and Buffer.limit() return a reference
    // to the buffer that must be deleted to prevent a resource leak.
    // We assume that these methods return NULL if and only if an
    // exception occurred.

    tmp = java_nio_Buffer_call_position(env, obj, position);
    if (tmp == NULL) {
	(*env)->DeleteLocalRef(env, obj);
	return NULL;
    }
    (*env)->DeleteLocalRef(env, tmp);

    tmp = java_nio_Buffer_call_limit(env, obj, limit);
    if (tmp == NULL) {
	(*env)->DeleteLocalRef(env, obj);
	return NULL;
    }
    (*env)->DeleteLocalRef(env, tmp);

    return obj;
}

OTP_FUNC_DEF
jarray JTuxNewArgs(JNIEnv *env, int argc, char **argv)
{
    jclass stringClass = java_lang_String_class(env);
    if (stringClass == NULL) {
	return NULL;
    } else {
        jarray args = (*env)->NewObjectArray(env, argc, stringClass, NULL);
        if (args == NULL) {
	    return NULL;
        } else {
	    int i = 0;
	    while (i < argc) {
	        jstring arg = (*env)->NewStringUTF(env, argv[i]);
	        if (arg == NULL) {
	            (*env)->DeleteLocalRef(env, args);
	            return NULL;
	        }
                (*env)->SetObjectArrayElement(env, args, i, arg);
    	        (*env)->DeleteLocalRef(env, arg);
		i++;
	    }
	    return args;
	}
    }
}
