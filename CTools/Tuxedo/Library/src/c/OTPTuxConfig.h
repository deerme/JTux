/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPTuxConfig_h_included
#define OTPTuxConfig_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

typedef struct {
    char *SRVGRP;
    char *SERVERNAME;
    char *CLOPT;
    char *ENVFILE;
    char *RCMD;
    char *RESTART;
    char *SYSTEM_ACCESS;
    char *CONV;
    char *REPLYQ;
    char *RQADDR;
    long SRVID;
    long GRPNO;
    long BASESRVID;
    long GRACE;
    long MAXGEN;
    long MAX;
    long MIN;
    long MINDISPATCHTHREADS;
    long MAXDISPATCHTHREADS;
    long THREADSTACKSIZE;
    long SEQUENCE;
    long RPPERM;
    long RQPERM;
} OTPTuxServerConfig;

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
int OTPTuxServerConfigSelf(OTPTuxServerConfig *config, char **errorInfo);

OTP_FUNC_DECL
void OTPTuxServerConfigClear(OTPTuxServerConfig *config);

#ifdef __cplusplus
}
#endif

#endif
