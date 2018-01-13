/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPInt_h_included
#define OTPInt_h_included

#ifdef _WIN32

#include "OTPWindows.h"

typedef SHORT int16_t;
typedef INT32 int32_t;
typedef INT64 int64_t;

#else

#include <stdint.h>

#endif

#endif
