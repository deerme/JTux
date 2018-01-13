/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPString.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPString.h"
#include "OTPError.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <errno.h>

int OTPStringIndexOf(const char *str, int len, char ch)
{
    int i = 0;
    while (i < len) {
	if (str[i] == ch) {
	    return i;
	}
	i++;
    }
    return -1;
}

OTP_FUNC_DEF
char *OTPStringLocateWhitespace(const char *str)
{
    while (*str != '\0') {
	if (isspace(*str)) {
	    return (char *) str;
	}
	str++;
    }
    return NULL;
}

OTP_FUNC_DEF
char *OTPStringLocateNonWhitespace(const char *str)
{
    while (*str != '\0') {
	if (!isspace(*str)) {
	    return (char *) str;
	}
	str++;
    }
    return NULL;
}

OTP_FUNC_DEF
int OTPStringStartsWith(const char *str, int len, const char* prefix)
{
    int prefixLen = strlen(prefix);
    if (prefixLen > len) {
	return 0;
    } else {
	return (strncmp(str, prefix, prefixLen) == 0);
    }
}

OTP_FUNC_DEF
char *OTPStringDuplicate(const char *str, char **errorInfo)
{
    return OTPStringDuplicateBounded(str, strlen(str), errorInfo);
}

OTP_FUNC_DEF
char *OTPStringDuplicateBounded(const char *str, int len, char **errorInfo)
{
    char *dup = (char *) malloc(len + 1);
    if (dup == NULL) {
	SET_MALLOC_FAILURE(len + 1);
	return NULL;
    }
    memcpy(dup, str, len);
    dup[len] = '\0';
    return dup;
}

static int getTotalFragmentLength(OTPStringFragment *fragments)
{
    int len = 0;
    OTPStringFragment *fragment = fragments;
    while (fragment->str != NULL) {
	if (fragment->len == -1) {
	    fragment->len = strlen(fragment->str);
	}
	len = len + fragment->len;
	fragment++;
    }
    return len;
}

OTP_FUNC_DEF
char *OTPStringConcat(OTPStringFragment *fragments, char **errorInfo) 
{
    int len = getTotalFragmentLength(fragments);
    char *str = (char *) malloc(len + 1);
    if (str == NULL) {
	SET_MALLOC_FAILURE(len + 1);
	return NULL;
    } else {
        int i = 0;
        OTPStringFragment *fragment = fragments;
        while (fragment->str != NULL) {
	    memcpy(str + i, fragment->str, fragment->len);
	    i = i + fragment->len;
	    fragment++;
        }
	str[i] = '\0';
	return str;
    }
}

OTP_FUNC_DEF
int OTPStringFragmentize(const char *str, char separator,
    OTPStringFragment *fragments, int maxFragments)
{
    int i = 0;
    char *sep = strchr(str, separator);
    while ((sep != NULL) && (i < maxFragments)) {
	fragments[i].str = str;
	fragments[i].len = sep - str;

	str = sep + 1;
        sep = strchr(str, separator);
	i++;
    }
    fragments[i].str = str;
    fragments[i].len = strlen(str);
    return i + 1;
}

int getDecimalCharacterValue(char ch)
{
    switch (ch) {
	case '0': return 0; case '1': return 1;
	case '2': return 2; case '3': return 3;
	case '4': return 4; case '5': return 5;
	case '6': return 6; case '7': return 7;
	case '8': return 8; case '9': return 9;
    }
    return -1;
}

OTP_FUNC_DEF
int OTPStringParseDecimal(const char *str, int len, int *value, 
    char **errorInfo)
{
    if (len == 0) {
	SET_ERROR("Missing value");
	return -1;
    } else {
	int result = 0;
	int i = 0;
        while (i < len) {
	    int val = getDecimalCharacterValue(str[i]);
	    if (val == -1) {
		char buf[512];
		sprintf(buf, "Invalid decimal character '%c'", str[i]);
		SET_ERROR(buf);
		return -1;
	    }
	    result = 10 * result + val;
	    i++;
	}
	*value = result;
	return 0;
    }
}

