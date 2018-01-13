/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPTuxServer.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPTuxServer.h"
#include "OTPUserlog.h"
#include "OTPThread.h"
#include "OTPString.h"
#include "OTPError.h"

#include <atmi.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAXTUXSVCLEN 15 /* See tpadvertise(3c) */

#ifdef _WIN32
#define TUXCALL __stdcall
#else
#define TUXCALL
#endif

typedef struct {
    int (TUXCALL *tpadvertise)(char *, void (*)(TPSVCINFO *));
    int (TUXCALL *tpunadvertise)(char *);
    void (TUXCALL *tpreturn)(int, long, char *, long, long);
    void (TUXCALL *tpforward)(char *, char *, long, long);
} TuxedoInterface;

static int TUXCALL test_tpadvertise(char *serviceName, 
    void (*func)(TPSVCINFO *))
{
    printf("OTPTuxServerTest: tpadvertise('%s', %X)\n", serviceName, func);
    return 0;
}

static int TUXCALL test_tpunadvertise(char *serviceName)
{
    printf("OTPTuxServerTest: tpunadvertise('%s')\n", serviceName);
    return 0;
}

static void TUXCALL test_tpreturn(int rval, long rcode, char *data, long len,
    long flags)
{
    printf("OTPTuxServerTest: tpreturn(%d, %d, %X, %d, %X)\n",
	rval, rcode, data, len, flags);
}

static void TUXCALL test_tpforward(char *svc, char *data, long len, long flags)
{
    printf("OTPTuxServerTest: tpforward('%s', %X, %d, %X)\n",
	svc, data, len, flags);
}

/*------------------*/
/* Service routines */
/*------------------*/

typedef struct ServiceRoutine {
    char *name;
    void *dispatchInfo;
    struct ServiceRoutine *next;
} ServiceRoutine;

static ServiceRoutine *serviceRoutines = NULL;

static ServiceRoutine *getServiceRoutine(const char *name)
{
    ServiceRoutine *sr = serviceRoutines;
    while (sr != NULL) {
	if (strcmp(sr->name, name) == 0) {
	    return sr;
	}
	sr = sr->next;
    }
    return NULL;
}

static ServiceRoutine *addServiceRoutine(const char *name, 
    void *dispatchInfo, char **errorInfo)
{
    ServiceRoutine *sr = (ServiceRoutine *) malloc(sizeof(ServiceRoutine));
    if (sr == NULL) {
	SET_MALLOC_FAILURE(sizeof(ServiceRoutine));
	return NULL;
    }

    sr->name = OTPStringDuplicate(name, errorInfo);
    if (sr->name == NULL) {
	free(sr);
	return NULL;
    }
    sr->dispatchInfo = dispatchInfo;
    sr->next = serviceRoutines;

    serviceRoutines = sr;

    return sr;
}

/*------------------------*/
/* Service advertisements */
/*------------------------*/

typedef struct ServiceAdvertisement {
    char *serviceName;
    ServiceRoutine *serviceRoutine;
    struct ServiceAdvertisement *prev;
    struct ServiceAdvertisement *next;
} ServiceAdvertisement;

static ServiceAdvertisement *serviceAdvertisements = NULL;

static ServiceAdvertisement *getServiceAdvertisement(const char *serviceName)
{
    ServiceAdvertisement *sa = serviceAdvertisements;
    while (sa != NULL) {
	/* Must use strncmp() to silently truncate service names to */
	/* MAXTUXSVCLEN characters just like Tuxedo does */
	if (strncmp(sa->serviceName, serviceName, MAXTUXSVCLEN) == 0) {
	    return sa;
	}
	sa = sa->next;
    }
    return NULL;
}

static ServiceAdvertisement *addServiceAdvertisement(const char *serviceName, 
    ServiceRoutine *serviceRoutine, char **errorInfo)
{
    ServiceAdvertisement *sa = (ServiceAdvertisement *) 
	malloc(sizeof(ServiceAdvertisement));
    if (sa == NULL) {
	SET_MALLOC_FAILURE(sizeof(ServiceAdvertisement));
	return NULL;
    }

    sa->serviceName = OTPStringDuplicate(serviceName, errorInfo);
    if (sa->serviceName == NULL) {
	ADD_ERROR_CONTEXT("Error duplicating service name");
	free(sa);
	return NULL;
    }
    sa->serviceRoutine = serviceRoutine;

    sa->prev = NULL;

    if (serviceAdvertisements != NULL) {
        serviceAdvertisements->prev = sa;
    }
    sa->next = serviceAdvertisements;

    serviceAdvertisements = sa;

    return sa;
}

