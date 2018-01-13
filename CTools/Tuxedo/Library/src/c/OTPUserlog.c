/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPUserlog.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include <userlog.h> /* for userlog() */
#include <stdio.h> /* for BUFSIZ */
#include <stdlib.h> /* for malloc() */
#include <string.h> /* for memcpy() */
#include <stdarg.h> /* for va_list, etc */
#include <assert.h> /* for assert() */

#include "OTPString.h"

/*
 * According to the Tuxedo documentation, userlog() crashes if the
 * message to log is longer than BUFSIZ characters. On Linux, however,
 * where BUFSIZ=8192, userlog() already crashes when the message length
 * is around 4200. So, to be safe, we cap the maximum line length to
 * the maximum of BUFSIZ and 4096.
 */

#define MAX_LINE_LEN ((BUFSIZ > 4096) ? 4096 : BUFSIZ)

static void userlogLongLine(const char* str, int len)
{
    char buf[MAX_LINE_LEN];

    while (len > 0) {
	int chunkLen = (len < MAX_LINE_LEN - 1) ? len : MAX_LINE_LEN - 1;
	memcpy(buf, str, chunkLen);
	buf[chunkLen] = '\0';
	/* buf may contain % => don't use userlog(buf) */
        userlog("%s", buf);
	str = str + chunkLen;
	len = len - chunkLen;
    }
}

static const char *getEndOfLine(const char *str, int len)
{
    int i = 0;
    while (i < len) {
	if (str[i] == '\n') {
	    return str + i;
	}
	i++;
    }
    return NULL;
}

static void userlogLongLines(const char *str, int len)
{
    const char *line = str;
    while (len > 0) {
	const char *endOfLine = getEndOfLine(line, len);
	if (endOfLine == NULL) {
	    userlogLongLine(line, len);
	    break;
	} else {
	    int lineLength = endOfLine - line;
	    if ((lineLength > 0) && (line[lineLength - 1] == '\r')) {
		userlogLongLine(line, lineLength - 1);
	    } else {
		userlogLongLine(line, lineLength);
	    }
	    line = endOfLine + 1;
	    len = len - lineLength - 1;
	}
    }
}

OTP_FUNC_DEF
void OTPUserlog(const char *format, ...)
{
#ifdef ACC_VA_LIST_430_BUG /* See Makefile.HP-UX */
    va_list args = 0;
#else
    va_list args;
#endif

    int len;
    char *str;

    va_start(args, format);
    len = OTPStringGetFormattedOutputLength(format, args, NULL);
    va_end(args);

    if (len < 4096) {

	char buf[4096];

        va_start(args, format);
        vsprintf(buf, format, args);
        va_end(args);

        userlogLongLines(buf, len);

    } else {

        char *str = (char *) malloc(len + 1);
        assert(str != NULL);

        va_start(args, format);
        vsprintf(str, format, args);
        va_end(args);

        userlogLongLines(str, len);

	free(str);
    }
}
