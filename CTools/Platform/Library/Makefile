# Copyright 2004 OTP Systems Oy. All rights reserved.

include ../../Makefiles/Makefile.$(PLATFORM)

CSources := $(wildcard src/c/*.c)

Library := $(LibPrefix)OTPPlatform$(LibSuffix)

build/$(Library): $(CSources)
	-mkdir build
	cd build && $(call Compile,-I../src/c,$(foreach C,$(CSources),../$(C)))
	cd build && $(call MakeLib,$(Library),*$(ObjSuffix))

clean:
	rm -rf build