static void deleteServiceAdvertisement(ServiceAdvertisement *sa)
{
    if (sa->prev != NULL) {
	sa->prev->next = sa->next;
    }

    if (sa->next != NULL) {
	sa->next->prev = sa->prev;
    }

    if (sa == serviceAdvertisements) {
	serviceAdvertisements = sa->next;
    }

    free(sa->serviceName);

    free(sa);
}

/*---------------------------------------*/
/* Server initialization and termination */
/*---------------------------------------*/

static int testMode = 0;

OTP_FUNC_DEF
void OTPTuxServerSetTestMode()
{
    testMode = 1;
}

static OTPThreadVar serviceThreadData;

static OTPTuxServerImpl server;

static TuxedoInterface tux;

OTP_FUNC_DEF
int OTPTuxServerInit(OTPTuxServerImpl *serverImpl, char **errorInfo)
{
    if (OTPThreadVarCreate(&serviceThreadData, errorInfo) == -1) {
	return -1;
    }

    memcpy(&server, serverImpl, sizeof(OTPTuxServerImpl));

    if (testMode) {
	tux.tpadvertise = test_tpadvertise;
	tux.tpunadvertise = test_tpunadvertise;
	tux.tpreturn = test_tpreturn;
	tux.tpforward = test_tpforward;
    } else {
	tux.tpadvertise = tpadvertise;
	tux.tpunadvertise = tpunadvertise;
	tux.tpreturn = tpreturn;
	tux.tpforward = tpforward;
    }

    return 0;
}

OTP_FUNC_DEF
void OTPTuxServerDone()
{
    while (serviceAdvertisements != NULL) {
	ServiceAdvertisement *sa = serviceAdvertisements;
	serviceAdvertisements = sa->next;
	tux.tpunadvertise(sa->serviceName);
	free(sa->serviceName);
	free(sa);
    }

    while (serviceRoutines != NULL) {
	ServiceRoutine *sr = serviceRoutines;
	serviceRoutines = sr->next;
	server.freeDispatchInfo(sr->dispatchInfo);
	free(sr->name);
	free(sr);
    }

    OTPThreadVarDestroy(serviceThreadData, NULL);
}

/*------------------*/
/* Startup services */
/*------------------*/

static ServiceRoutine *getOrAddServiceRoutine(const char *name,
    char **errorInfo)
{
    ServiceRoutine *sr = getServiceRoutine(name);
    if (sr == NULL) {
	void *dispatchInfo;
       	if (server.getDispatchInfo(name, &dispatchInfo) == -1) {
	    SET_ERROR("Invalid service routine name");
	    return NULL;
	}
	sr = addServiceRoutine(name, dispatchInfo, errorInfo);
	if (sr == NULL) {
	    server.freeDispatchInfo(dispatchInfo);
	    return NULL;
	}
    }
    return sr;
}

static int advertiseInitialService(const char *serviceName, 
    ServiceRoutine *serviceRoutine, char **errorInfo)
{
    ServiceAdvertisement *sa = getServiceAdvertisement(serviceName);
    if (sa != NULL) {
	SET_ERROR("Service already advertised");
	return -1;
    }

    if (serviceRoutine == NULL) {
	serviceRoutine = getOrAddServiceRoutine(serviceName, 
	    errorInfo);
	if (serviceRoutine == NULL) {
	    return -1;
	}
    }

    sa = addServiceAdvertisement(serviceName, serviceRoutine, errorInfo);
    if (sa == NULL) {
	return -1;
    }

    if (tux.tpadvertise((char *) serviceName, server.tpservice) == -1) {
	SET_ERROR(tpstrerror(tperrno));
	deleteServiceAdvertisement(sa);
	return -1;
    }

    return 0;
}

static int advertiseInitialServices(char *serviceNames,
    ServiceRoutine *serviceRoutine, char **errorInfo)
{
    int isLast = 0;

    char *serviceName = serviceNames;

    while (1) {
	char *ch = serviceName;
	while ((*ch != '\0') && (*ch != ',')) {
	    ch++;
	}
	if (*ch == '\0') {
	    isLast = 1;
	} else {
	    *ch = '\0'; /* serviceNames is a duplicate so this is OK */
	}
	if (advertiseInitialService(serviceName, serviceRoutine, 
		errorInfo) == -1) {
	    OTPErrorAddInfo(errorInfo, "Error starting service '%s'",
		serviceName);
	    return -1;
	}
	if (isLast) {
	    return 0;
	}
	serviceName = ch + 1;
    }
}

