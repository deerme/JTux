# Copyright 2004 OTP Systems Oy. All rights reserved.

include ../../Makefiles/Makefile.$(PLATFORM)

CSources := $(wildcard src/c/*.c)

Library := $(LibPrefix)OTPTuxedo$(LibSuffix)

build/$(Library): $(CSources)
	-mkdir build
	cd build && $(call Compile,-I../src/c -I../../../Platform/Library/src/c -I$(TUXDIR)/include,$(foreach C,$(CSources),../$(C)))
	cd build && $(call MakeLib,$(Library),*$(ObjSuffix))

clean:
	rm -rf build
