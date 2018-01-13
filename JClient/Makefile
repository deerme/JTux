# Copyright 2004 OTP Systems Oy. All rights reserved

BASE_DIR := ..

default: all

include $(BASE_DIR)/Makefile.include

all: $(JClient)

$(JClient): src/c/JClient.c
	-mkdir build
	cd build && buildclient -v -o $(JClient) -f "../src/c/JClient.c $(JTuxLibrary) $(OTPJNILibrary) $(OTPTuxedoLibrary) $(OTPPlatformLibrary)"
ifneq ($(PLATFORM),Windows)
	strip $(JClient)
endif

clean:
	rm -rf build
