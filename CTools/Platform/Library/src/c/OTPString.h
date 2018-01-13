/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPString_h_included
#define OTPString_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include <stdarg.h> /* va_list */

typedef struct {
    const char *str;
    int len;
} OTPStringFragment;

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
int OTPStringIndexOf(const char *str, int len, char ch);

OTP_FUNC_DECL
char *OTPStringLocateWhitespace(const char *str);

OTP_FUNC_DECL
char *OTPStringLocateNonWhitespace(const char *str);

OTP_FUNC_DECL
int OTPStringStartsWith(const char *str, int len, const char *prefix);

OTP_FUNC_DECL
char *OTPStringDuplicate(const char *str, char **errorInfo);

OTP_FUNC_DECL
char *OTPStringDuplicateBounded(const char *str, int len, char **errorInfo);

OTP_FUNC_DECL
char *OTPStringConcat(OTPStringFragment *fragments, char **errorInfo);

OTP_FUNC_DECL
int OTPStringFragmentize(const char *str, char separator, 
    OTPStringFragment *fragments, int maxFragments);

OTP_FUNC_DECL
int OTPStringParseDecimal(const char *str, int len, int *value, 
    char **errorInfo);

OTP_FUNC_DECL
int OTPStringParseSignedDecimal(const char *str, int len, int *value, 
    char **errorInfo);

OTP_FUNC_DECL
int OTPStringParseBytes(const char *str, unsigned char *bytes, int len, 
    char **errorInfo);

OTP_FUNC_DECL
int OTPStringGetFormattedOutputLength(const char *format, va_list args,
    char **errorInfo);

OTP_FUNC_DECL
char *OTPStringCreate(char **errorInfo, const char *format, ...);

#ifdef __cplusplus
}
#endif

#endif
