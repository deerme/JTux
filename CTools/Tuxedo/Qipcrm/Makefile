# Copyright 2004 OTP Systems Oy. All rights reserved.

include ../../Makefiles/Makefile.$(PLATFORM)

include ../Makefiles/Makefile.Tuxedo

Qipcrm := Qipcrm$(ExeSuffix)

build/$(Qipcrm): src/c/Qipcrm.c
	-mkdir build
	cd build && buildclient -v -o $(Qipcrm) -f "../src/c/Qipcrm.c $(libtmib)"

clean:
	rm -rf build
