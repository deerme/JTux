# Copyright 2004 OTP Systems Oy. All rights reserved.

BASE_DIR := ..

default: samples

include $(BASE_DIR)/Makefile.include

JTUX=JTux-$(JTux_version)
JTUXWS=JTuxWS-$(JTux_version)

JTUXPKG=$(JTUX)-$(SHORT_PLATFORM)
JTUXWSPKG=$(JTUXWS)-$(SHORT_PLATFORM)

ifeq ($(PLATFORM),Windows)
JCLIENT := $(JTUX)\bin\JClient.exe
QIPCRM := $(JTUX)\bin\Qipcrm.exe
else
JCLIENT := $(JTUX)/bin/JClient
QIPCRM := $(JTUX)/bin/Qipcrm
endif

test: samples config boot run_samples shutdown

test_jdbc: samples jdbc_config boot run_jdbc_samples shutdown

test_jms: samples jms_config boot run_jms_samples shutdown

build/$(JTUX):
	$(JDKDIR)/bin/java -jar ../Packages/build/$(JTUXPKG)-install.jar -dir build

build/$(JTUXWS):
	$(JDKDIR)/bin/java -jar ../Packages/build/$(JTUXWSPKG)-install.jar -dir build

samples: build/$(JTUX) build/$(JTUXWS)
	cd build && javac -classpath $(JTUX)/lib/JTux.jar$(PS)$(JTUX)/lib/javax.jms.jar -d . ../src/java/*.java

export TUXCONFIG := $(CurrentDir)/build/TUXCONFIG

config:
	cd build && $(JCLIENT) -classpath . SampleConfig

jdbc_config:
	cd build && echo CLASSPATH=.$(PS)$(JDBC_CLASSPATH) > JDBCGRP.env
	cd build && echo JTUX_DEBUG=XA >> JDBCGRP.env
	cd build && $(JCLIENT) -classpath . SampleConfig JDBC:$(JDBC_CONNECT_STRING)

jms_config:
	cd build && echo CLASSPATH=.$(PS)$(JMS_CLASSPATH) > JMSGRP.env
	cd build && echo JTUX_DEBUG=XA >> JMSGRP.env
	cd build && $(JCLIENT) -classpath . SampleConfig JMS:$(JMS_CONNECT_STRING)

boot:
ifeq ($(PLATFORM),Windows)
	set "PATH=$(TUXDIR)\bin;$(CurrentDir)\build\$(JTUX)\bin" && tmboot -y
else
	env PATH=$(TUXDIR)/bin:$(CurrentDir)/build/$(JTUX)/bin tmboot -y
endif

unloadcf:
	tmunloadcf

shutdown:
	tmshutdown -y
	-cd build && $(QIPCRM) QSPACE master QSPACE

run_samples: run_jclient_samples run_java_samples run_java_ws_samples

export FIELDTBLS32 := $(TUXDIR)/udataobj/tpadm

RUN_JCLIENT_SAMPLE := cd build && $(JCLIENT) -classpath .

ifdef IS_64_BIT_PLATFORM
DFLAG=-d64
endif

RUN_JAVA_SAMPLE := cd build && java $(DFLAG) -classpath $(JTUX)/lib/JTux.jar$(PS). -Djava.library.path=$(JTUX)/$(DLLDirectory)

RUN_JAVA_WS_SAMPLE := cd build && java $(DFLAG) -classpath $(JTUXWS)/lib/JTuxWS.jar$(PS). -Djava.library.path=$(JTUXWS)/$(DLLDirectory)

# Don't use "Hello, World!" as an argument because the exclamation mark
# may be interpreted by the shell on Unix. This is not so much a problem
# here in this Makefile but it is when the commands are typed interactively
# as described in the JTux Programmer's Guide.

run_jclient_samples:
	$(RUN_JCLIENT_SAMPLE) SimpleClient "Hello, World"
	$(RUN_JCLIENT_SAMPLE) SimpleAsyncClient "Hello, World"
	$(RUN_JCLIENT_SAMPLE) DownloadClient TUXCONFIG TUXCONFIG.downloaded
	$(RUN_JCLIENT_SAMPLE) SimpleEnqueuer QSPACE TOUPPER "Hello, World" TOUPPER_RET
	$(RUN_JCLIENT_SAMPLE) SimpleDequeuer QSPACE TOUPPER_RET
	$(RUN_JCLIENT_SAMPLE) UnsolClient NOTIFY_TOUPPER "Hello, World"
	$(RUN_JCLIENT_SAMPLE) UnsolClient BRDCST_TOUPPER "Hello, World"
	$(RUN_JCLIENT_SAMPLE) SimpleSubscriber TIME
	$(RUN_JCLIENT_SAMPLE) SimpleFML32Client TA_CLASS=t_domain TA_MODEL=shm TA_MASTER=master

run_java_samples:
	$(RUN_JAVA_SAMPLE) SimpleClient "Hello, World"
	$(RUN_JAVA_SAMPLE) SimpleAsyncClient "Hello, World"
	$(RUN_JAVA_SAMPLE) DownloadClient TUXCONFIG TUXCONFIG.downloaded
	$(RUN_JAVA_SAMPLE) SimpleEnqueuer QSPACE TOUPPER "Hello, World" TOUPPER_RET
	$(RUN_JAVA_SAMPLE) SimpleDequeuer QSPACE TOUPPER_RET
	$(RUN_JAVA_SAMPLE) UnsolClient NOTIFY_TOUPPER "Hello, World"
	$(RUN_JAVA_SAMPLE) UnsolClient BRDCST_TOUPPER "Hello, World"
	$(RUN_JAVA_SAMPLE) SimpleSubscriber TIME
	$(RUN_JAVA_SAMPLE) SimpleFML32Client TA_CLASS=t_domain TA_MODEL=shm TA_MASTER=master

export WSNADDR := //$(LocalHostName):5120

run_java_ws_samples:
	$(RUN_JAVA_WS_SAMPLE) SimpleClient "Hello, World"
	$(RUN_JAVA_WS_SAMPLE) SimpleAsyncClient "Hello, World"
	$(RUN_JAVA_WS_SAMPLE) DownloadClient TUXCONFIG TUXCONFIG.downloaded
	$(RUN_JAVA_WS_SAMPLE) SimpleEnqueuer QSPACE TOUPPER "Hello, World" TOUPPER_RET
	$(RUN_JAVA_WS_SAMPLE) SimpleDequeuer QSPACE TOUPPER_RET
	$(RUN_JAVA_WS_SAMPLE) UnsolClient NOTIFY_TOUPPER "Hello, World"
	$(RUN_JAVA_WS_SAMPLE) UnsolClient BRDCST_TOUPPER "Hello, World"
	$(RUN_JAVA_WS_SAMPLE) SimpleSubscriber TIME
	$(RUN_JAVA_WS_SAMPLE) SimpleFML32Client TA_CLASS=t_domain TA_MODEL=shm TA_MASTER=master

run_jdbc_samples:
	@$(RUN_JAVA_SAMPLE) JDBCClient find 20
	@$(RUN_JAVA_SAMPLE) JDBCClient move 20 HAWAII
	@$(RUN_JAVA_SAMPLE) SimpleDequeuer QSPACE DEPTMOVES
	@$(RUN_JAVA_SAMPLE) JDBCClient find 20
	@$(RUN_JAVA_SAMPLE) JDBCClient move 20 ANTARCTICA
	@$(RUN_JAVA_SAMPLE) SimpleDequeuer QSPACE DEPTMOVES
	@$(RUN_JAVA_SAMPLE) JDBCClient find 20
	@$(RUN_JAVA_SAMPLE) JDBCClient move 20 DALLAS
	@$(RUN_JAVA_SAMPLE) SimpleDequeuer QSPACE DEPTMOVES
	@$(RUN_JAVA_SAMPLE) JDBCClient find 20

run_jms_samples:
	$(RUN_JAVA_SAMPLE) JMSClient enqueue DEPTMOVES "Hello, World!"
	$(RUN_JAVA_SAMPLE) JMSClient dequeue DEPTMOVES

clean:
	rm -rf build
