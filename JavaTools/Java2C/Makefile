# Copyright 2004 OTP Systems Oy. All rights reserved.

all: build/Java2C.jar

build/Java2C.jar: build
	javac -d build src/java/*.java
	jar -cvf build/Java2C.jar -C build com/otpsystems/tools/jni

build:
	mkdir build

clean:
	rm -rf build

ifeq ($(PLATFORM),Windows)
CC := cl -c
includes := -I$(JDKDIR)/include -I$(JDKDIR)/include/win32
else
CC := cc -c
includes := -I$(JDKDIR)/include -I$(JDKDIR)/include/linux
endif

test: build/test
	java -classpath build/Java2C.jar com.otpsystems.tools.jni.Java2C -d build/test src/j2c/test.j2c
	cd build/test && $(CC) $(includes) *.c

build/test:
	cd build && mkdir test
