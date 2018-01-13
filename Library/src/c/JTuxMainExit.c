/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "JTuxMainExit.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "JTuxMainExit.h"

#include "OTPUserlog.h"
#include "OTPError.h"

#include <atmi.h>

#include <string.h>
#include <stdlib.h>
#include <assert.h>

#ifdef _WIN32

#include <windows.h>

#define PATH_MAX 4096

static char FS = '\\';

static int getExePath(char *exe /* ignored */, char *buf, size_t size,
   char **errorInfo)
{
   if (GetModuleFileName(NULL, buf, size) == 0) {
	SET_SYSTEM_CALL_FAILURE("GetModuleFileName", GetLastError());
	return -1;
   }
   return 0;
}

#else /* Unix */

#include <sys/types.h>
#include <sys/stat.h>
#include <limits.h>
#include <unistd.h>
#include <errno.h>

static char FS = '/';

static int getExePath(char *exe, char *buf, size_t size, char **errorInfo)
{
    if (exe[0] == '/') {
	strncpy(buf, exe, size);
	return 0;
    } else if (strchr(exe, '/') != NULL) {
	if (getcwd(buf, size) == NULL) {
	    SET_SYSTEM_CALL_FAILURE("getcwd", errno);
	    return -1;
	} else {
	    int dirLen = strlen(buf);
	    assert((dirLen > 0) && (dirLen < size));
	    if (buf[dirLen - 1] != '/') {
	        buf[dirLen] = '/';
	        dirLen++;
	    }
	    strncpy(buf + dirLen, exe, size - dirLen);
	    return 0;
	}
    } else {
	char *path = tuxgetenv("PATH");
	if (path == NULL) {
	    SET_ERROR("Environment variable PATH not set");
	    return -1;
	} else {
	    char *dir = path;
	    while (1) {
	        struct stat exeInfo;
	        char *colon = strchr(dir, ':');
	        int dirLen = (colon == NULL) ? strlen(dir) : colon - dir;
	        if (dirLen > 0) {
	            assert(dirLen < size);
		    strncpy(buf, dir, dirLen);
		    if (buf[dirLen - 1] != '/') {
		        buf[dirLen] = '/';
		        dirLen++;
		    }
	            strncpy(buf + dirLen, exe, size - dirLen);
	            if (stat(buf, &exeInfo) == 0) {
		        return 0;
	            }
	        }
	        if (colon == NULL) {
		    OTPErrorSet(errorInfo, "%s not found in PATH (%s)",
		        exe, path);
		    return -1;
	        }
	        dir = colon + 1;
	    }
	}
    }
}

#endif

static char envJTuxDir[8 + PATH_MAX + 1] = "JTUXDIR=";

static char *jtuxDir = envJTuxDir + 8;

static int getJTuxDir(char *exe, char **errorInfo)
{
    char buf[PATH_MAX];

    if (getExePath(exe, buf, sizeof(buf), errorInfo) == - 1) {
	return -1;
    } else { /* buf should be "<JTUXDIR>/bin/<executable>" */
	char *slash = strrchr(buf, FS);
	if (slash == NULL) {
	    OTPErrorSet(errorInfo, "Missing '%c' in %s", FS, buf);
	    return -1;
	}
	*slash = '\0'; /* buf should now be "<JTUXDIR>/bin" */
	slash = strrchr(buf, FS);
	if (slash == NULL) {
	    OTPErrorSet(errorInfo, "Missing '%c' in %s", FS, buf);
	    return -1;
	}
	*slash = '\0'; /* buf is now "<JTUXDIR>" */
	strcpy(jtuxDir, buf);
	return 0;
   }
}

static int readJTuxEnv(char **errorInfo)
{
    char buf[PATH_MAX];

    int dirLen = strlen(jtuxDir);

    assert(dirLen < PATH_MAX);

    strcpy(buf, jtuxDir);

#ifdef _WIN32
    strncpy(buf + dirLen, "\\JTux.env", PATH_MAX - dirLen);
#else
    strncpy(buf + dirLen, "/JTux.env", PATH_MAX - dirLen);
#endif

    if (tuxreadenv(buf, NULL) != 0) {
	OTPErrorSet(errorInfo, "Error reading %s", buf);
	return -1;
    }

    return 0;
}

OTP_FUNC_DEF
void JTuxMainExit(int argc, char *const argv[])
{
    char *err = NULL;

    if (tuxgetenv("JTUX_ENV_READ") != NULL) {
	return;
    }

    if (getJTuxDir(argv[0], &err) == -1) {
	OTPUserlog("ERROR: %s", err);
	free(err);
	exit(1);
    }

    tuxputenv(envJTuxDir);

    if (readJTuxEnv(&err) == -1) {
	OTPUserlog("ERROR: %s", err);
	free(err);
	exit(1);
    }

    tuxputenv("JTUX_ENV_READ=Y");

#ifndef _WIN32
    if (execvp(argv[0], argv) == -1) {
        OTPUserlog("ERROR: execvp() failed with errno %d", errno);
	exit(1);
    }
#endif
}
