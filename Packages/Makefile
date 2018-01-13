# Copyright 2004 OTP Systems Oy. All rights reserved.

BASE_DIR := ..

default: all

include $(BASE_DIR)/Makefile.include

JTUX=JTux-$(JTux_version)
JTUXPKG=$(JTUX)-$(SHORT_PLATFORM)

JTUXWS=JTuxWS-$(JTux_version)
JTUXWSPKG=$(JTUXWS)-$(SHORT_PLATFORM)

all: JTux JTuxWS

JTux:
	-mkdir build
	-cd build && mkdir JTux
	-cd build/JTux && mkdir bin
	-cd build/JTux && mkdir lib
	cp $(JTuxJar) build/JTux/lib
	cp $(JTuxInteropDLL) build/JTux/$(DLLDirectory)
	cp $(JClient) build/JTux/bin
	cp $(JServer) build/JTux/bin
	cp $(JTMS) build/JTux/bin
	cp $(Qipcrm) build/JTux/bin
	cp $(JMSJar) build/JTux/lib
ifeq ($(PLATFORM),Windows)
	cp $(MSVCR71DLL) build/JTux/bin
	attrib -r build/JTux/bin/*.dll
	cd build/JTux && jar cfM0 archive bin lib
else
#	cd $(JTUXDIR)/bin && $(strip) *
#	cd $(JTUXDIR)/lib && $(strip) *$(DllSuffix)
	cd build/JTux && tar cf archive bin lib
endif
	cd build/JTux && echo Product=$(JTUX) > properties
	cd build/JTux && echo Platform=$(SHORT_PLATFORM) >> properties
	cp ../Installer/src/java/Install.mf build/JTux
	cp ../Installer/build/Install.class build/JTux
	cd build/JTux && jar cmf Install.mf ../$(JTUXPKG)-install.jar Install.class properties archive

JTuxWS:
	-mkdir build
	-cd build && mkdir JTuxWS
	-cd build/JTuxWS && mkdir bin
	-cd build/JTuxWS && mkdir lib
	cp $(JTuxWSJar) build/JTuxWS/lib
	cp $(JTuxWSInteropDLL) build/JTuxWS/$(DLLDirectory)
ifeq ($(PLATFORM),Windows)
	cp $(MSVCR71DLL) build/JTuxWS/bin
	attrib -r build/JTuxWS/bin/*.dll
	cd build/JTuxWS && jar cfM0 archive bin lib
else
#	cd $(JTUXWSDIR)/lib && $(strip) *$(DllSuffix)
	cd build/JTuxWS && tar cf archive bin lib
endif
	cd build/JTuxWS && echo Product=$(JTUXWS) > properties
	cd build/JTuxWS && echo Platform=$(SHORT_PLATFORM) >> properties
	cp ../Installer/src/java/Install.mf build/JTuxWS
	cp ../Installer/build/Install.class build/JTuxWS
	cd build/JTuxWS && jar cmf Install.mf ../$(JTUXWSPKG)-install.jar Install.class properties archive

clean:
	rm -rf build

upload:
	cd build && $(upload) $(JTUXPKG)-install.jar $(JTUXWSPKG)-install.jar $(upload_target)
