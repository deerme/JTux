/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPTuxServerImpl_h_included
#define OTPTuxServerImpl_h_included

#include <atmi.h>

typedef struct {
    void (*tpservice)(TPSVCINFO *svcInfo);
    int (*getDispatchInfo)(const char *serviceRoutineName, 
	void **dispatchInfo);
    void (*freeDispatchInfo)(void *dispatchInfo);
    void (*dispatch)(TPSVCINFO *svcInfo, void *dispatchInfo);
} OTPTuxServerImpl;

#endif
