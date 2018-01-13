/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPPipe_h_included
#define OTPPipe_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

/*--------------------------------------------------------------------------*/
#ifdef _WIN32                               /* Windows specific definitions */
/*--------------------------------------------------------------------------*/

#include "OTPWindows.h" /* Don't include windows.h directly */

typedef HANDLE OTPPipe;

/*--------------------------------------------------------------------------*/
#else                                          /* Unix specific definitions */
/*--------------------------------------------------------------------------*/

#include <unistd.h>

typedef int OTPPipe;

/*--------------------------------------------------------------------------*/
#endif                              /* End of platform specific definitions */
/*--------------------------------------------------------------------------*/

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
int OTPPipeRead(OTPPipe pipe, void *buf, size_t size, size_t *len, 
    char **errorInfo);

OTP_FUNC_DECL
int OTPPipeWrite(OTPPipe pipe, void *buf, size_t len, char **errorInfo);

OTP_FUNC_DECL
int OTPPipeClose(OTPPipe pipe, char **errorInfo);

#ifdef __cplusplus
}
#endif

#endif /* OTPPipe_h_included */
