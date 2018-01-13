# Copyright 2004 OTP Systems Oy. All rights reserved.

BASE_DIR := ..

include $(BASE_DIR)/Makefile.include

all:
	$(MAKE) -C JTux
	$(MAKE) -C JTuxWS

clean:
	$(MAKE) -C JTux clean
	$(MAKE) -C JTuxWS clean

packages:
	$(MAKE) -C JTux package
	$(MAKE) -C JTuxWS package

upload: packages
	$(upload) JTux/build/*.tar.gz JTuxWS/build/*.tar.gz $(upload_target)
