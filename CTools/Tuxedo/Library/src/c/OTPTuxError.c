/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPTuxError.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPTuxError.h"
#include "OTPError.h"

#include <atmi.h>
#include <fml32.h>
#include <stdio.h>

static void setError(char **errorInfo, const char *functionName, 
    int errorCode, const char *errorString, const char *file, int line)
{
    char buf[512];
    if (errorString == NULL) {
	errorString = "???";
    }
    sprintf(buf, "%s() failed with error code %d (%s) in file %s at line %d",
	functionName, errorCode, errorString, file, line);
    OTPErrorSet(errorInfo, buf);
}

OTP_FUNC_DEF
void OTPTuxErrorSetTPSystemCallFailure(char **errorInfo, 
    const char *functionName, int errorCode, const char *file, int line)
{
    setError(errorInfo, functionName, errorCode, tpstrerror(errorCode),
	file, line);
}

OTP_FUNC_DEF
void OTPTuxErrorSetF32SystemCallFailure(char **errorInfo, 
    const char *functionName, int errorCode, const char *file, int line)
{
    setError(errorInfo, functionName, errorCode, Fstrerror32(errorCode),
	file, line);
}
