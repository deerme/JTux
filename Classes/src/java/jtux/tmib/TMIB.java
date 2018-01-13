/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.tmib;

/**
 * Defines symbolic constants for interacting with the Tuxedo MIB.
 */

public class TMIB
{
    private TMIB() { /* Nothing to do, just make sure it is not public */ }

    // The following symbol table was generated using OTPTuxSymbolDumper

    /** TMIB field identifier */
    public final static int TA_ATTFLAGS = 33560884;

    /** TMIB field identifier */
    public final static int TA_ATTRIBUTE = 33560432;

    /** TMIB field identifier */
    public final static int TA_BADFLD = 33560433;

    /** TMIB field identifier */
    public final static int TA_CLASS = 167778162;

    /** TMIB field identifier */
    public final static int TA_CLASSNAME = 167778163;

    /** TMIB field identifier */
    public final static int TA_CURSOR = 167778164;

    /** TMIB field identifier */
    public final static int TA_CURSORHOLD = 33560437;

    /** TMIB field identifier */
    public final static int TA_DEFAULT = 167778615;

    /** TMIB field identifier */
    public final static int TA_ERROR = 33560438;

    /** TMIB field identifier */
    public final static int TA_EVENT_DESCRIPTION = 167779165;

    /** TMIB field identifier */
    public final static int TA_EVENT_LMID = 167779162;

    /** TMIB field identifier */
    public final static int TA_EVENT_NAME = 167779160;

    /** TMIB field identifier */
    public final static int TA_EVENT_SEVERITY = 167779161;

    /** TMIB field identifier */
    public final static int TA_EVENT_TIME = 33561435;

    /** TMIB field identifier */
    public final static int TA_EVENT_USEC = 33561436;

    /** TMIB field identifier */
    public final static int TA_FACTPERM = 33560888;

    /** TMIB field identifier */
    public final static int TA_FILTER = 33560439;

    /** TMIB field identifier */
    public final static int TA_FLAGS = 33560440;

    /** TMIB field identifier */
    public final static int TA_GETSTATES = 167778617;

    /** TMIB field identifier */
    public final static int TA_INASTATES = 167778623;

    /** TMIB field identifier */
    public final static int TA_MAXPERM = 33560901;

    /** TMIB field identifier */
    public final static int TA_MIBTIMEOUT = 33560441;

    /** TMIB field identifier */
    public final static int TA_MORE = 33560442;

    /** TMIB field identifier */
    public final static int TA_OCCURS = 33560443;

    /** TMIB field identifier */
    public final static int TA_OPERATION = 167778172;

    /** TMIB field identifier */
    public final static int TA_PERM = 33560445;

    /** TMIB field identifier */
    public final static int TA_SETSTATES = 167778639;

    /** TMIB field identifier */
    public final static int TA_STATE = 167778174;

    /** TMIB field identifier */
    public final static int TA_STATUS = 167778175;

    /** TMIB field identifier */
    public final static int TA_ULOGCAT = 167778588;

    /** TMIB field identifier */
    public final static int TA_ULOGMSGNUM = 33560863;

    /** TMIB field identifier */
    public final static int TA_VALIDATION = 167778597;

    /** TMIB status code */
    public final static int TAOK = 0;

    /** TMIB status code */
    public final static int TAUPDATED = 1;

    /** TMIB status code */
    public final static int TAPARTIAL = 2;

    /** TMIB flag */
    public final static int MIB_LOCAL = 0x10000;

    /** TMIB flag */
    public final static int MIB_PREIMAGE = 0x1;

    /** TMIB flag */
    public final static int MIB_SELF = 0x100000;

    /** TMIB error code */
    public final static int TAEAPP = -1;

    /** TMIB error code */
    public final static int TAECONFIG = -2;

    /** TMIB error code */
    public final static int TAEINVAL = -3;

    /** TMIB error code */
    public final static int TAEOS = -4;

    /** TMIB error code */
    public final static int TAEPERM = -5;

    /** TMIB error code */
    public final static int TAEPREIMAGE = -6;

    /** TMIB error code */
    public final static int TAEPROTO = -7;

    /** TMIB error code */
    public final static int TAEREQUIRED = -8;

    /** TMIB error code */
    public final static int TAESUPPORT = -9;

    /** TMIB error code */
    public final static int TAESYSTEM = -10;

    /** TMIB error code */
    public final static int TAEUNIQ = -11;

    /** TM_MIB flag */
    public final static int TMIB_ADMONLY = 0x40000;

    /** TM_MIB flag */
    public final static int TMIB_APPONLY = 0x200000;

    /** TM_MIB flag */
    public final static int TMIB_CONFIG = 0x80000;

    /** TM_MIB flag */
    public final static int TMIB_NOTIFY = 0x20000;

    /** APPQ_MIB flag */
    public final static int QMIB_FORCECLOSE = 0x20000;

    /** APPQ_MIB flag */
    public final static int QMIB_FORCEDELETE = 0x80000;

    /** APPQ_MIB flag */
    public final static int QMIB_FORCEPURGE = 0x40000;

    /** TMIB attribute flag */
    public final static int MIBATT_KEYFIELD = 0x1;

    /** TMIB attribute flag */
    public final static int MIBATT_LOCAL = 0x2;

    /** TMIB attribute flag */
    public final static int MIBATT_REGEXKEY = 0x4;

    /** TMIB attribute flag */
    public final static int MIBATT_REQUIRED = 0x8;

    /** TMIB attribute flag */
    public final static int MIBATT_SETKEY = 0x10;

    /** TMIB attribute flag */
    public final static int MIBATT_NEWONLY = 0x20;
}
