# Copyright 2004 OTP Systems Oy. All rights reserved.

all: build/SourceFormat.exe

cs := $(subst /,\\,$(wildcard src/cs/*.cs))

build/SourceFormat.exe: $(cs)
	-mkdir build
	csc /out:build\SourceFormat.exe $(cs)

test:
	echo class ABC | build\SourceFormat.exe CSharp

clean:
	rm -rf build