static int setupFromString(char *spec, char **errorInfo)
{
    char *colon;

    ServiceRoutine *serviceRoutine;

    char *serviceNames; /* duplicate to allow replacing commas by zeros */

    if (OTPStringLocateWhitespace(spec) != NULL) {
	SET_ERROR("Unexpected whitespace in startup service specification");
	return -1;
    }

    colon = strchr(spec, ':');

    if (colon == NULL) {
	serviceRoutine = NULL;
	serviceNames = OTPStringDuplicate(spec, errorInfo);
	if (serviceNames == NULL) {
	    return -1;
	}
    } else {
	serviceRoutine = getOrAddServiceRoutine(colon + 1, errorInfo);
	if (serviceRoutine == NULL) {
	    return -1;
	}
	if (spec == colon) { /* No service names specified */
	    return 0;
	}
	serviceNames = OTPStringDuplicateBounded(spec, colon - spec, 
	    errorInfo);
	if (serviceNames == NULL) {
	    return -1;
	}
    }

    if (advertiseInitialServices(serviceNames, serviceRoutine, 
	    errorInfo) == -1) {
	free(serviceNames);
	return -1;
    }

    free(serviceNames);
    return 0;
}

static int setupFromFile(char *fileName, char **errorInfo)
{
    FILE *file = fopen(fileName, "r");
    if (file == NULL) {
	OTPErrorSet(errorInfo, "Could not open file '%s'", fileName);
	return -1;
    } else {
	int result = 0;
	char line[1024];
	char *eol; /* end of line */
	int lineNumber = 1;
	while (fgets(line, sizeof(line), file) != NULL) {
	    int len = strlen(line);
	    if (len == sizeof(line) - 1) {
		OTPErrorSet(errorInfo, "Line %d too long", lineNumber);
		result = -1;
		break;
	    }
	    /* servopts(5) specifies '#' and ':' as a comment characters */
	    /* We only support '#', however, as ':' is already used for */
	    /* separating service names from service routine names */
	    eol = strchr(line, '#');
	    if (eol == NULL) {
		eol = &line[len];
	    }
	    while ((eol > line) && isspace(eol[-1])) {
		eol--; /* trim trailing whitespace */
	    }
	    if (eol > line) { /* line is not empty */
		*eol = '\0';
	        if (setupFromString(line, errorInfo) == -1) {
		    OTPErrorSet(errorInfo, "Error processing line %d", 
			lineNumber);
		    result = -1;
		    break;
		}
	    }
	    lineNumber++;
	}
	fclose(file);
	return result;
    }
}

OTP_FUNC_DEF
int OTPTuxServerSetup(char *setupString, char **errorInfo)
{
    if (setupString[0] == '@') {
	return setupFromFile(setupString + 1, errorInfo);
    } else {
	return setupFromString(setupString, errorInfo);
    }
}

/*-------------------------------*/
/* Dynamic service advertisement */
/*-------------------------------*/

static void tpinvalid(TPSVCINFO *svcinfo)
{
    /* This is a bogus service routine */
}

OTP_FUNC_DEF
int OTPTuxServerAdvertise(char *svc, const char *func)
{
    ServiceAdvertisement *sa;

    if ((svc == NULL) || (func == NULL)) {
	tperrno = TPEINVAL;
	return -1;
    }
   
    sa = getServiceAdvertisement(svc);
    if (sa != NULL) {
	if (strcmp(sa->serviceRoutine->name, func) != 0) {
	    /* service already advertised with another service routine */
	    tperrno = TPEMATCH; /* see tpadvertise(3c) */
	    return -1;
	} else {
	    /* service already advertised with the same service routine */
	    return 0;
	}
    } else {
	ServiceRoutine *sr = getServiceRoutine(func);
	if (sr == NULL) {
	    /* unknown service routine */
	    return tux.tpadvertise(svc, tpinvalid);
	} else {
	    char *err = NULL;
	    sa = addServiceAdvertisement(svc, sr, &err);
	    if (sa == NULL) {
		OTPUserlog("ERROR: %s", err);
		free(err);
		tperrno = TPESYSTEM;
		return -1;
	    }
	    if (tux.tpadvertise(svc, server.tpservice) == -1) {
		deleteServiceAdvertisement(sa);
		return -1;
	    } else {
		return 0;
	    }
	}
    }
}

