/* * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPSocket.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPSocket.h"
#include "OTPString.h"
#include "OTPError.h"

#include <time.h>
#include <assert.h>

#define SEND 0
#define RECV 1

/*--------------------------------------------------------------------------*/
#ifdef _WIN32                               /* Windows specific definitions */
/*--------------------------------------------------------------------------*/

#include <winsock2.h>
#include <ws2tcpip.h>

#include <limits.h>

typedef int ssize_t;

#define SSIZE_MAX INT_MAX

/* SOCKET_ERROR defined by WinSock */

/* INVALID_SOCKET defined by WinSock */

#define SOCKET_ERRNO WSAGetLastError()

#define CLOSE_SOCKET closesocket
#define CLOSE_SOCKET_STR "closesocket"

#define CONNECTION_REFUSED WSAECONNREFUSED
#define CONNECT_TIMEOUT WSAETIMEDOUT
#define NETWORK_UNREACHABLE WSAENETUNREACH
#define CONNECTION_CLOSED_BY_RECVR WSAECONNRESET

static int tryNextSocketErrors[] = {
    WSAENETDOWN, WSAEAFNOSUPPORT, WSAEPROTONOSUPPORT, WSAESOCKTNOSUPPORT
};

OTP_FUNC_DEF
int OTPSocketInitialize(char **errorInfo)
{
    WSADATA wsaData;

    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
	int err = WSAGetLastError();
	if (err == WSAVERNOTSUPPORTED) {
	    SET_ERROR("Winsock 2.2 not supported");
	} else {
	    SET_SYSTEM_CALL_FAILURE("WSAStartup", err);
	}
	return -1;
    }

    return 0;
}

static int setNonBlocking(OTPSocket socket, char **errorInfo)
{
    unsigned long nonBlocking = 1;

    if (ioctlsocket(socket, FIONBIO, &nonBlocking) != 0) {
	SET_SYSTEM_CALL_FAILURE("ioctlsocket", WSAGetLastError());
	return -1;
    }

    return 0;
}

static int waitForNonBlockingCondition(OTPSocket socket, int operation,
    time_t *timeout, char **errorInfo)
{
    fd_set fds;
    struct timeval tv;
    time_t before;
    int result;

    FD_ZERO(&fds);

    FD_SET(socket, &fds);

    if (timeout != NULL) {
        tv.tv_sec = *timeout;
        tv.tv_usec = 0;
        before = time(NULL);
    }

    if (operation == RECV) {
        result = select(0, &fds, NULL, NULL, (timeout == NULL) ? NULL : &tv);
    } else {
	result = select(0, NULL, &fds, NULL, (timeout == NULL) ? NULL : &tv);
    }

    if (result == SOCKET_ERROR) {
	SET_SYSTEM_CALL_FAILURE("select", WSAGetLastError());
	return -1;
    }

    if (result == 0) {
	assert(timeout != NULL);
	*timeout = 0; /* Allow caller to distinguish timeout from I/O errors */
	OTPErrorSet(errorInfo, "%s timeout", 
	    (operation == RECV) ? "Receive" : "Send");
	return -1;
    }

    if (timeout != NULL) {
	time_t after = time(NULL);
	*timeout = *timeout - (after - before);
    }

    return 0;
}

/*--------------------------------------------------------------------------*/
#else                                          /* Unix specific definitions */
/*--------------------------------------------------------------------------*/

#include <unistd.h>
#include <stropts.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <netdb.h>
#include <poll.h>
#include <string.h>
#include <limits.h>
#include <errno.h>

#define SOCKET_ERROR -1

#define INVALID_SOCKET -1

#define SOCKET_ERRNO errno

#define CLOSE_SOCKET close
#define CLOSE_SOCKET_STR "close"

#define CONNECTION_REFUSED ECONNREFUSED
#define CONNECT_TIMEOUT ETIMEDOUT
#define NETWORK_UNREACHABLE ENETUNREACH
#define CONNECTION_CLOSED_BY_RECVR EPIPE

/* INFTIM not defined on Linux 2.4 */

#ifndef INFTIM
#define INFTIM -1
#endif

static int tryNextSocketErrors[] = { EPROTONOSUPPORT, EACCES };

OTP_FUNC_DEF
int OTPSocketInitialize(char **errorInfo)
{
    return 0;
}

