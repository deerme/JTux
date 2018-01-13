/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

#include <stdio.h>
#include <stdlib.h>
#include <atmi.h>
#include <tpadm.h>
#include <fml32.h>

int main(int argc, char *argv[])
{
    long flags;
    char *qmconfig;
    char *lmid;
    char *qspace;

    FBFR32 *fbfr;

    if (argc != 4) {
	fprintf(stderr, "Usage: Qipcrm <qmconfig> <lmid> <qspace>\n");
	exit(1);
    }

    qmconfig = argv[1];
    lmid = argv[2];
    qspace = argv[3];

    fbfr = (FBFR32 *) tpalloc("FML32", NULL, 4096);
    if (fbfr == NULL) {
	fprintf(stderr, "ERROR: tpalloc() failed: %s\n", tpstrerror(tperrno));
	exit(1);
    }

    flags = QMIB_FORCECLOSE;

    if ((Fadd32(fbfr, TA_OPERATION, "SET", 0) == -1)
	    || (Fadd32(fbfr, TA_CLASS, "T_APPQSPACE", 0) == -1) 
	    || (Fadd32(fbfr, TA_STATE, "CLEANING", 0) == -1)
	    || (Fadd32(fbfr, TA_FLAGS, (char *) &flags, 0) == -1)
	    || (Fadd32(fbfr, TA_QMCONFIG, qmconfig, 0) == -1)
       	    || (Fadd32(fbfr, TA_LMID, lmid, 0) == -1)
	    || (Fadd32(fbfr, TA_APPQSPACENAME, qspace, 0) == -1)) {
	fprintf(stderr, "ERROR: Fadd32() failed: %s\n", Fstrerror32(Ferror32));
	exit(1);
    }

    if (tpadmcall(fbfr, &fbfr, 0) == -1) {
	if (tperrno == TPEMIB) {
	    fprintf(stderr, "ERROR: %s\n", Fvals32(fbfr, TA_STATUS, 0));
	    exit(1);
	}
	fprintf(stderr, "ERROR: tpadmcall() failed: %s\n", tpstrerror(tperrno));
	exit(1);
    }

    printf("OK\n");

    exit(0);
}
