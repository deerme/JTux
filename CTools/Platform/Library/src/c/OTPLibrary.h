/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPLibrary_h_included
#define OTPLibrary_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

/*--------------------------------------------------------------------------*/
#ifdef _WIN32                              /* Windows specific declarations */
/*--------------------------------------------------------------------------*/

#include "OTPWindows.h" /* Don't include windows.h directly */

typedef HMODULE OTPLibrary;

/*--------------------------------------------------------------------------*/
#else                                         /* Unix specific declarations */
/*--------------------------------------------------------------------------*/

#include <dlfcn.h>

typedef void *OTPLibrary;

/*--------------------------------------------------------------------------*/
#endif                             /* End of platform specific declarations */
/*--------------------------------------------------------------------------*/

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
int OTPLibraryLoad(const char *libraryName, OTPLibrary *libraryPtr, 
    char **errorInfo);

OTP_FUNC_DECL
int OTPLibraryGetAddress(OTPLibrary library, const char *symbolName, 
    void **addressPtr, char **errorInfo);

#ifdef __cplusplus
}
#endif

#endif
