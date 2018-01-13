/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPDate_h_included
#define OTPDate_h_included

#include <stddef.h> /* size_t */

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

typedef struct {
    int year;
    int month;
    int day;
} OTPDate;

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
void OTPDateNow(OTPDate *date);

OTP_FUNC_DECL
int OTPDateParse(const char *str, size_t len, OTPDate *date, char **errorInfo);

/*
 * The functions below must be called with valid dates or the behaviour
 * will be undefined.
 */

OTP_FUNC_DECL
int OTPDateGetDaysBetween(OTPDate *from, OTPDate *to);

OTP_FUNC_DECL
void OTPDateAdvance(OTPDate *date, int days);

#ifdef __cplusplus
}
#endif

#endif
