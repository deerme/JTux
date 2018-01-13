/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "OTPTuxConfig.c"

#ifndef OTP_FUNC_DEF
#define OTP_FUNC_DEF /* empty */
#endif

#include "OTPTuxConfig.h"
#include "OTPTuxError.h"
#include "OTPError.h"

#include <atmi.h>
#include <fml32.h>
#include <tpadm.h>
#include <string.h> /* memset() */
#include <stdlib.h> /* free() */

/* Utilities */

static int setStringField(FBFR32 *fbfr, int fldid, char *value,
    char **errorInfo)
{
    if (Fadd32(fbfr, fldid, value, 0) == -1) {
	SET_F32_ERROR("Fadd32");
	return -1;
    } else {
	return 0;
    }
}

static int setLongField(FBFR32 *fbfr, int fldid, long value,
    char **errorInfo)
{
    if (Fadd32(fbfr, fldid, (char *) &value, 0) == -1) {
	SET_F32_ERROR("Fadd32");
	return -1;
    } else {
	return 0;
    }
}

static int getStringField(FBFR32 *fbfr, int fldid, char **value,
    char **errorInfo)
{
    char *result = Fgetalloc32(fbfr, fldid, 0, NULL);
    if ((result == NULL) && (Ferror32 != FNOTPRES)) {
	SET_F32_ERROR("Fgetalloc32");
	return -1;
    }
    *value = result;
    return 0;
}

static int getLongField(FBFR32 *fbfr, int fldid, long *value,
    char **errorInfo)
{
    FLDLEN32 maxlen = sizeof(long);
    if (Fget32(fbfr, fldid, 0, (char *) value, &maxlen) == -1) {
        if (Ferror32 != FNOTPRES) {
	    SET_F32_ERROR("Fget32");
	    return -1;
	}
    }
    return 0;
}

#define SET_STRING_FIELD(FLDID, VALUE) \
    if (setStringField(fbfr, FLDID, VALUE, errorInfo) == -1) { \
	ADD_ERROR_CONTEXT("Error setting " #FLDID); \
	goto error_after_fbfr; \
    }

#define SET_LONG_FIELD(FLDID, VALUE) \
    if (setLongField(fbfr, FLDID, VALUE, errorInfo) == -1) { \
	ADD_ERROR_CONTEXT("Error setting " #FLDID); \
	goto error_after_fbfr; \
    }

#define GET_STRING_CONFIG_PARAM(PARAM) \
    if (getStringField(fbfr, TA_##PARAM, &private.PARAM, \
	    errorInfo) == -1) { \
	ADD_ERROR_CONTEXT("Error getting TA_" #PARAM); \
	goto error_after_private; \
    }

#define GET_LONG_CONFIG_PARAM(PARAM) \
    if (getLongField(fbfr, TA_##PARAM, &private.PARAM, \
	    errorInfo) == -1) { \
	ADD_ERROR_CONTEXT("Error getting TA_" #PARAM); \
	goto error_after_private; \
   }

/* T_SERVER */

OTP_FUNC_DEF
void OTPTuxServerConfigClear(OTPTuxServerConfig *config)
{
    /*
     * Both the Windows CRT reference and the Single UNIX Specification 
     * specify that free() should do nothing if the pointer argument is NULL.
     */

    free(config->SRVGRP);
    free(config->SERVERNAME);
    free(config->CLOPT);
    free(config->ENVFILE);
    free(config->RCMD);
    free(config->RESTART);
    free(config->SYSTEM_ACCESS);
    free(config->CONV);
    free(config->REPLYQ);
    free(config->RQADDR);
}

OTP_FUNC_DEF
int OTPTuxServerConfigSelf(OTPTuxServerConfig *config, char **errorInfo)
{
    OTPTuxServerConfig private;

    FBFR32 *fbfr = (FBFR32 *) tpalloc("FML32", NULL, 4096);
    if (fbfr == NULL) {
	SET_TP_ERROR("tpalloc");
	goto error;
    }

    SET_STRING_FIELD(TA_OPERATION, "GET");
    SET_STRING_FIELD(TA_CLASS, "T_SERVER");
    SET_LONG_FIELD(TA_FLAGS, MIB_SELF);

    if (tpadmcall(fbfr, &fbfr, 0) == -1) {
	SET_TP_ERROR("tpadmcall");
	goto error_after_fbfr;
    }

    memset(&private, 0, sizeof(OTPTuxServerConfig));

    GET_STRING_CONFIG_PARAM(SRVGRP);
    GET_STRING_CONFIG_PARAM(SERVERNAME);
    GET_STRING_CONFIG_PARAM(CLOPT);
    GET_STRING_CONFIG_PARAM(ENVFILE);
    GET_STRING_CONFIG_PARAM(RCMD);
    GET_STRING_CONFIG_PARAM(RESTART);
    GET_STRING_CONFIG_PARAM(SYSTEM_ACCESS);
    GET_STRING_CONFIG_PARAM(CONV);
    GET_STRING_CONFIG_PARAM(REPLYQ);
    GET_STRING_CONFIG_PARAM(RQADDR);

    GET_LONG_CONFIG_PARAM(SRVID);
    GET_LONG_CONFIG_PARAM(GRPNO);
    GET_LONG_CONFIG_PARAM(BASESRVID);
    GET_LONG_CONFIG_PARAM(GRACE);
    GET_LONG_CONFIG_PARAM(MAXGEN);
    GET_LONG_CONFIG_PARAM(MAX);
    GET_LONG_CONFIG_PARAM(MIN);
    GET_LONG_CONFIG_PARAM(MINDISPATCHTHREADS);
    GET_LONG_CONFIG_PARAM(MAXDISPATCHTHREADS);
    GET_LONG_CONFIG_PARAM(THREADSTACKSIZE);
    GET_LONG_CONFIG_PARAM(SEQUENCE);
    GET_LONG_CONFIG_PARAM(RPPERM);
    GET_LONG_CONFIG_PARAM(RQPERM);

    tpfree((char *) fbfr);

    memcpy(config, &private, sizeof(OTPTuxServerConfig));

    return 0;

error_after_private:

    OTPTuxServerConfigClear(&private);

error_after_fbfr:

    tpfree((char *) fbfr);

error:

    return -1;
}
