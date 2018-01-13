/* 
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPProcess.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPProcess.h"
#include "OTPString.h"
#include "OTPError.h"

#include <stdlib.h>

/*--------------------------------------------------------------------------*/
#ifdef _WIN32                            /* Windows specific implementation */
/*--------------------------------------------------------------------------*/

#include "OTPWindows.h" /* Don't include windows.h directly */

static char *createCommandLine(char *prog, int argc, char *argv[],
    char **errorInfo)
{
    /*
     * The command line consists of the following fragments
     *
     * '"' <prog> '" "' <arg0> '" "' ... '" "' <argN> '"'
     *  |     |     |     |      |   ...   |      |    |
     *
     * So the total number of fragments = 3 + 2 * argc plus one extra
     * for the terminator.
     */

    int count = 3 + (2 * argc) + 1;

    OTPStringFragment *fragments = (OTPStringFragment *)
	malloc(count * sizeof(OTPStringFragment));

    if (fragments == NULL) {
	SET_MALLOC_FAILURE(count * sizeof(OTPStringFragment));
	return NULL;
    } else {
	char *result;

        OTPStringFragment *fragment = fragments;

        fragment->str = "\"";
        fragment->len = 1;
        fragment++;

        fragment->str = prog;
        fragment->len = strlen(prog);
        fragment++;

        for (count = 0; count < argc; count++) {

	    fragment->str = "\" \"";
	    fragment->len = 3;
	    fragment++;

	    fragment->str = argv[count];
	    fragment->len = strlen(argv[count]);
	    fragment++;
        }

        fragment->str = "\"";
        fragment->len = 1;
        fragment++;

        fragment->str = NULL; /* terminator */

        result = OTPStringConcat(fragments, errorInfo);

        free(fragments);

	if (result == NULL) {
	    ADD_ERROR_CONTEXT("Failed to create command line");
	    return NULL;
	} else {
            return result;
	}
    }
}

/*
 * See MSDN Library
 *   /Platform SDK
 *     /DLLs, Processes, and Threads
 *       /CreateProcess
 *     /Interprocess Communications
 *       /CreatePipe
 *       /Pipe Handle Inheritance
 *
 * See also Microsoft Knowledge Base Article - 190351
 * "HOWTO: Spawn Console Processes with Redirected Standard Handles".
 */

static int makeInheritable(HANDLE *handle, char **errorInfo)
{
    HANDLE currentProcess = GetCurrentProcess(); /* never fails */
    HANDLE oldHandle = *handle;
    HANDLE newHandle;

    if (!DuplicateHandle(currentProcess, oldHandle, currentProcess,
	    &newHandle, 0, TRUE, DUPLICATE_SAME_ACCESS)) {
	SET_SYSTEM_CALL_FAILURE("DuplicateHandle", GetLastError());
	return -1;
    } else {
	CloseHandle(oldHandle);
	*handle = newHandle;
	return 0;
    }
}

OTP_FUNC_DEF
int OTPProcessCreate(char *prog, int argc, char *argv[],
    OTPProcess *processPtr, OTPPipe *stdinHandlePtr, 
    OTPPipe *stdoutHandlePtr, char **errorInfo)
{
    char *commandLine;

    HANDLE stdinReadHandle;
    HANDLE stdinWriteHandle;

    HANDLE stdoutReadHandle;
    HANDLE stdoutWriteHandle;

    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    /* Create command line */

    commandLine = createCommandLine(prog, argc, argv, errorInfo);
    if (commandLine == NULL) {
	goto error;
    }

    /* Create stdin pipe */

    if (!CreatePipe(&stdinReadHandle, &stdinWriteHandle, NULL, 0)) {
	SET_SYSTEM_CALL_FAILURE("CreatePipe", GetLastError());
	goto error_after_commandLine;
    }

    if (makeInheritable(&stdinReadHandle, errorInfo) == -1) {
	ADD_ERROR_CONTEXT("Failed to make stdin read handle inheritable");
	goto error_after_stdin_pipe;
    }

    /* Create stdout pipe */

    if (!CreatePipe(&stdoutReadHandle, &stdoutWriteHandle, NULL, 0)) {
	SET_SYSTEM_CALL_FAILURE("CreatePipe", GetLastError());
	goto error_after_stdin_pipe;
    }

    if (makeInheritable(&stdoutWriteHandle, errorInfo) == -1) {
	ADD_ERROR_CONTEXT("Failed to make stdout write handle inheritable");
	goto error_after_stdout_pipe;
    }

    ZeroMemory(&si, sizeof(STARTUPINFO));
    si.cb = sizeof(STARTUPINFO);
    si.dwFlags = STARTF_USESTDHANDLES;
    si.hStdInput = stdinReadHandle;
    si.hStdOutput = stdoutWriteHandle;
    si.hStdError = GetStdHandle(STD_ERROR_HANDLE);

    ZeroMemory(&pi, sizeof(PROCESS_INFORMATION));

    if (!CreateProcess(NULL, commandLine, NULL, NULL, TRUE,
	    CREATE_NO_WINDOW, NULL, NULL, &si, &pi)) {
	int errorCode = GetLastError();
	if (errorCode == ERROR_FILE_NOT_FOUND) {
	    OTPErrorSet(errorInfo, "File '%s' not found", prog);
	} else if (errorCode == ERROR_BAD_EXE_FORMAT) {
	    OTPErrorSet(errorInfo, "%s is not executable", prog);
	} else {
	    SET_SYSTEM_CALL_FAILURE("CreateProcess", GetLastError());
	}
	goto error_after_stdout_pipe;
    }

    CloseHandle(pi.hThread);

    /*
     * Close the handles passed to client. If you don't do this then
     * the pipes will not break when the child dies because this process
     * still has handles on them.
     */

    CloseHandle(stdoutWriteHandle);
    CloseHandle(stdinReadHandle);

    free(commandLine);

    *processPtr = pi.hProcess;

    *stdinHandlePtr = stdinWriteHandle;
    *stdoutHandlePtr = stdoutReadHandle;

    return 0;

error_after_stdout_pipe:

    CloseHandle(stdoutWriteHandle);
    CloseHandle(stdoutReadHandle);

error_after_stdin_pipe:

    CloseHandle(stdinWriteHandle);
    CloseHandle(stdinReadHandle);

error_after_commandLine:

    free(commandLine);

error:

    return -1;
}

