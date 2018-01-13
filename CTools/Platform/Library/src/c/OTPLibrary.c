/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPLibrary.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPLibrary.h"
#include "OTPError.h"

/*--------------------------------------------------------------------------*/
#ifdef _WIN32                               /* Windows specific definitions */
/*--------------------------------------------------------------------------*/

#include "OTPWindows.h" /* Don't include windows.h directly */

OTP_FUNC_DEF
int OTPLibraryLoad(const char *libraryName, OTPLibrary *libraryPtr, 
    char **errorInfo)
{
    HMODULE module = LoadLibrary(libraryName);
    if (module == NULL) {
	int error = GetLastError();
	if ((error == ERROR_FILE_NOT_FOUND)
	       	|| (error == ERROR_PATH_NOT_FOUND)) {
	    OTPErrorSet(errorInfo, "Library %s not found", libraryName);
	} else {
	    SET_SYSTEM_CALL_FAILURE("LoadLibrary", GetLastError());
	}
	return -1;
    } else {
	*libraryPtr = module;
        return 0;
    }
}

OTP_FUNC_DEF
int OTPLibraryGetAddress(OTPLibrary library, const char *symbolName, 
    void **addressPtr, char **errorInfo)
{
    FARPROC proc = GetProcAddress(library, symbolName);
    if (proc == NULL) {
	SET_SYSTEM_CALL_FAILURE("GetProcAddress", GetLastError());
	return -1;
    } else {
	*addressPtr = proc;
	return 0;
    }
}

/*--------------------------------------------------------------------------*/
#else                                          /* Unix specific definitions */
/*--------------------------------------------------------------------------*/

#include <dlfcn.h>

OTP_FUNC_DEF
int OTPLibraryLoad(const char *libraryName, OTPLibrary *libraryPtr, 
    char **errorInfo)
{
    void *handle = dlopen(libraryName, RTLD_LAZY);
    if (handle == NULL) {
	SET_SYSTEM_CALL_FAILURE_2("dlopen", dlerror());
	return -1;
    } else {
	*libraryPtr = handle;
	return 0;
    }
}

OTP_FUNC_DEF
int OTPLibraryGetAddress(OTPLibrary library, const char *symbolName, 
    void **addressPtr, char **errorInfo)
{
    void *address = dlsym(library, symbolName);
    if (address == NULL) {
	SET_SYSTEM_CALL_FAILURE_2("dlsym", dlerror());
	return -1;
    } else {
	*addressPtr = address;
	return 0;
    }
}

/*--------------------------------------------------------------------------*/
#endif                              /* End of platform specific definitions */
/*--------------------------------------------------------------------------*/
