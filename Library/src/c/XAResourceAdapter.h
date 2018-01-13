/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef XAResourceAdapter_h_included
#define XAResourceAdapter_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include <jni.h>

OTP_FUNC_DECL
int XAResourceAdapter_init(JNIEnv *env, const char *name);

OTP_FUNC_DECL
jobject XAResourceAdapter_call_open(JNIEnv *env, const char *xaInfo);

OTP_FUNC_DECL
void XAResourceAdapter_call_close(JNIEnv *env);

#endif /* XAResourceAdapter_h_included */

