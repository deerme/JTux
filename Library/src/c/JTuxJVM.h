/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef JTuxJVM_h_included
#define JTuxJVM_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include <jni.h>

OTP_FUNC_DECL
int JTuxJVMInit(const char *classPath, JavaVM **jvmPtr, JNIEnv **envPtr,
    char **errorInfo);

#endif
