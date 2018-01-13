# Copyright 2004 OTP Systems Oy. All rights reserved.

BASE_DIR := ..

default: all

include $(BASE_DIR)/Makefile.include

all: $(JTuxDevJar) $(JTuxJar) $(JTuxWSJar)

src_dirs := jtux jtux/holders jtux/atmi jtux/fml32 jtux/tmib jtux/xa jtux/jdbc jtux/jms jtux/jms/v10 jtux/install

src_files := $(foreach src_dir,$(src_dirs),$(wildcard src/java/$(src_dir)/*.java))

$(JTuxDevJar) $(JTuxJar) $(JTuxWSJar): $(src_files) 
	-mkdir build
	javac -classpath ../Redistribute/Sun/JMSClasses/javax.jms.jar -d build $(src_files)
	cd build && jar -cf $(JTuxDevJar) jtux/*.class jtux/atmi jtux/holders jtux/xa
	cd build && jar -cf $(JTuxJar) -C ../src/java jtux/JTux.tag jtux
	cd build && jar -cf $(JTuxWSJar) -C ../src/java jtux/JTuxWS.tag jtux/*.class jtux/holders jtux/atmi jtux/fml32 jtux/install/TestClient.class

clean:
	rm -rf build
