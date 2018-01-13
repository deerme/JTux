/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPThread_h_included
#define OTPThread_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

/*
 * Implementation notes.
 *
 * - A thread exit code is an integer on Windows and a void* on Unix.
 *   These two types are hard to unify, that's why we decided not to
 *   support thread exit codes. As a result, the OTPThreadWait()
 *   function does not have an exit code parameter.
 */

typedef void (*OTPThreadFunc)(void *arg);

/*--------------------------------------------------------------------------*/
#ifdef _WIN32                               /* Windows specific definitions */
/*--------------------------------------------------------------------------*/

#include "OTPWindows.h" /* Don't include windows.h directly */

typedef HANDLE OTPThread;

typedef HANDLE OTPMutex;

typedef DWORD OTPThreadVar;

typedef HANDLE OTPEvent;

/*--------------------------------------------------------------------------*/
#else                                          /* Unix specific definitions */
/*--------------------------------------------------------------------------*/

#include <pthread.h>
#include <errno.h>

typedef pthread_t OTPThread;

typedef pthread_mutex_t *OTPMutex;

typedef pthread_key_t OTPThreadVar;

typedef struct OTPEventStruct *OTPEvent;

/*--------------------------------------------------------------------------*/
#endif                                 /* End platform specific definitions */
/*--------------------------------------------------------------------------*/

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
int OTPThreadCreate(OTPThread *threadPtr, OTPThreadFunc func,
    void *arg, char **errorInfo);

/*
 * Waits for the thread to terminate and cleans up the resources associated
 * with the thread.
 * This is the only proper way to terminate and clean up a thread.
 */
OTP_FUNC_DECL
int OTPThreadWait(OTPThread thread, char **errorInfo);

/*
 * Causes the calling thread to sleep for the indicated number of seconds.
 */
OTP_FUNC_DECL
void OTPThreadSleep(unsigned int secs);

OTP_FUNC_DECL
int OTPMutexCreate(OTPMutex *mutexPtr, char **errorInfo);

OTP_FUNC_DECL
int OTPMutexDestroy(OTPMutex mutex, char **errorInfo);

OTP_FUNC_DECL
int OTPMutexAcquire(OTPMutex mutex, char **errorInfo);

OTP_FUNC_DECL
int OTPMutexRelease(OTPMutex mutex, char **errorInfo);

OTP_FUNC_DECL
int OTPThreadVarCreate(OTPThreadVar *threadVarPtr, char **errorInfo);

OTP_FUNC_DECL
int OTPThreadVarDestroy(OTPThreadVar threadVar, char **errorInfo);

OTP_FUNC_DECL
int OTPThreadVarSet(OTPThreadVar threadVar, void *value, char **errorInfo);

OTP_FUNC_DECL
int OTPThreadVarGet(OTPThreadVar threadVar, void **valuePtr, 
    char **errorInfo);

OTP_FUNC_DECL
int OTPEventCreate(OTPEvent *eventPtr, char **errorInfo);

OTP_FUNC_DECL
int OTPEventDestroy(OTPEvent event, char **errorInfo);

OTP_FUNC_DECL
int OTPEventSignal(OTPEvent event, char **errorInfo);

OTP_FUNC_DECL
int OTPEventWait(OTPEvent event, char **errorInfo);

OTP_FUNC_DECL
int OTPEventTimedWait(OTPEvent event, unsigned int msecs, int *signalled, 
    char **errorInfo);

OTP_FUNC_DECL
int OTPEventClear(OTPEvent event, char **errorInfo);

#ifdef __cplusplus
}
#endif

#endif /* OTPThread_h_included */
