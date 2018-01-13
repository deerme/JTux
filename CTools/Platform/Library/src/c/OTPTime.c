/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPTime.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPTime.h"
#include "OTPString.h"
#include "OTPError.h"

#include <ctype.h>

OTP_FUNC_DEF
int OTPTimeParseDuration(const char *str, int len, int *seconds, 
    char **errorInfo)
{
    if (len == 0) {
	SET_ERROR("Missing value");
	return -1;
    } else {
	int num;
	char unit = tolower(str[len-1]);
	if ((unit == 'h') || (unit == 'm') || (unit == 's'))  {
	    len--;
	}
	if (OTPStringParseDecimal(str, len, &num, errorInfo) == -1) {
	    return -1;
	}
	if (unit == 'h') {
	    *seconds = 3600 * num;
	} else if (unit == 'm') {
	    *seconds = 60 * num;
	} else {
	    *seconds = num;
	}
	return 0;
    }
}
