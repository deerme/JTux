/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPWindows_h_included
#define OTPWindows_h_included

/*
 * This file must be used to include windows.h rather than including
 * windows.h directly.
 */

#define WIN32_LEAN_AND_MEAN

/*
 * The WIN32_LEAN_AND_MEAN macro must be defined to prevent windows.h
 * from including winsock.h. Without this, winsock2.h cannot be used
 * as it conflicts with winsock.h.
 */

#include <windows.h>

#endif 
