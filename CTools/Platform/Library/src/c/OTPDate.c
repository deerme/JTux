/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPDate.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPDate.h"
#include "OTPString.h"
#include "OTPError.h"

#include <time.h>

OTP_FUNC_DEF
void OTPDateNow(OTPDate *date)
{
    time_t now = time(NULL);

    struct tm *localTime = localtime(&now);

    date->year = localTime->tm_year + 1900;
    date->month = localTime->tm_mon + 1;
    date->day = localTime->tm_mday;
}

static int isLeapYear(int year)
{
    if (year % 4 == 0) {
	if (year % 100 == 0) {
	    return (year % 400 == 0);
	} else {
	    return 1;
	}
    } else {
	return 0;
    }
}

static int getDaysInYear(int year)
{
    return isLeapYear(year) ? 366 : 365;
}

static int getDaysInMonth(int year, int month)
{
    switch (month) {
	case 2: return isLeapYear(year) ? 29 : 28;
	case 4: case 6: case 9: case 11: return 30;
	default: return 31;
    }
}

OTP_FUNC_DEF
int OTPDateParse(const char *str, size_t len, OTPDate *date, char **errorInfo)
{
    /*
     * Use temporary variables for year, month and day so that date is
     * not modified if parsing fails.
     */

    int year;
    int month;
    int day;

    int daysInMonth;

    if ((len != 10) || (str[4] != '-') || (str[7] != '-')) {
	SET_ERROR("Date not specified in YYYY-MM-DD format");
	return -1;
    }

    if (OTPStringParseDecimal(str, 4, &year, errorInfo) == -1) {
	ADD_ERROR_CONTEXT("Error parsing year");
	return -1;
    }

    if (year <= 0) {
	SET_ERROR("Year must be 1 or higher");
	return -1;
    }

    if (OTPStringParseDecimal(str + 5, 2, &month, errorInfo) == -1) {
	ADD_ERROR_CONTEXT("Error parsing month");
	return -1;
    }

    if ((month < 1) || (month > 12)) {
	SET_ERROR("Month must be between 1 and 12");
	return -1;
    }

    if (OTPStringParseDecimal(str + 8, 2, &day, errorInfo) == -1) {
	ADD_ERROR_CONTEXT("Error parsing day");
	return -1;
    }

    daysInMonth = getDaysInMonth(year, month);
    if ((day < 1) || (day > daysInMonth)) {
	char buf[256];
	sprintf(buf, "Day must be between 1 and %d", daysInMonth);
	SET_ERROR(buf);
	return -1;
    }

    date->year = year;
    date->month = month;
    date->day = day;

    return 0;
}

static int getDaysToEndOfMonth(OTPDate *date)
{
    return getDaysInMonth(date->year, date->month) - date->day;
}

static int getDaysToEndOfYear(OTPDate *date)
{
    int days = getDaysToEndOfMonth(date);

    int month = date->month + 1;
    while (month <= 12) {
	days = days + getDaysInMonth(date->year, month);
	month++;
    }

    return days;
}

static int getDaysFromStartOfMonth(OTPDate *date)
{
    return date->day;
}

static int getDaysFromStartOfYear(OTPDate *date)
{
    int days = 0;

    int month = 1;
    while (month < date->month) {
	days = days + getDaysInMonth(date->year, month);
	month++;
    }

    return days + getDaysFromStartOfMonth(date);
}

OTP_FUNC_DEF
int OTPDateGetDaysBetween(OTPDate *from, OTPDate *to)
{
    if (from->year < to->year) {
	int days = getDaysToEndOfYear(from);
	int year = from->year + 1;
	while (year < to->year) {
	    days = days + getDaysInYear(year);
	    year++;
	}
	return days + getDaysFromStartOfYear(to);
    } else if (to->year < from->year) {
	return -OTPDateGetDaysBetween(to, from);
    } else { /* from->year == to->year */
	if (from->month < to->month) {
            int days = getDaysToEndOfMonth(from);
            int month = from->month + 1;
            while (month < to->month) {
	        days = days + getDaysInMonth(from->year, month);
	        month++;
	    }
	    return days + getDaysFromStartOfMonth(to);
	} else if (to->month < from->month) {
	    return -OTPDateGetDaysBetween(to, from);
	} else { /* from->month == to->month */ 
	    return to->day - from->day;
	}
    }
}

OTP_FUNC_DEF
void OTPDateAdvance(OTPDate *date, int days)
{
    int count;

    count = getDaysToEndOfYear(date);
    if (days > count) {
	date->year++;
	date->month = 1;
	date->day = 0;
	days -= count;
	count = getDaysInYear(date->year);
	while (days > count) {
	    date->year++;
	    days -= count;
	    count = getDaysInYear(date->year);
	}
    }

    count = getDaysToEndOfMonth(date);
    if (days > count) {
	date->month++;
	date->day = 0;
	days -= count;
	count = getDaysInMonth(date->year, date->month);
	while (days > count) {
	    date->month++;
	    days -= count;
	    count = getDaysInMonth(date->year, date->month);
	}
    }

    date->day += days;
}
