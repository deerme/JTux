/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPProcess_h_included
#define OTPProcess_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include "OTPPipe.h"

#ifdef _WIN32

#include "OTPWindows.h" /* Don't include windows.h directly */

typedef HANDLE OTPProcess;

#else

#include <sys/types.h>

typedef pid_t OTPProcess;

#endif

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Creates a child process and attaches pipes to the stdin, stdout and
 * stderr streams of the process.
 */
OTP_FUNC_DECL
int OTPProcessCreate(char *prog, int argc, char *argv[],
    OTPProcess *processPtr, OTPPipe *stdinWriteHandlePtr, 
    OTPPipe *stdoutReadHandlePtr, char **errorInfo);

/*
 * Waits for the process to terminate.
 * This function must be called to free the resources associated with
 * the process.
 * The exact interpretation of the returned exit code is platform dependent.
 * However, is seems reasonable on both Window and Unix to intepret exit
 * codes 0 to 255 as normal terminations and other error codes as abnormal
 * terminations.
 */
OTP_FUNC_DECL
int OTPProcessWait(OTPProcess process, int *exitCodePtr, char **errorInfo);

#ifdef __cplusplus
}
#endif

#endif /* OTPProcess_h_included */
