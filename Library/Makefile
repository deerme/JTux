# Copyright 2004 OTP Systems Oy. All rights reserved.

BASE_DIR := ..

default: all

include $(BASE_DIR)/Makefile.include

all: $(JTuxLibrary)

CSources := $(wildcard src/c/*.c)

J2CSources := $(wildcard src/j2c/*.j2c)

$(JTuxLibrary): $(J2CSources) $(CSources)
	-mkdir build
	$(Java2C) -d build $(J2CSources)
	cd build && $(call Compile,$(CFLAGS) -I../src/c -I.,$(foreach C,$(CSources),../$(C)) *.c)
	cd build && $(call MakeLib,$(JTuxLibrary),*$(ObjSuffix))

clean:
	rm -rf build