static int setNonBlocking(OTPSocket socket, char **errorInfo)
{
    int flags = fcntl(socket, F_GETFL);
    if (flags == -1) {
	SET_SYSTEM_CALL_FAILURE("fcntl", errno);
	return -1;
    }

    if (fcntl(socket, F_SETFL, flags | O_NONBLOCK) == -1) {
	SET_SYSTEM_CALL_FAILURE("fcntl", errno);
	return -1;
    }

    return 0;
}

static int waitForNonBlockingCondition(OTPSocket socket, int operation,
    time_t *timeout, char **errorInfo)
{
    struct pollfd pollFd;
    time_t before;
    int result;

    pollFd.fd = socket;
    pollFd.events = (operation == RECV) ? POLLIN : POLLOUT;

    if (timeout != NULL) {
	before = time(NULL);
    }

    result = poll(&pollFd, 1, (timeout == NULL) ? INFTIM : (*timeout) * 1000);
    if (result == -1) {
	SET_SYSTEM_CALL_FAILURE("poll", errno);
	return -1;
    }

    if (result == 0) {
	assert(timeout != NULL);
	*timeout = 0; /* Allow caller to distinguish timeout from I/O errors */
	OTPErrorSet(errorInfo, "%s timeout",
	    (operation == RECV) ? "Receive" : "Send");
	return -1;
    }

    if (timeout != NULL) {
	time_t after = time(NULL);
	*timeout = *timeout - (after - before);
    }

    return 0;
}

/*--------------------------------------------------------------------------*/
#endif                              /* End of platform specific definitions */
/*--------------------------------------------------------------------------*/

static int tryOpenSocket(OTPSocket *socketPtr, struct addrinfo *ai,
    char **errorInfo)
{
    OTPSocket s = socket(ai->ai_family, ai->ai_socktype, 
	ai->ai_protocol);
    if (s == INVALID_SOCKET) {
	int err = SOCKET_ERRNO;
	int i = 0;
	while (i < sizeof(tryNextSocketErrors) / sizeof(int)) {
	    if (err == tryNextSocketErrors[i]) {
	        return 0;
	    }
	    i++;
	}
	SET_SYSTEM_CALL_FAILURE("socket", err);
	return -1;
    }
    *socketPtr = s;
    return 1;
}

OTP_FUNC_DEF
int OTPSocketConnect(OTPSocket *socketPtr,
    const char *hostNameOrAddress, const char *portOrServiceName,
    char **errorInfo)
{
    struct addrinfo filter;
    struct addrinfo *ais;
    struct addrinfo *ai;
    OTPSocket s;
    int err;

    memset(&filter, 0, sizeof(struct addrinfo));
    filter.ai_family = PF_UNSPEC; /* accept both IPv4 and IPv6 */
    filter.ai_socktype = SOCK_STREAM; /* accept only TCP, not UDP */

    err = getaddrinfo(hostNameOrAddress, portOrServiceName, &filter, &ais);
    if (err != 0) {
	if (err == EAI_NODATA) {
	    SET_ERROR("No address information available for specified host");
	} else if (err == EAI_NONAME) {
	    SET_ERROR("Unknown host or service name");
	} else {
	    SET_SYSTEM_CALL_FAILURE("getaddrinfo", err);
	}
	return -1;
    }

    ai = ais;
    while (ai != NULL) {
	int result = tryOpenSocket(&s, ai, errorInfo);
	if (result == 1) { /* Socket opened successfully */
	    break;
	} else if (result == 0) { /* Open failed but next may succeed */
	    ai = ai->ai_next;
	} else { /* Fatal error opening socket */
            freeaddrinfo(ais);
	    return -1;
	}
    }

    if (ai == NULL) {
        SET_ERROR("Could not open socket for any available address");
        freeaddrinfo(ais);
	return -1;
    }

    if (connect(s, ai->ai_addr, ai->ai_addrlen) == SOCKET_ERROR) {
	int err = SOCKET_ERRNO;
	if (err == CONNECTION_REFUSED) {
	    SET_ERROR("Connection refused");
	} else if (err == CONNECT_TIMEOUT) {
	    SET_ERROR("Connect timeout");
	} else if (err == NETWORK_UNREACHABLE) {
	    SET_ERROR("Network unreachable");
	} else {
	    SET_SYSTEM_CALL_FAILURE("connect", err);
	}
	CLOSE_SOCKET(s); /* Must be done *after* checking SOCKET_ERRNO */
        freeaddrinfo(ais); /* Must be done *after* using ai in connect() */
	return -1;
    }

    freeaddrinfo(ais); /* Must be done *after* using ai in connect() */

    if (setNonBlocking(s, errorInfo) == -1) {
	CLOSE_SOCKET(s);
	return -1;
    }

    *socketPtr = s;

    return 0;
}

