/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef JTux_h_included
#define JTux_h_included

#define XSTR(S) STR(S)
#define STR(S) #S

#ifdef JTUX_VERSION
#error Do not define JTUX_VERSION, use JTUX_RELEASE and JTUX_PATCH_LEVEL
#endif

#ifndef JTUX_RELEASE
#error JTUX_RELEASE not defined
#endif

#ifndef JTUX_PATCH_LEVEL
#error JTUX_PATCH_LEVEL not defined
#endif

#ifndef TUX_VERSION
#error TUX_VERSION not defined
#endif

#define JTUX_VERSION_STR XSTR(JTUX_RELEASE) "." XSTR(JTUX_PATCH_LEVEL)

#define TUX_VERSION_STR XSTR(TUX_VERSION)

#endif
