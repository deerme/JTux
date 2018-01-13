/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPTime_h_included
#define OTPTime_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DEF
int OTPTimeParseDuration(const char *str, int len, int *seconds, 
    char **errorInfo);

#ifdef __cplusplus
}
#endif

#endif
