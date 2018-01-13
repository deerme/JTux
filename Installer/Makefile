# Copyright 2004 OTP Systems Oy. All rights reserved.

BASE_DIR := ..

default: installer

include $(BASE_DIR)/Makefile.include

installer:
	-mkdir build
	javac -d build src/java/*.java

clean:
	rm -rf build

build/properties:
	cd build && echo Product=Test-1.0.0 > properties
	cd build && echo Platform=test >> properties

build/archive:
ifeq ($(PLATFORM),Windows)
	jar cfM0 build/archive Makefile src 
else
	tar cf build/archive Makefile src 
endif

build/TestInstall.jar: build/properties build/archive
	cp src/java/Install.mf build
	cd build && jar cmf Install.mf TestInstall.jar Install.class properties archive

test: build/TestInstall.jar
	$(JDKDIR)/bin/java -jar build/TestInstall.jar
