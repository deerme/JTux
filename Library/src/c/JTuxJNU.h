/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef JTuxJNU_h_included
#define JTuxJNU_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include <jni.h>

OTP_FUNC_DECL
void JTuxLogAndClearException(JNIEnv *env);

OTP_FUNC_DECL
jclass JTuxFindClassByName(JNIEnv *env, const char *className);

OTP_FUNC_DECL
jclass JTuxFindClassByNameBounded(JNIEnv *env, const char *className,
     int len);

OTP_FUNC_DECL
void JTuxThrowRuntimeException(JNIEnv *env, const char* message);

OTP_FUNC_DECL
void JTuxThrowNullPointerException(JNIEnv *env, const char* message);

OTP_FUNC_DECL
void JTuxThrowIllegalStateException(JNIEnv *env, const char* message);

OTP_FUNC_DECL
void JTuxThrowIllegalArgumentException(JNIEnv *env, const char* message);

OTP_FUNC_DECL
void JTuxThrowUnsupportedOperationException(JNIEnv *env,
    const char* message);

OTP_FUNC_DECL
jbyteArray JTuxNewByteArray(JNIEnv *env, jbyte *bytes, jsize length);

OTP_FUNC_DECL
jobject JTuxNewTypedBuffer(JNIEnv *env, char *buffer, long position,
    long limit);

OTP_FUNC_DECL
jarray JTuxNewArgs(JNIEnv *env, int argc, char **argv);

#endif
