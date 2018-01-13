/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPError.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPError.h"
#include "OTPString.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>

OTP_FUNC_DEF
void OTPErrorSet(char **errorInfo, const char *format, ...)
{
#ifdef ACC_VA_LIST_430_BUG /* See Makefile.HP-UX */
    va_list args = 0;
#else
    va_list args;
#endif

    int len;
    char *err;

    if (errorInfo == NULL) {
	fprintf(stderr, "PANIC: Unexpected error: %s\n", format);
	abort(); /* Generate core dump with stack trace */
    }

    va_start(args, format);
    len = OTPStringGetFormattedOutputLength(format, args, NULL);
    va_end(args);

    err = (char *) malloc(len + 1);
    if (err == NULL) {
	fprintf(stderr, "PANIC: malloc(%d) failed trying to set error '%s'", 
	    len + 1, format);
	abort(); /* Generate core dump with stack trace */
    }

    va_start(args, format);
    vsprintf(err, format, args);
    va_end(args);

    *errorInfo = err;
}

OTP_FUNC_DEF
void OTPErrorAddInfo(char **errorInfo, const char *format, ...)
{
    char *err;
    int errLen;

#ifdef ACC_VA_LIST_430_BUG /* See Makefile.HP-UX */
    va_list args = 0;
#else
    va_list args;
#endif

    int infoLen;
    int newErrLen;
    char *newErr;

    if (errorInfo == NULL) {
	fprintf(stderr, "PANIC: Unexpected error info: %s\n", format);
	abort(); /* Generate core dump with stack trace */
    }
   
    err = *errorInfo;
    if (err == NULL) {
	fprintf(stderr, "PANIC: Unexpected error info: %s\n", format);
	abort(); /* Generate core dump with stack trace */
    }

    errLen = strlen(err);

    va_start(args, format);
    infoLen = OTPStringGetFormattedOutputLength(format, args, NULL);
    va_end(args);

    newErrLen = infoLen + 2 + errLen;
    newErr = (char *) malloc(newErrLen + 1);
    if (newErr == NULL) {
	fprintf(stderr, "PANIC: malloc(%d) failed adding info '%s' "
	    "to error '%s'", newErrLen + 1, format, err);
	abort(); /* Generate core dump with stack trace */
    }

    va_start(args, format);
    vsprintf(newErr, format, args);
    va_end(args);

    sprintf(newErr + infoLen, ": %s", err);

    free(err);

    *errorInfo = newErr;
}

OTP_FUNC_DEF
void OTPErrorSetMallocFailure(char **errorInfo, size_t size, 
    const char *file, int line)
{
    OTPErrorSet(errorInfo, "malloc(%d) failed in %s at line %d", size, 
        file, line);
}

OTP_FUNC_DEF
void OTPErrorSetSystemCallFailure(char **errorInfo, const char *functionName,
    int errorCode, const char *file, int line)
{
    OTPErrorSet(errorInfo, "%s() failed with error code %d (0x%X) "
	"in file %s at line %d", functionName, errorCode, errorCode, 
	file, line);
}

OTP_FUNC_DEF
void OTPErrorSetSystemCallFailure2(char **errorInfo, const char *functionName,
    const char *errorString, const char *file, int line)
{
    OTPErrorSet(errorInfo, "%s() failed with error string '%s' in file %s "
	"at line %d", functionName, errorString, file, line);
}
