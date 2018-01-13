/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPJavaVM_h_included
#define OTPJavaVM_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
int OTPJavaVMAddOption(JavaVMInitArgs *jvmArgs, char *optionString,
    void *extraInfo, char **errorInfo);

OTP_FUNC_DECL
int OTPJavaVMCreate(const char *jniLibraryName, JavaVMInitArgs *jvmArgs,
    JavaVM **jvmPtr, JNIEnv **envPtr, char **errorInfo);

OTP_FUNC_DECL
int OTPJavaVMDestroy(JavaVM *jvm, char **errorInfo);

OTP_FUNC_DECL
int OTPJavaVMGetEnv(JavaVM *jvm, jint version, JNIEnv **envPtr, 
    char **errorInfo);

OTP_FUNC_DECL
int OTPJavaVMAttachCurrentThread(JavaVM *jvm, JavaVMAttachArgs *threadArgs,
    JNIEnv **envPtr, char **errorInfo);

OTP_FUNC_DECL
int OTPJavaVMDetachCurrentThread(JavaVM *jvm, char **errorInfo);

#ifdef __cplusplus
}
#endif

#endif
