/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPTuxServer_h_included
#define OTPTuxServer_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include <atmi.h>

typedef struct {
    void (*unsol)(char *data, long len, long flags, void *unsolInfo);
} OTPTuxClient;

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
int OTPTuxClientInit(OTPTuxClient *client, char **errorInfo);

OTP_FUNC_DECL
void OTPTuxClientDone();

OTP_FUNC_DECL
void *OTPTuxClientGetUnsolInfo()

OTP_FUNC_DECL
int OTPTuxClientSetUnsol(void *unsolInfo);

OTP_FUNC_DECL
int OTPTuxClientClearUnsol();

#ifdef __cplusplus
}
#endif

#endif
