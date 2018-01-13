/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPUunix_h_included
#define OTPUunix_h_included

#ifdef _WIN32

/*
 * The Tuxedo include file Uunix.h and the Windows include file WinNT.h
 * both define the symbols MINSHORT, MAXSHORT, MINLONG and MAXLONG. 
 * The definitions in Uunix.h file are protected against redefinition but
 * the ones in WinNT.h are not. Including WinNT.h after Uunix.h will
 * therefore lead to a macro redefinition causing the compilation to 
 * fail. To prevent this, source files can include OTPUunix.h rather
 * than Uunix.h as this will prevent this failure from happening by
 * including WinNT.h (or in this case windows.h) before Uunix.h.
 */

#include <windows.h>

#endif

#include <Uunix.h>

#endif /* OTPUunix_h_included */