OTP_FUNC_DEF
int OTPTuxServerUnadvertise(char *svc)
{
    ServiceAdvertisement *sa;

    if (svc == NULL) {
	tperrno = TPEINVAL;
	return -1;
    }

    sa = getServiceAdvertisement(svc);
    if (sa == NULL) {
	tperrno = TPENOENT; /* see tpunadvertise(3c) */
	return -1;
    } else {
        if (tux.tpunadvertise(svc) == -1) {
	    return -1;
        } else {
            deleteServiceAdvertisement(sa);
            return 0;
	}
    }
}

/*-----------------*/
/* Service routine */
/*-----------------*/

#define UNTERMINATED 0
#define RETURNED 1
#define FORWARDED 2

/*
 * The svc field cannot be dynamically allocated because there would be
 * no way to free it after calling tpforward(). As a result, the svc
 * field must have a static size and therefore, service names are bounded.
 */

typedef struct {
    int status; 
    int rval;
    long rcode;
    char svc[MAXTUXSVCLEN + 1];
    char *data;
    long len;
    long flags;
} ServiceTerminationInfo;

OTP_FUNC_DEF
void OTPTuxServerDispatch(TPSVCINFO *svcInfo)
{
    char *err = NULL;

    ServiceTerminationInfo termInfo;

    ServiceAdvertisement *sa = getServiceAdvertisement(svcInfo->name);
    if (sa == NULL) { /* This should never happen */
	OTPUserlog("WARNING: Dropping service request "
	   "for unknown service '%s'", svcInfo->name);
	return; /* => TPESVCERR */
    }

    if (OTPThreadVarSet(serviceThreadData, &termInfo, &err) == -1) {
	OTPUserlog("ERROR: %s", err);
	free(err);
        tux.tpreturn(TPEXIT, 0, NULL, 0, 0);
	return; /* Needed for test mode */
    }

    termInfo.status = UNTERMINATED;

    server.dispatch(svcInfo, sa->serviceRoutine->dispatchInfo);

    OTPThreadVarSet(serviceThreadData, NULL, NULL);

    switch (termInfo.status) {
	case RETURNED:
	    tux.tpreturn(termInfo.rval, termInfo.rcode, termInfo.data, 
	        termInfo.len, termInfo.flags);
	    return; /* Needed for test mode */
	case FORWARDED:
	    tux.tpforward(termInfo.svc, termInfo.data, termInfo.len, 
	        termInfo.flags);
	    return; /* Needed for test mode */
	default:
	    return; /* => TPESVCERR */
    }
}

/*---------------------*/
/* Service termination */
/*---------------------*/

OTP_FUNC_DEF
int OTPTuxServerReturn(int rval, long rcode, char *data, long len,
    long flags)
{
    char *err = NULL;

    ServiceTerminationInfo *termInfo;

    if (OTPThreadVarGet(serviceThreadData, (void **) &termInfo, &err) == -1) {
	OTPUserlog("ERROR: Error getting ServiceTerminationInfo "
	    "in OTPTuxServerReturn(): %s", err);
	free(err);
	tperrno = TPESYSTEM;
	return -1;
    }

    if ((termInfo == NULL) || (termInfo->status != UNTERMINATED)) {
	tperrno = TPEPROTO;
	return -1;
    }

    termInfo->status = RETURNED;
    termInfo->rval = rval;
    termInfo->rcode = rcode;
    termInfo->data = data;
    termInfo->len = len;
    termInfo->flags = flags;

    return 0;
}

OTP_FUNC_DEF
int OTPTuxServerForward(char *svc, char *data, long len, long flags)
{
    char *err = NULL;

    ServiceTerminationInfo *termInfo;

    if (svc == NULL) {
	tperrno = TPEINVAL;
	return -1;
    }

    if (OTPThreadVarGet(serviceThreadData, (void **) &termInfo, &err) == -1) {
	OTPUserlog("ERROR: Error getting ServiceTerminationInfo "
	    "in OTPTuxServerForward(): %s", err);
	free(err);
	tperrno = TPESYSTEM;
	return -1;
    }

    if ((termInfo == NULL) || (termInfo->status != UNTERMINATED)) {
	tperrno = TPEPROTO;
	return -1;
    }

    termInfo->status = FORWARDED;
    strncpy(termInfo->svc, svc, MAXTUXSVCLEN);
    termInfo->svc[MAXTUXSVCLEN] = '\0';
    termInfo->data = data;
    termInfo->len = len;
    termInfo->flags = flags;

    return 0;
}
