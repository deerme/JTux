/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef JTuxJTA_h_included
#define JTuxJTA_h_included

#include <jni.h>
#include <xa.h>

int JTuxJTAInitialize(JavaVM *jvm, int multiThreaded, char **errorInfo);

int JTuxJTAOpen(const char *rm, int *xaResult, char **errorInfo);

int JTuxJTAClose(int *xaResult, char **errorInfo);

int JTuxJTAStart(XID *xid, long flags, int *xaResult, char **errorInfo);

int JTuxJTAEnd(XID *xid, long flags, int *xaResult, char **errorInfo);

int JTuxJTAPrepare(XID *xid, long flags, int *xaResult, char **errorInfo);

int JTuxJTACommit(XID *xid, long flags, int *xaResult, char **errorInfo);

int JTuxJTARollback(XID *xid, long flags, int *xaResult, char **errorInfo);

int JTuxJTAForget(XID *xid, long flags, int *xaResult, char **errorInfo);

int JTuxJTARecover(XID *xids, long count, long flags, int *xaResult, 
    char **errorInfo);

#endif