OTP_FUNC_DEF
int OTPSocketSend(OTPSocket socket, void *buf, size_t len,
    size_t *bytesSent, time_t *timeout, char **errorInfo)
{
    ssize_t result;

    if (waitForNonBlockingCondition(socket, SEND, timeout, errorInfo) == -1) {
	return -1;
    }

    if (len > SSIZE_MAX) {
	/* Prevent ambiguous return from send() and int overflow (Windows) */
	len = SSIZE_MAX;
    }

    result = send(socket, (char *) buf, len, 0);
    if (result == SOCKET_ERROR) {
	int err = SOCKET_ERRNO;
	if (err == CONNECTION_CLOSED_BY_RECVR) {
	    SET_ERROR("Connection closed by receiver");
	} else {
	    SET_SYSTEM_CALL_FAILURE("send", err);
	}
	return -1;
    }

    *bytesSent = result;

    return 0;
}

OTP_FUNC_DEF
int OTPSocketSendAll(OTPSocket socket, void *buf, size_t len,
    time_t *timeout, char **errorInfo)
{
    size_t bytesSent;

    while (len > 0) {
	if (OTPSocketSend(socket, buf, len, &bytesSent, timeout, 
		errorInfo) == -1) {
	    return -1;
	}
	buf = ((char*) buf) + bytesSent;
	len = len - bytesSent;
    }

    return 0;
}

OTP_FUNC_DEF
int OTPSocketReceive(OTPSocket socket, void *buf, size_t len, 
    size_t *bytesReceived, time_t *timeout, char **errorInfo)
{
    ssize_t result;

    if (waitForNonBlockingCondition(socket, RECV, timeout, errorInfo) == -1) {
	return -1;
    }

    if (len > SSIZE_MAX) {
	/* Prevent ambiguous return from recv() and int overflow (Windows) */
	len = SSIZE_MAX;
    }

    result = recv(socket, (char *) buf, len, 0);
    if (result == SOCKET_ERROR) {
	SET_SYSTEM_CALL_FAILURE("recv", SOCKET_ERRNO);
	return -1;
    }

    *bytesReceived = result;

    return 0;
}

OTP_FUNC_DEF
int OTPSocketReceiveFull(OTPSocket socket, void *buf, size_t len,
    time_t *timeout, char **errorInfo)
{
    size_t bytesReceived;

    while (len > 0) {
	if (OTPSocketReceive(socket, buf, len, &bytesReceived, timeout, 
		errorInfo) == -1) {
	    return -1;
	} else if (bytesReceived == 0) {
	    SET_ERROR("Connection closed");
	    return -1;
	} else {
	    buf = ((char*) buf) + bytesReceived;
	    len = len - bytesReceived;
	}
    }

    return 0;
}

OTP_FUNC_DEF
int OTPSocketClose(OTPSocket socket, char **errorInfo)
{
    if (CLOSE_SOCKET(socket) == SOCKET_ERROR) {
	SET_SYSTEM_CALL_FAILURE(CLOSE_SOCKET_STR, SOCKET_ERRNO);
	return -1;
    }
    return 0;
}


/*--------------------------------------------------------------------------*/
#ifdef _WIN32                               /* Windows specific definitions */
/*--------------------------------------------------------------------------*/

OTP_FUNC_DEF
int OTPSocketGetStatus(OTPSocket socket, char **errorInfo)
{
    fd_set fds;
    struct timeval tv;
    int result;
    unsigned long bytesPending;

    FD_ZERO(&fds);

    FD_SET(socket, &fds);

    tv.tv_sec = 0;
    tv.tv_usec = 0;
 
    result = select(0, &fds, NULL, NULL, &tv);
    if (result == SOCKET_ERROR) {
	SET_SYSTEM_CALL_FAILURE("select", WSAGetLastError());
	return -1;
    }

    if (result == 0) {
	return OTP_SOCKET_NO_DATA_PENDING;
    }

    if (ioctlsocket(socket, FIONREAD, &bytesPending) == SOCKET_ERROR) {
	SET_SYSTEM_CALL_FAILURE("ioctlsocket", WSAGetLastError());
	return -1;
    }

    if (bytesPending > 0) {
	return OTP_SOCKET_DATA_PENDING;
    } else {
        return OTP_SOCKET_DISCONNECTED;
    }
}

