/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPTuxServer_h_included
#define OTPTuxServer_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include <atmi.h>

#include "OTPTuxServerImpl.h"

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
void OTPTuxServerSetTestMode();

OTP_FUNC_DECL
int OTPTuxServerInit(OTPTuxServerImpl *impl, char **errorInfo);

OTP_FUNC_DECL
void OTPTuxServerDone();

OTP_FUNC_DECL
int OTPTuxServerSetup(char *setupString, char **errorInfo);

OTP_FUNC_DECL
int OTPTuxServerAdvertise(char *svc, const char *func);

OTP_FUNC_DECL
int OTPTuxServerUnadvertise(char *svc);

OTP_FUNC_DECL
void OTPTuxServerDispatch(TPSVCINFO *svcInfo);

OTP_FUNC_DECL
int OTPTuxServerReturn(int rval, long rcode, char *data, long len,
    long flags);

OTP_FUNC_DECL
int OTPTuxServerForward(char *svc, char *data, long len, long flags);

#ifdef __cplusplus
}
#endif

#endif
