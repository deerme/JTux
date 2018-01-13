/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#ifndef OTPSocket_h_included
#define OTPSocket_h_included

#ifndef OTP_FUNC_DECL
#define OTP_FUNC_DECL /* empty */
#endif

#include <stddef.h> /* size_t */
#include <time.h> /* time_t */

/*--------------------------------------------------------------------------*/
#ifdef _WIN32                               /* Windows specific definitions */
/*--------------------------------------------------------------------------*/

#include <winsock2.h>

typedef SOCKET OTPSocket;

typedef struct {
    WSAEVENT ioEvent;
    WSAEVENT signalEvent;
} OTPSocketSignal;

/*--------------------------------------------------------------------------*/
#else                                          /* Unix specific definitions */
/*--------------------------------------------------------------------------*/

#include <sys/types.h>
#include <sys/socket.h>

typedef int OTPSocket;

typedef struct {
    int fdToRead;
    int fdToWrite;
} OTPSocketSignal;

/*--------------------------------------------------------------------------*/
#endif                              /* End of platform specific definitions */
/*--------------------------------------------------------------------------*/

#ifdef __cplusplus
extern "C" {
#endif

OTP_FUNC_DECL
int OTPSocketInitialize(char **errorInfo);

OTP_FUNC_DECL
int OTPSocketConnect(OTPSocket *socket, const char *hostNameOrAddress, 
    const char* portOrServiceName, char **errorInfo);

OTP_FUNC_DECL
int OTPSocketSend(OTPSocket socket, void *buf, size_t len, 
    size_t *bytesSent, time_t *timeout, char **errorInfo);

OTP_FUNC_DECL
int OTPSocketSendAll(OTPSocket socket, void *buf, size_t len, 
    time_t *timeout, char **errorInfo);

OTP_FUNC_DECL
int OTPSocketReceive(OTPSocket socket, void *buf, size_t size, 
    size_t *bytesReceived, time_t *timeout, char **errorInfo);

OTP_FUNC_DECL
int OTPSocketReceiveFull(OTPSocket socket, void *buf, size_t len, 
    time_t *timeout, char **errorInfo);

OTP_FUNC_DECL
int OTPSocketClose(OTPSocket socket, char **errorInfo);

#define OTP_SOCKET_NO_DATA_PENDING 0
#define OTP_SOCKET_DATA_PENDING 1
#define OTP_SOCKET_DISCONNECTED 2
#define OTP_SOCKET_TIMEOUT 3
#define OTP_SOCKET_SIGNAL 4

/* Can return OTP_SOCKET_[NO_DATA_PENDING|DATA_PENDING|DISCONNECTED] */

OTP_FUNC_DECL
int OTPSocketGetStatus(OTPSocket socket, char **errorInfo);

OTP_FUNC_DECL
int OTPSocketSignalCreate(OTPSocketSignal *signal, char **errorInfo);

/* Can return OTP_SOCKET_[DATA_PENDING|DISCONNECTED|TIMEOUT|ALARM] */

OTP_FUNC_DECL
int OTPSocketWaitForEvent(OTPSocket socket, OTPSocketSignal signal,
    time_t *timeout, char **errorInfo);

OTP_FUNC_DECL
int OTPSocketSignalRaise(OTPSocketSignal signal, char **errorInfo);

OTP_FUNC_DECL
int OTPSocketSignalDestroy(OTPSocketSignal signal, char **errorInfo);

#ifdef __cplusplus
}
#endif

#endif /* OTPSocket_h_included */