OTP_FUNC_DEF
int OTPSocketSignalCreate(OTPSocketSignal *signal, char **errorInfo)
{
    WSAEVENT ioEvent;
    WSAEVENT signalEvent;
   
    ioEvent = WSACreateEvent();
    if (ioEvent == WSA_INVALID_EVENT) {
	SET_SYSTEM_CALL_FAILURE("WSACreateEvent", WSAGetLastError());
	return -1;
    }
   
    signalEvent = WSACreateEvent();
    if (signalEvent == WSA_INVALID_EVENT) {
	SET_SYSTEM_CALL_FAILURE("WSACreateEvent", WSAGetLastError());
	WSACloseEvent(ioEvent);
	return -1;
    }

    signal->ioEvent = ioEvent;
    signal->signalEvent = signalEvent;

    return 0;
}

OTP_FUNC_DEF
int OTPSocketWaitForEvent(OTPSocket socket, OTPSocketSignal signal, 
    time_t *timeout, char **errorInfo)
{
    time_t before;
    DWORD result;

    WSAEVENT events[2];

    int spinCount = 0;

again:

    if (WSAEventSelect(socket, signal.ioEvent, FD_READ | FD_CLOSE)
	    == SOCKET_ERROR) {
	SET_SYSTEM_CALL_FAILURE("WSAEventSelect", WSAGetLastError());
	return -1;
    }

    if (timeout != NULL) {
	before = time(NULL);
    }

    events[0] = signal.ioEvent;
    events[1] = signal.signalEvent;

    result = WSAWaitForMultipleEvents(2, events, FALSE, 
	(timeout == NULL) ? WSA_INFINITE : (*timeout) * 1000, FALSE);
    if (result == WSA_WAIT_FAILED) {
	SET_SYSTEM_CALL_FAILURE("WSAWaitForMultipleEvents", 
	    WSAGetLastError());
	return -1;
    }

    if (result == WSA_WAIT_TIMEOUT) {
	assert(timeout != NULL);
	*timeout = 0;
	return OTP_SOCKET_TIMEOUT;
    }

    if (timeout != NULL) {
	time_t after = time(NULL);
	*timeout = *timeout - (after - before);
    }

    if (result == WSA_WAIT_EVENT_0) {
        WSANETWORKEVENTS networkEvents;
	if (WSAEnumNetworkEvents(socket, signal.ioEvent, &networkEvents)
		== SOCKET_ERROR) {
	    SET_SYSTEM_CALL_FAILURE("WSAEnumNetworkEvents", 
		WSAGetLastError());
	    return -1;
	}
	if ((networkEvents.lNetworkEvents & FD_READ) != 0) {
	    return OTP_SOCKET_DATA_PENDING;
	}
	if ((networkEvents.lNetworkEvents & FD_CLOSE) != 0) {
	    return OTP_SOCKET_DISCONNECTED;
	}
	/* It turns out that lNetworkEvents can be 0. This doesn't make
	 * sense but it happens in practice. */
	spinCount++;
	if (spinCount > 1000) {
	    SET_ERROR("Spinning in OTPSocketSleep()");
	    return -1;
	}
	goto again;
    }

    if (result == WSA_WAIT_EVENT_0 + 1) {
	return OTP_SOCKET_SIGNAL;
    }

    OTPErrorSet(errorInfo, "WSAWaitForMultipleEvents() returned "
	"unexpected value %d", result);

    return -1;
}

OTP_FUNC_DEF
int OTPSocketSignalRaise(OTPSocketSignal signal, char **errorInfo)
{
    if (!WSASetEvent(signal.signalEvent)) {
	SET_SYSTEM_CALL_FAILURE("WSASetEvent", WSAGetLastError());
	return -1;
    }
    return 0;
}

OTP_FUNC_DEF
int OTPSocketSignalDestroy(OTPSocketSignal signal, char **errorInfo)
{
    if (!WSACloseEvent(signal.signalEvent)) {
	SET_SYSTEM_CALL_FAILURE("WSACloseEvent", WSAGetLastError());
	WSACloseEvent(signal.ioEvent);
	return -1;
    }

    if (!WSACloseEvent(signal.ioEvent)) {
	SET_SYSTEM_CALL_FAILURE("WSACloseEvent", WSAGetLastError());
	return -1;
    }

    return 0;
}

