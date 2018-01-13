# Copyright 2004 OTP Systems Oy. All rights reserved.

BASE_DIR := .

default: bin

include $(BASE_DIR)/Makefile.include

# See GNU Make section 4.6 "Phony Targets" for more information about the
# build strategy below.

BINDIRS := CTools/Platform/Library \
	   CTools/Tuxedo/Library \
	   CTools/Tuxedo/Qipcrm \
	   CTools/JNI/Library \
	   JavaTools/Java2C \
	   Classes \
	   Library \
	   XASwitch \
	   Interop \
	   JTMS \
	   JClient \
	   JServer \
	   Installer \
	   Packages \
	   Samples

DOCDIRS := DocTools \
	   Docs

SUBDIRS := $(BINDIRS) $(DOCDIRS)

CLEAN_SUBDIRS := $(foreach SUBDIR,$(SUBDIRS),$(SUBDIR).clean)

.PHONY: $(SUBDIRS) $(CLEAN_SUBDIRS)

# Main build targets

all: bin doc

bin: $(BINDIRS)

doc: $(DOCDIRS)

$(SUBDIRS):
	$(MAKE) -C $@

# Dependency declarations (needed for parallel and isolated builds)

Library: JavaTools/Java2C Classes

Libraries := Library CTools/JNI/Library CTools/Tuxedo/Library CTools/Platform/Library

Interop: XASwitch $(Libraries)

JTMS: XASwitch $(Libraries)

JClient: $(Libraries)

JServer: XASwitch $(Libraries)

Packages: Classes Interop JTMS JClient JServer CTools/Tuxedo/Qipcrm Installer

Samples: Packages

# Cleanup

clean: $(CLEAN_SUBDIRS)
	rm -rf build

$(CLEAN_SUBDIRS):
	$(MAKE) -C $(basename $@) clean

test:
	$(MAKE) -C Samples test

empty :=
space := $(empty) $(empty)

# Note that the driverType property is required. Otherwise, you get a
# rather uninformative 'invalid URL' property.

test_jdbc_oracle:
	$(MAKE) -C Samples JDBC_CONNECT_STRING=XADataSourceClass=oracle.jdbc.xa.client.OracleXADataSource,driverType=thin,serverName=10.0.0.5,portNumber=1521,databaseName=sales,user=scott,password=tiger JDBC_CLASSPATH=$(OracleJDBCJar) test_jdbc

test_jdbc_oracle_dd:
	$(MAKE) -C Samples JDBC_CONNECT_STRING=XADataSourceClass=com.ddtek.jdbcx.oracle.OracleDataSource,serverName=10.0.0.5,portNumber=1521,SID=sales,user=scott,password=tiger JDBC_CLASSPATH=$(subst $(space),$(PS),$(ConnectJDBCJars)) test_jdbc

test_jdbc_oracle_oranxo:
	$(MAKE) -C Samples JDBC_CONNECT_STRING=XADataSourceClass=com.inet.ora.XDataSource,serverName=10.0.0.5,portNumber=1521,serviceName=sales,user=scott,password=tiger JDBC_CLASSPATH=$(OranxoJar) test_jdbc

test_jdbc_db2:
	$(MAKE) -C Samples JDBC_CONNECT_STRING=XADataSourceClass=com.ibm.db2.jcc.DB2XADataSource,driverType=4,serverName=10.0.0.5,portNumber=50000,databaseName=sample,user=db2,password=db2 JDBC_CLASSPATH=db2jcc.jar test_jdbc

test_jdbc_db2_dd:
	$(MAKE) -C Samples JDBC_CONNECT_STRING=XADataSourceClass=com.ddtek.jdbcx.db2.DB2DataSource,serverName=10.0.0.5,portNumber=50000,databaseName=sample,user=db2,password=db2 JDBC_CLASSPATH=$(subst $(space),$(PS),$(ConnectJDBCJars)) test_jdbc

test_jms:
	$(MAKE) -C Samples JMS_CONNECT_STRING=XAConnectionFactoryClass=com.sun.messaging.XAConnectionFactory,propertySetter=setProperty,imqAddressList=mq://10.0.0.5:7676/jms JMS_CLASSPATH=$(subst $(space),$(PS),$(IMQClientJars)) test_jms
