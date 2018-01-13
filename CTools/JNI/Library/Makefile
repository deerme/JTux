# Copyright 2004 OTP Systems Oy. All rights reserved.

include ../../Makefiles/Makefile.$(PLATFORM)

include ../Makefiles/Makefile.$(JDK)

CSources := $(wildcard src/c/*.c)

Library := $(LibPrefix)OTPJNI$(LibSuffix)

build/$(Library): $(CSources)
	-mkdir build
	cd build && $(call Compile,-I../src/c -I../../../Platform/Library/src/c $(JNI_INCLUDES),$(foreach C,$(CSources),../$(C)))
	cd build && $(call MakeLib,$(Library),*$(ObjSuffix))

clean:
	rm -rf build
