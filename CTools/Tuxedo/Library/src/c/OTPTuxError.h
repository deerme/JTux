/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPTuxError_h_included
#define OTPTuxError_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include "OTPError.h" /* For OTP__FILE__ check */

#include <atmi.h>
#include <fml32.h>

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
void OTPTuxErrorSetTPSystemCallFailure(char **errorInfo,
    const char *functionName, int errorCode, const char *file, int line);

OTP_FUNC_DECL
void OTPTuxErrorSetF32SystemCallFailure(char **errorInfo,
    const char *functionName, int errorCode, const char *file, int line);

#ifdef __cplusplus
}
#endif

#define SET_TP_ERROR(FUNC) \
    OTPTuxErrorSetTPSystemCallFailure(errorInfo, FUNC, tperrno, \
	OTP__FILE__, __LINE__)

#define SET_F32_ERROR(FUNC) \
    OTPTuxErrorSetF32SystemCallFailure(errorInfo, FUNC, Ferror32, \
	OTP__FILE__, __LINE__)

#endif
