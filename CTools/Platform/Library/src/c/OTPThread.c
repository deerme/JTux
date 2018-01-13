/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPThread.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPThread.h"
#include "OTPError.h"

#include <stdlib.h> /* malloc() */

typedef struct {
    OTPThreadFunc func;
    void *arg;
} OTPRunnable;

/*--------------------------------------------------------------------------*/
#ifdef _WIN32                                /* Implementations for Windows */
/*--------------------------------------------------------------------------*/

static DWORD WINAPI run(void *arg)
{
    OTPRunnable *runnable = (OTPRunnable *) arg;
    runnable->func(runnable->arg);
    free(runnable);
    return 0;
}

OTP_FUNC_DEF
int OTPThreadCreate(OTPThread *threadPtr, OTPThreadFunc func,
    void *arg, char **errorInfo)
{
    HANDLE thread;

    OTPRunnable *runnable = (OTPRunnable *) malloc(sizeof(OTPRunnable));
    if (runnable == NULL) {
	SET_MALLOC_FAILURE(sizeof(OTPRunnable));
	return -1;
    }

    runnable->func = func;
    runnable->arg = arg;

    thread = CreateThread(NULL, 0, run, runnable, 0, NULL);
    if (thread == NULL) {
	SET_SYSTEM_CALL_FAILURE("CreateThread", GetLastError());
	free(runnable);
	return -1;
    } else {
        *threadPtr = thread;
	return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadWait(OTPThread thread, char **errorInfo)
{
    DWORD result = WaitForSingleObject(thread, INFINITE);
    if (result == WAIT_OBJECT_0) {
	CloseHandle(thread);
	return 0;
    } else {
       	if (result == WAIT_FAILED) {
	    SET_SYSTEM_CALL_FAILURE("WaitForSingleObject", GetLastError());
        } else {
	    SET_ERROR("WaitForSingleObject(thread, INFINITE) succeeded "
	        "for reason other than thread termination");
	}
        return -1;
    }
}

OTP_FUNC_DEF
void OTPThreadSleep(unsigned int secs)
{
    /* Sleep() argument is a 32 bit unsigned int and is in millisecs */

    if (secs > (0xFFFFFFFF/1000)) {
	Sleep(0xFFFFFFFF); /* This is almost 500 days */
    } else {
        Sleep(secs * 1000);
    }
}

OTP_FUNC_DEF
int OTPMutexCreate(OTPMutex *mutexPtr, char **errorInfo)
{
    HANDLE mutex = CreateMutex(NULL, FALSE, NULL);
    if (mutex == NULL) {
	SET_SYSTEM_CALL_FAILURE("CreateMutex", GetLastError());
	return -1;
    } else {
	*mutexPtr = mutex;
	return 0;
    }
}

OTP_FUNC_DEF
int OTPMutexDestroy(OTPMutex mutex, char **errorInfo)
{
    if (!CloseHandle(mutex)) {
	SET_SYSTEM_CALL_FAILURE("CloseHandle", GetLastError());
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPMutexAcquire(OTPMutex mutex, char **errorInfo)
{
    DWORD result = WaitForSingleObject(mutex, INFINITE);
    if (result == WAIT_OBJECT_0) {
	return 0;
    } else {
       	if (result == WAIT_FAILED) {
	    SET_SYSTEM_CALL_FAILURE("WaitForSingleObject", GetLastError());
        } else {
	    SET_ERROR("WaitForSingleObject(mutex, INFINITE) succeeded "
	        "for reason other than getting mutex ownership");
	}
        return -1;
    }
}

OTP_FUNC_DEF
int OTPMutexRelease(OTPMutex mutex, char **errorInfo)
{
    if (!ReleaseMutex(mutex)) {
	SET_SYSTEM_CALL_FAILURE("ReleaseMutex", GetLastError());
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadVarCreate(OTPThreadVar *threadVarPtr, 
    char **errorInfo)
{
    DWORD threadVar = TlsAlloc();
    if (threadVar == TLS_OUT_OF_INDEXES) {
	SET_SYSTEM_CALL_FAILURE("TlsAlloc", GetLastError());
	return -1;
    } else {
	*threadVarPtr = threadVar;
        return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadVarDestroy(OTPThreadVar threadVar, char **errorInfo)
{
    if (!TlsFree(threadVar)) {
	SET_SYSTEM_CALL_FAILURE("TlsFree", GetLastError());
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadVarSet(OTPThreadVar threadVar, void *value, 
    char **errorInfo)
{
    if (!TlsSetValue(threadVar, value)) {
	SET_SYSTEM_CALL_FAILURE("TlsSetValue", GetLastError());
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadVarGet(OTPThreadVar threadVar, void** valuePtr, 
    char **errorInfo)
{
    void *value = TlsGetValue(threadVar);
    if ((value == NULL) && (GetLastError() != NO_ERROR)) {
	SET_SYSTEM_CALL_FAILURE("TlsGetValue", GetLastError());
	return -1;
    } else {
        *valuePtr = value;
        return 0;
    }
}

/* 
 * Our event model is such that each event wakes up at most one waiting
 * thread. For Windows, this means we have to use an auto-reset event
 * (a manual reset event would wake up any number of threads).
 */

OTP_FUNC_DEF
int OTPEventCreate(OTPEvent *eventPtr, char **errorInfo)
{
    HANDLE event = CreateEvent(NULL, FALSE, FALSE, NULL);
    if (event == NULL) {
	SET_SYSTEM_CALL_FAILURE("CreateEvent", GetLastError());
	return -1;
    } else {
	*eventPtr = event;
	return 0;
    }
}

OTP_FUNC_DEF
int OTPEventDestroy(OTPEvent event, char **errorInfo)
{
    if (!CloseHandle(event)) {
	SET_SYSTEM_CALL_FAILURE("CloseHandle", GetLastError());
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPEventSignal(OTPEvent event, char **errorInfo)
{
    if (!SetEvent(event)) {
	SET_SYSTEM_CALL_FAILURE("SetEvent", GetLastError());
	return -1;
    } else {
        return 0;
    }
}

OTP_FUNC_DEF
int OTPEventWait(OTPEvent event, char **errorInfo)
{
    DWORD result = WaitForSingleObject(event, INFINITE);
    if (result == WAIT_OBJECT_0) {
	return 0;
    } else {
       	if (result == WAIT_FAILED) {
	    SET_SYSTEM_CALL_FAILURE("WaitForSingleObject", GetLastError());
        } else {
	    SET_ERROR("WaitForSingleObject(event, INFINITE) succeeded "
	        "for readon other than occurrence of event");
	}
        return -1;
    }
}

/*
 * For Windows, the maximum wait time is ULONG_MAX milliseconds which is
 * about 500 days.
 */
OTP_FUNC_DEF
int OTPEventTimedWait(OTPEvent event, unsigned int msecs, 
    int *signalled, char **errorInfo)
{
    DWORD result = WaitForSingleObject(event,
	 msecs > 0xFFFFFFFF ? 0xFFFFFFFF : msecs);
    if (result == WAIT_OBJECT_0) {
	*signalled = 1;
	return 0;
    } else if (result == WAIT_TIMEOUT) {
	*signalled = 0;
	return 0;
    } else {
       	if (result == WAIT_FAILED) {
	    SET_SYSTEM_CALL_FAILURE("WaitForSingleObject", GetLastError());
        } else {
	    SET_ERROR("WaitForSingleObject() succeeded for reason "
		"other than occurrence of event or timeout");
	}
        return -1;
    }
}

OTP_FUNC_DEF
int OTPEventClear(OTPEvent event, char **errorInfo)
{
    if (!ResetEvent(event)) {
	SET_SYSTEM_CALL_FAILURE("ResetEvent", GetLastError());
	return -1;
    } else {
        return 0;
    }
}

/*--------------------------------------------------------------------------*/
#else                                           /* Implementations for Unix */
/*--------------------------------------------------------------------------*/

#include <sys/time.h>
#include <unistd.h> /* sleep() */

static void *run(void *arg)
{
    OTPRunnable *runnable = (OTPRunnable *) arg;
    runnable->func(runnable->arg);
    free(runnable);
    return NULL;
}

OTP_FUNC_DEF
int OTPThreadCreate(OTPThread *threadPtr, OTPThreadFunc func,
    void *arg, char **errorInfo)
{
    int result;
    pthread_t thread;

    OTPRunnable *runnable = (OTPRunnable *) malloc(sizeof(OTPRunnable));
    if (runnable == NULL) {
	SET_MALLOC_FAILURE(sizeof(OTPRunnable));
	return -1;
    }

    runnable->func = func;
    runnable->arg = arg;

    result = pthread_create(&thread, NULL, run, runnable);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_create", result);
	free(runnable);
	return -1;
    } else {
	*threadPtr = thread;
        return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadWait(OTPThread thread, char **errorInfo)
{
    int result = pthread_join(thread, NULL);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_join", result);
	return -1;
    } else {
        return 0;
    }
}

OTP_FUNC_DEF
int OTPMutexCreate(OTPMutex *mutexPtr, char **errorInfo)
{
    pthread_mutex_t *mutex = (pthread_mutex_t *) malloc(
	sizeof(pthread_mutex_t));
    if (mutex == NULL) {
	SET_MALLOC_FAILURE(sizeof(pthread_mutex_t));
	return -1;
    } else {
        int result = pthread_mutex_init(mutex, NULL);
        if (result != 0) {
	    SET_SYSTEM_CALL_FAILURE("pthread_mutex_init", result);
	    free(mutex);
	    return -1;
	} else {
            *mutexPtr = mutex;
            return 0;
	}
    }
}

OTP_FUNC_DEF
int OTPMutexDestroy(OTPMutex mutex, char **errorInfo)
{
    int result = pthread_mutex_destroy(mutex);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_mutex_destroy", result);
	return -1;
    } else {
	free(mutex);
	return 0;
    }
}

OTP_FUNC_DEF
int OTPMutexAcquire(OTPMutex mutex, char **errorInfo)
{
    int result = pthread_mutex_lock(mutex);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_mutex_lock", result);
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPMutexRelease(OTPMutex mutex, char **errorInfo)
{
    int result = pthread_mutex_unlock(mutex);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_mutex_unlock", result);
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadVarCreate(OTPThreadVar *threadVarPtr,
    char **errorInfo)
{
    pthread_key_t threadVar;

    int result = pthread_key_create(&threadVar, NULL);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_key_create", result);
	return -1;
    } else {
	*threadVarPtr = threadVar;
	return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadVarDestroy(OTPThreadVar threadVar, 
    char **errorInfo)
{
    int result = pthread_key_delete(threadVar);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_key_delete", result);
	return -1;
    } else {
	return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadVarSet(OTPThreadVar threadVar, void *value, 
    char **errorInfo)
{
    int result = pthread_setspecific(threadVar, value);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_setspecific", result);
	return -1;
    } else {
        return 0;
    }
}

OTP_FUNC_DEF
int OTPThreadVarGet(OTPThreadVar threadVar, void** valuePtr,
    char **errorInfo)
{
    /* pthread_getspecific() either succeeds or crashes */

    *valuePtr = pthread_getspecific(threadVar);
    return 0;
}

/*
 * Unix does not have an event construct like Windows. Instead,
 * Windows-like events can be constructed using a combination of
 * a mutex and a condition variable. How to do this follows quite
 * naturally from the way condition variables work.
 * 
 * Our event model is such that each event wakes up at most one waiting
 * thread. For Unix, this means using pthread_cond_signal() (rather than
 * pthread_cond_broadcast(), which would wake up all waiting threads).
 */

typedef struct OTPEventStruct {
    int signalled;
    pthread_cond_t cond;
    pthread_mutex_t mutex;
} OTPEventStruct;

OTP_FUNC_DEF
int OTPEventCreate(OTPEvent *eventPtr, char **errorInfo)
{
    OTPEventStruct *event = (OTPEventStruct *) malloc(sizeof(OTPEventStruct));
    if (event == NULL) {
	SET_MALLOC_FAILURE(sizeof(OTPEventStruct));
	return -1;
    } else {
        int result = pthread_cond_init(&event->cond, NULL);
        if (result != 0) {
	    SET_SYSTEM_CALL_FAILURE("pthread_cond_init", result);
	    free(event);
	    return -1;
	} else {
            result = pthread_mutex_init(&event->mutex, NULL);
            if (result != 0) {
	        SET_SYSTEM_CALL_FAILURE("pthread_mutex_init", result);
	        pthread_cond_destroy(&event->cond);
	        free(event);
	        return -1;
	    } else {
		event->signalled = 0;
    		*eventPtr = event;
		return 0;
	    }
	}
    }
}

OTP_FUNC_DEF
int OTPEventDestroy(OTPEvent event, char **errorInfo)
{
    int result = pthread_mutex_destroy(&event->mutex);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_mutex_destroy", result);
	return -1;
    } else {
        result = pthread_cond_destroy(&event->cond);
        if (result != 0) {
	    SET_SYSTEM_CALL_FAILURE("pthread_cond_destroy", result);
	    return -1;
	} else {
            free(event);
	    return 0;
	}
    }
}

OTP_FUNC_DEF
int OTPEventSignal(OTPEvent event, char **errorInfo)
{
    int result = pthread_mutex_lock(&event->mutex);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_mutex_lock", result);
	return -1;
    } else {
        event->signalled = 1;
        result = pthread_cond_signal(&event->cond);
        if (result != 0) {
	    SET_SYSTEM_CALL_FAILURE("pthread_cond_signal", result);
	    pthread_mutex_unlock(&event->mutex);
	    return -1;
	} else {
	    pthread_mutex_unlock(&event->mutex);
	    return 0;
	}
    }
}

OTP_FUNC_DEF
int OTPEventWait(OTPEvent event, char **errorInfo)
{
    int result = pthread_mutex_lock(&event->mutex);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_mutex_lock", result);
	return -1;
    } else {
        if (event->signalled) {
	    event->signalled = 0;
            pthread_mutex_unlock(&event->mutex);
	    return 0;
	} else {
	    result = pthread_cond_wait(&event->cond, &event->mutex);
	    if (result != 0) {
	        SET_SYSTEM_CALL_FAILURE("pthread_cond_wait", result);
                pthread_mutex_unlock(&event->mutex);
		return -1;
	    } else {
		event->signalled = 0;
                pthread_mutex_unlock(&event->mutex);
		return 0;
	    }
	}
    }
}

OTP_FUNC_DEF
int OTPEventTimedWait(OTPEvent event, unsigned int msecs, 
    int *signalled, char **errorInfo)
{
    int result = pthread_mutex_lock(&event->mutex);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_mutex_lock", result);
	return -1;
    } else {
        if (event->signalled) {
	   *signalled = 1;
	   event->signalled = 0;
	   pthread_mutex_unlock(&event->mutex);
	   return 0;
	} else if (msecs == 0) {
	    *signalled = 0;
	   pthread_mutex_unlock(&event->mutex);
	   return 0;
	} else {
	    unsigned int secs = msecs / 1000;
	    unsigned int usecs = (msecs % 1000) * 1000; 
    	    struct timeval now;
            result = gettimeofday(&now, NULL);
            if (result != 0) {
	        SET_SYSTEM_CALL_FAILURE("gettimeofday", errno);
		pthread_mutex_unlock(&event->mutex);
		return -1;
	    } else {
    	        struct timespec timeout;
                timeout.tv_sec = now.tv_sec + secs;
                timeout.tv_nsec = (now.tv_usec + usecs) * 1000;
	        result = pthread_cond_timedwait(&event->cond, &event->mutex,
	            &timeout);
		if (result != 0) {
		    if (result == ETIMEDOUT) {
		        *signalled = 0;
		        pthread_mutex_unlock(&event->mutex);
		        return 0;
		    } else {
	                SET_SYSTEM_CALL_FAILURE("pthread_cond_timedwait", 
			    result);
		        pthread_mutex_unlock(&event->mutex);
		        return -1;
		    }
		} else {
		    *signalled = 1;
		    event->signalled = 0;
		    pthread_mutex_unlock(&event->mutex);
		    return 0;
		}
	    }
        }
    }
}

OTP_FUNC_DEF
int OTPEventClear(OTPEvent event, char **errorInfo)
{
    int result = pthread_mutex_lock(&event->mutex);
    if (result != 0) {
	SET_SYSTEM_CALL_FAILURE("pthread_mutex_lock", result);
	return -1;
    } else {
	event->signalled = 0;
	pthread_mutex_unlock(&event->mutex);
    }
}

OTP_FUNC_DEF
void OTPThreadSleep(unsigned int secs)
{
    sleep(secs);
}

/*--------------------------------------------------------------------------*/
#endif                          /* End of platform specific implementations */
/*--------------------------------------------------------------------------*/
