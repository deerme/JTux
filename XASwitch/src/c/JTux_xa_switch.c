/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#define OTP__FILE__ "JTux_xa_switch.c"

#include "JTuxJTA.h"

#include "OTPUserlog.h"

#include <stdlib.h>

static int xa_open(char *xa_info, int rmid, long flags)
{
    char *err = NULL;

    int xaResult;

    if (JTuxJTAOpen(xa_info, &xaResult, &err) == -1) {
	OTPUserlog("ERROR: %s", err);
	free(err);
	return XAER_RMERR;
    }

    return xaResult;
}

static int xa_close(char *xa_info, int rmid, long flags)
{
    char *err = NULL;

    int xaResult;

    if (JTuxJTAClose(&xaResult, &err) == -1) {
	OTPUserlog("ERROR: %s", err);
	free(err);
	return XAER_RMERR;
    }

    return xaResult;
}

typedef int (*XAXidFunction)(XID *xid, long flags, int *xaResult, 
    char **errorInfo);

static int callXAXidFunction(XID *xid, long flags, XAXidFunction func)
{
    char *err = NULL;

    int xaResult;

    if ((*func)(xid, flags, &xaResult, &err) == -1) {
	OTPUserlog("ERROR: %s", err);
	free(err);
	return XAER_RMERR;
    }

    return xaResult;
}

static int xa_start(XID *xid, int rmid, long flags)
{
    return callXAXidFunction(xid, flags, JTuxJTAStart);
}

static int xa_end(XID *xid, int rmid, long flags)
{
    return callXAXidFunction(xid, flags, JTuxJTAEnd);
}

static int xa_prepare(XID *xid, int rmid, long flags)
{
    return callXAXidFunction(xid, flags, JTuxJTAPrepare);
}

static int xa_commit(XID *xid, int rmid, long flags)
{
    return callXAXidFunction(xid, flags, JTuxJTACommit);
}

static int xa_rollback(XID *xid, int rmid, long flags)
{
    return callXAXidFunction(xid, flags, JTuxJTARollback);
}

static int xa_forget(XID *xid, int rmid, long flags)
{
    return callXAXidFunction(xid, flags, JTuxJTAForget);
}

static int xa_recover(XID *xids, long count, int rmid, long flags)
{
    char *err = NULL;

    int xaResult;

    if (JTuxJTARecover(xids, count, flags, &xaResult, &err) == -1) {
	OTPUserlog("ERROR: %s", err);
	free(err);
	return XAER_RMERR;
    }

    return xaResult;
}

static int xa_complete(int *handle, int *retval, int rmid, long flags)
{
    return XAER_PROTO; /* No support for asynchronous operations in JTA */
}

struct xa_switch_t JTux_xa_switch = {
    "JTux",
    TMNOMIGRATE,
    0,
    xa_open,
    xa_close,
    xa_start,
    xa_end,
    xa_rollback,
    xa_prepare,
    xa_commit,
    xa_recover,
    xa_forget,
    xa_complete
};
