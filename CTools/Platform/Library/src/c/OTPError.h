/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPError_h_included
#define OTPError_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include <stdio.h>

/*
 * We don't  use the __FILE__ macro as this will put the full path of
 * the source file in the generated object file. Instead, we require
 * the OTP__FILE__ macro to be set.
 */
	
#ifndef OTP__FILE__
#error Macro OTP__FILE__ not set
#endif

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
void OTPErrorSet(char **errorInfo, const char *format, ...);

OTP_FUNC_DECL
void OTPErrorAddInfo(char **errorInfo, const char *format, ...);

OTP_FUNC_DECL
void OTPErrorSetMallocFailure(char **errorInfo, size_t size,
    const char* file, int line);

OTP_FUNC_DECL
void OTPErrorSetSystemCallFailure(char **errorInfo, const char *functionName,
    int errorCode, const char *file, int line);

OTP_FUNC_DECL
void OTPErrorSetSystemCallFailure2(char **errorInfo, const char *functionName, 
    const char *errorString, const char *file, int line);

#ifdef __cplusplus
}
#endif

#define SET_ERROR(MSG) OTPErrorSet(errorInfo, MSG);

#define SET_MALLOC_FAILURE(SIZE) \
    OTPErrorSetMallocFailure(errorInfo, SIZE, OTP__FILE__, __LINE__)

#define SET_SYSTEM_CALL_FAILURE(FUNC, ERR) \
    OTPErrorSetSystemCallFailure(errorInfo, FUNC, ERR, OTP__FILE__, __LINE__)

#define SET_SYSTEM_CALL_FAILURE_2(FUNC, ERR) \
    OTPErrorSetSystemCallFailure2(errorInfo, FUNC, ERR, OTP__FILE__, __LINE__)

#define ADD_ERROR_CONTEXT(CTX) OTPErrorAddInfo(errorInfo, CTX)

#endif /* OTPError_h_included */
