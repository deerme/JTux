# Copyright 2004 OTP Systems Oy. All rights reserved

BASE_DIR := ..

default: all

include $(BASE_DIR)/Makefile.include

all: $(JServer)

# On Windows, the command below will generate a warning about the locally
# defined symbol _JTux_xa_switch being imported in the function
# __tmgetsvrargs. This warning occurs because the server stub generated
# by buildserver expects the JTux_xa_switch to be in a DLL. However, to make
# it easier to run multiple versions of JTux in parallel (especially on Unix)
# we have deciced to statically link the JTux_xa_switch to the JServer. The
# warning appears to be harmless and can be ignored.

$(JServer): src/c/JServer.c
	-mkdir build
	cd build && buildserver -v -t -r JTux -s :tpservice -o $(JServer) -f "-DTMMAINEXIT ../src/c/JServer.c $(JTuxXASwitch) $(JTuxLibrary) $(OTPJNILibrary) $(OTPTuxedoLibrary) $(OTPPlatformLibrary) $(libtmib)"
ifneq ($(PLATFORM),Windows)
	strip $(JServer)
endif

clean:
	rm -rf build