/*--------------------------------------------------------------------------*/
#else                                          /* Unix specific definitions */
/*--------------------------------------------------------------------------*/

OTP_FUNC_DEF
int OTPSocketGetStatus(OTPSocket socket, char **errorInfo)
{
    struct pollfd pollFd;
    int result;
    int bytesPending;

    pollFd.fd = socket;
    pollFd.events = POLLIN;

    result = poll(&pollFd, 1, 0);
    if (result == -1) {
	SET_SYSTEM_CALL_FAILURE("poll", errno);
	return -1;
    }

    if (result == 0) {
	return OTP_SOCKET_NO_DATA_PENDING;
    }

    if (ioctl(socket, I_NREAD, &bytesPending) == -1) {
	SET_SYSTEM_CALL_FAILURE("ioctl", errno);
	return -1;
    }

    if (bytesPending > 0) {
	return OTP_SOCKET_DATA_PENDING;
    } else {
	return OTP_SOCKET_DISCONNECTED;
    }
}

OTP_FUNC_DEF
int OTPSocketSignalCreate(OTPSocketSignal *signal, char **errorInfo)
{
    int fds[2];

    if (pipe(fds) == -1) {
	SET_SYSTEM_CALL_FAILURE("pipe", errno);
	return -1;
    }

    signal->fdToRead = fds[0];
    signal->fdToWrite = fds[1];

    return 0;
}

OTP_FUNC_DEF
int OTPSocketWaitForEvent(OTPSocket socket, OTPSocketSignal signal, 
    time_t *timeout, char **errorInfo)
{
    struct pollfd pollFds[2];
    time_t before;
    int result;

    pollFds[0].fd = socket;
    pollFds[0].events = POLLIN;

    pollFds[1].fd = signal.fdToRead;
    pollFds[1].events = POLLIN;

    if (timeout != NULL) {
	before = time(NULL);
    }

    result = poll(pollFds, 2, (timeout == NULL) ? INFTIM : (*timeout) * 1000);
    if (result == -1) {
	SET_SYSTEM_CALL_FAILURE("poll", errno);
	return -1;
    }

    if (result == 0) {
	assert(timeout != NULL);
	*timeout = 0;
	return OTP_SOCKET_TIMEOUT;
    }

    if (timeout != NULL) {
	time_t after = time(NULL);
	*timeout = *timeout - (after - before);
    }

    if (pollFds[0].events != 0) {

        int bytesPending;

	if (ioctl(socket, I_NREAD, &bytesPending) == -1) {
	    SET_SYSTEM_CALL_FAILURE("ioctl", errno);
	    return -1;
	}

	if (bytesPending > 0) {
	    return OTP_SOCKET_DATA_PENDING;
	} else {
	    return OTP_SOCKET_DISCONNECTED;
	}
    }

    if (pollFds[1].events != 0) {
	char byte;
	if (read(signal.fdToRead, &byte, 1) == -1) {
	    SET_SYSTEM_CALL_FAILURE("read()", errno);
	    return -1;
	}
	return OTP_SOCKET_SIGNAL;
    }

    OTPErrorSet(errorInfo, "poll() returned %d without any data pending", 
	result);
    return -1;
}

OTP_FUNC_DEF
int OTPSocketSignalRaise(OTPSocketSignal signal, char **errorInfo)
{
    char byte = 0;
    if (write(signal.fdToWrite, &byte, 1) == -1) {
	SET_SYSTEM_CALL_FAILURE("write()", errno);
	return -1;
    }
    return 0;
}

OTP_FUNC_DEF
int OTPSocketSignalDestroy(OTPSocketSignal signal, char **errorInfo)
{
    if (close(signal.fdToWrite) == -1) {
	SET_SYSTEM_CALL_FAILURE("close", errno);
	close(signal.fdToRead);
	return -1;
    }

    if (close(signal.fdToRead) == -1) {
	SET_SYSTEM_CALL_FAILURE("close", errno);
	return -1;
    }

    return 0;
}

/*--------------------------------------------------------------------------*/
#endif                              /* End of platform specific definitions */
/*--------------------------------------------------------------------------*/