OTP_FUNC_DEF
int OTPProcessWait(OTPProcess process, int *exitCodePtr, char **errorInfo)
{
    DWORD result = WaitForSingleObject(process, INFINITE);
    if (result == WAIT_OBJECT_0) {
	DWORD exitCode;
	if (GetExitCodeProcess(process, &exitCode)) {
	    *exitCodePtr = exitCode;
	    CloseHandle(process);
	    return 0;
	} else {
	    SET_SYSTEM_CALL_FAILURE("GetExitCodeProcess", GetLastError());
	    return -1;
	}
    } else {
       	if (result == WAIT_FAILED) {
	    SET_SYSTEM_CALL_FAILURE("WaitForSingleObject", GetLastError());
        } else {
	    SET_ERROR("WaitForSingleObject(process, INFINITE) succeeded "
	        "for reason other than process termination");
	}
        return -1;
    }
}

/*--------------------------------------------------------------------------*/
#else                                       /* Unix specific implementation */
/*--------------------------------------------------------------------------*/

/*
 * See Unix System Programming for SVR4, David A. Curry,
 * Chapter 11 "Processes" and Chapter 13 "Interprocess Communication"
 */

#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <errno.h>

static char **createCommandLine(char *prog, int argc, char *argv[],
    char **errorInfo)
{
    char **commandLine = (char **) malloc((argc + 2) * sizeof(char *));
    if (commandLine == NULL) {
	SET_MALLOC_FAILURE((argc + 2) * sizeof(char *));
	return NULL;
    } else {
        int i;
        commandLine[0] = prog;
        for (i = 0; i < argc; i++) {
	    commandLine[i + 1] = argv[i];
        }
        commandLine[argc + 1] = NULL;
        return commandLine;
    }
}

static int attach(int fd, int fdToAttach, int fdToClose, char **errorInfo)
{
    if (dup2(fdToAttach, fd) == -1) {
	SET_SYSTEM_CALL_FAILURE("dup2", errno);
	return -1;
    } else {
        close(fdToClose);
        return 0;
    }
}

OTP_FUNC_DEF
int OTPProcessCreate(char *prog, int argc, char *argv[],
    OTPProcess *processPtr, OTPPipe *stdinHandlePtr, 
    OTPPipe *stdoutHandlePtr, char **errorInfo)
{
    char **commandLine;

    pid_t pid;

    int stdinPipe[2];
    int stdoutPipe[2];

    commandLine = createCommandLine(prog, argc, argv, errorInfo);
    if (commandLine == NULL) {
	goto error;
    }

    if (pipe(stdinPipe) == -1) {
	SET_SYSTEM_CALL_FAILURE("pipe", errno);
	goto error_after_commandLine;
    }

    if (pipe(stdoutPipe) == -1) {
	SET_SYSTEM_CALL_FAILURE("pipe", errno);
	goto error_after_stdin;
    }

    pid = fork();
    if (pid == (pid_t) -1) {
	SET_SYSTEM_CALL_FAILURE("fork", errno);
	goto error_after_stdout;
    }

    if (pid == 0) { /* We are in the child process */

	if (attach(STDIN_FILENO, stdinPipe[0], stdinPipe[1], 
		errorInfo) == -1) {
	    ADD_ERROR_CONTEXT("Failed to attach child stdin to pipe");
	    goto error_after_stdout;
	}

	if (attach(STDOUT_FILENO, stdoutPipe[1], stdoutPipe[0],
	        errorInfo) == -1) {
	    ADD_ERROR_CONTEXT("Failed to attach child stdout to pipe");
	    goto error_after_stdout;
	}

	if (execvp(prog, commandLine) == -1) {
	    if (errno == ENOENT) {
	        OTPErrorSet(errorInfo, "File '%s' not found", prog);
	    } else {
		/* Note that we don't need to check for ENOEXEC as execvp()
		 * does not raise this error (see Single Unix Specification)
		 */
	        SET_SYSTEM_CALL_FAILURE("execv", errno);
	    }
	    goto error_after_stdout;
	}

	/* not reached, execvp() does not return on success */
    }
    
    /* We are in the parent process */

    close(stdinPipe[0]);
    close(stdoutPipe[1]);

    *processPtr = pid;

    *stdinHandlePtr = stdinPipe[1];
    *stdoutHandlePtr = stdoutPipe[0];

    return 0;
    
error_after_stdout:

    close(stdoutPipe[0]);
    close(stdoutPipe[1]);

error_after_stdin:

    close(stdinPipe[0]);
    close(stdinPipe[1]);

error_after_commandLine:

    free(commandLine);

error:

    return -1;
}

OTP_FUNC_DEF
int OTPProcessWait(OTPProcess process, int *exitCodePtr, char **errorInfo)
{
    int exitCode;
    if (waitpid(process, &exitCode, 0) == -1) {
        SET_SYSTEM_CALL_FAILURE("waitpid", errno);
	return -1;
    } else {
	*exitCodePtr = exitCode;
	return 0;
    }
}

/*--------------------------------------------------------------------------*/
#endif                          /* End of platform specific implementations */
/*--------------------------------------------------------------------------*/