OTP_FUNC_DEF
int OTPStringParseSignedDecimal(const char *str, int len, int *value,
    char **errorInfo)
{
    if ((len > 0) && (str[0] == '-')) {
	int unsignedValue;
	if (OTPStringParseDecimal(str + 1, len - 1, &unsignedValue, 
		errorInfo) == -1) {
	    return -1;
	}
	*value = -unsignedValue;
	return 0;
    } else if ((len >0) && (str[0] == '+')) {
	return OTPStringParseDecimal(str + 1, len - 1, value, errorInfo);
    } else {
	return OTPStringParseDecimal(str, len, value, errorInfo);
    }
}

static int getHexadecimalCharacterValue(char ch)
{
    switch (ch) {
	case '0': return 0; case '1': return 1;
       	case '2': return 2; case '3': return 3;
       	case '4': return 4; case '5': return 5;
	case '6': return 6; case '7': return 7;
       	case '8': return 8; case '9': return 9;
       	case 'a': /* fall through */ case 'A': return 10;
	case 'b': /* fall through */ case 'B': return 11;
       	case 'c': /* fall through */ case 'C': return 12;
       	case 'd': /* fall through */ case 'D': return 13;
	case 'e': /* fall through */ case 'E': return 14;
       	case 'f': /* fall through */ case 'F': return 15;
    }
    return -1;
}

OTP_FUNC_DEF
int OTPStringParseBytes(const char *str, unsigned char *bytes, int len,
    char **errorInfo)
{
    int i = 0;
    while (i < len) {
	const char *chPair = str + (2 * i);
	if ((chPair[0] == '\0') || (chPair[1] == '\0')) {
	    SET_ERROR("Unexpected end of hex string");
	    return -1;
	} else {
	    int hi = getHexadecimalCharacterValue(chPair[0]);
	    int lo = getHexadecimalCharacterValue(chPair[1]);
	    if ((hi == -1) || (lo == -1)) {
		SET_ERROR("Invalid hex string");
		return -1;
	    }
	    bytes[i] = (unsigned char) (16 * hi) + lo;
	    i++;
	}
    }
    return 0;
}

#ifdef _WIN32
#define NULL_FILE_NAME "NUL"
#else
#define NULL_FILE_NAME "/dev/null"
#endif

OTP_FUNC_DEF
int OTPStringGetFormattedOutputLength(const char *format, va_list args,
    char **errorInfo)
{
    if (strchr(format, '%') == NULL) {
	return strlen(format);
    } else {
        FILE *nullFile = fopen(NULL_FILE_NAME, "w");
        if (nullFile != NULL) {
	    int result = vfprintf(nullFile, format, args);
	    if (result < 0) {
	        SET_SYSTEM_CALL_FAILURE("vfprintf", errno);
	        fclose(nullFile);
	        return -1;
	    } else {
	        fclose(nullFile);
	        return result;
	    }
        } else {
	    SET_SYSTEM_CALL_FAILURE("fopen", errno);
	    return -1;
        }
    }
}

OTP_FUNC_DEF
char *OTPStringCreate(char **errorInfo, const char *format, ...)
{
#ifdef ACC_VA_LIST_430_BUG /* See Makefile.HP-UX */
    va_list args = 0;
#else
    va_list args;
#endif

    int len;
    char *str;

    va_start(args, format);
    len = OTPStringGetFormattedOutputLength(format, args, errorInfo);
    va_end(args);

    if (len == -1) {
	return NULL;
    }

    str = (char *) malloc(len + 1);
    if (str == NULL) {
	SET_MALLOC_FAILURE(len + 1);
	return NULL;
    }

    va_start(args, format);

    if (vsprintf(str, format, args) < 0) {
	SET_SYSTEM_CALL_FAILURE("vsprintf", errno);
	free(str); /* Must be done *after* using errno in the previous line */
	str = NULL;
    }

    va_end(args);

    return str;
}
