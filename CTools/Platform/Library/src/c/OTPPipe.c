/* 
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPPipe.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPPipe.h"
#include "OTPError.h"

/*--------------------------------------------------------------------------*/
#ifdef _WIN32                            /* Windows specific implementation */
/*--------------------------------------------------------------------------*/

#define MAX_DWORD ((DWORD) -1)

OTP_FUNC_DEF
int OTPPipeRead(OTPPipe pipe, void *buf, size_t size, size_t *len,
    char **errorInfo)
{
    /*
     * From the Windows Platform SDK documentation: "If the anonymous
     * write pipe handle has been closed and ReadFile attempts to read
     * using the corresponding anonymous read pipe handle, the function
     * returns FALSE and GetLastError returns ERROR_BROKEN_PIPE."
     */

    DWORD bytesRead;

    if (size > MAX_DWORD) { /* Prevent overflow of DWORD */
	size = MAX_DWORD;
    }

    if (!ReadFile(pipe, buf, size, &bytesRead, NULL)) {
       	if (GetLastError() == ERROR_BROKEN_PIPE) {
	    *len = 0;
	    return 0;
        } else {
	    SET_SYSTEM_CALL_FAILURE("ReadFile", GetLastError());
	    return -1;
	}
    } else {
        if (bytesRead == 0) {
	    SET_ERROR("ReadFile() succeeded w/o reading any data");
	    return -1;
        } else {
	    *len = bytesRead;
	    return 0;
	}
    }
}

OTP_FUNC_DEF
int OTPPipeWrite(OTPPipe pipe, void *buf, size_t len, char **errorInfo)
{
    size_t excess;

    DWORD bytesToWrite;

    while (len > 0) {

	if (len > MAX_DWORD) {
	    excess = len - MAX_DWORD;
	    bytesToWrite = MAX_DWORD;
	} else {
	    excess = 0;
	    bytesToWrite = len;
	}

        while (bytesToWrite > 0) {
	    DWORD bytesWritten;
	    if (!WriteFile(pipe, buf, bytesToWrite, &bytesWritten, NULL)) {
		SET_SYSTEM_CALL_FAILURE("WriteFile", GetLastError());
		return -1;
	    } else {
		if (bytesWritten == 0) {
		    SET_ERROR("WriteFile() succeeded w/o writing any data");
		    return -1;
		}
		buf = ((char *) buf) + bytesWritten;
		bytesToWrite = bytesToWrite - bytesWritten;
	    }
	}

	len = excess;
    }

    return 0;
}

OTP_FUNC_DEF
int OTPPipeClose(OTPPipe pipe, char **errorInfo)
{
    if (!CloseHandle(pipe)) {
	SET_SYSTEM_CALL_FAILURE("CloseHandle", GetLastError());
	return -1;
    } else {
	return 0;
    }
}

/*--------------------------------------------------------------------------*/
#else                                       /* Unix specific implementation */
/*--------------------------------------------------------------------------*/

#include <unistd.h>
#include <errno.h>
#include <limits.h>

OTP_FUNC_DEF
int OTPPipeRead(OTPPipe pipe, void *buf, size_t size, size_t *len,
    char **errorInfo)
{
    ssize_t bytesRead;

    if (size > SSIZE_MAX) {
       	/* Prevent possible ambiguous result from read() */
	size = SSIZE_MAX;
    }

    bytesRead = read(pipe, buf, size);
    if (bytesRead == -1) {
	SET_SYSTEM_CALL_FAILURE("read", errno);
	return -1;
    } else {
	*len = bytesRead; /* 0 means broken pipe */
	return 0;
    }
}

OTP_FUNC_DEF
int OTPPipeWrite(OTPPipe pipe, void *buf, size_t len, char **errorInfo)
{
    size_t excess = 0;

    if (len > SSIZE_MAX) {
        excess = len - SSIZE_MAX;
	len = SSIZE_MAX;
    }

    while (len > 0) {
	ssize_t bytesWritten = write(pipe, buf, len);
	if (bytesWritten == -1) {
	    SET_SYSTEM_CALL_FAILURE("write", errno);
	    return -1;
	} else {
	    if (bytesWritten == 0) {
	        SET_ERROR("write() succeeded without writing any data");
	        return -1;
	    }
	    buf = ((char *) buf) + bytesWritten;
	    len = len - bytesWritten;
	}
    }

    if (excess > 0) {
	return OTPPipeWrite(pipe, buf, excess, errorInfo);
    } else {
        return 0;
    }
}

OTP_FUNC_DEF
int OTPPipeClose(OTPPipe pipe, char **errorInfo)
{
    if (close(pipe) == -1) {
	SET_SYSTEM_CALL_FAILURE("close", errno);
	return -1;
    } else {
	return 0;
    }
}

/*--------------------------------------------------------------------------*/
#endif                          /* End of platform specific implementations */
/*--------------------------------------------------------------------------*/
