/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPUserlog_h_included
#define OTPUserlog_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
void OTPUserlog(const char *fmt, ...);

#ifdef __cplusplus
}
#endif

#endif
