/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.install;

import jtux.TUX;

import java.net.InetAddress;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class TestConfig
{
    private static boolean isWindows = (File.separatorChar == '\\');

    public static void main(String[] args) throws IOException
    {
	String TUXDIR = TUX.tuxgetenv("TUXDIR");
	if (TUXDIR == null) {
	    System.err.println("ERROR: Environment variable TUXDIR not set");
	    System.exit(1);
	}

	String userDir = System.getProperty("user.dir");
	if (userDir == null) {
	    System.err.println("ERROR: System property user.dir not set");
	    System.exit(1);
	}

	File APPDIR = new File(userDir);

	File TUXCONFIG = new File(APPDIR, "TUXCONFIG");

	File UBBCONFIG = new File(APPDIR, "test.ubb");

	System.out.println("Creating " + UBBCONFIG);

	InetAddress localHost = InetAddress.getLocalHost();
	String hostName = localHost.getHostName();
	if (isWindows) {
	    hostName = hostName.toUpperCase();
	}

	FileWriter ubbWriter = new FileWriter(UBBCONFIG);
	try {
	    PrintWriter out = new PrintWriter(ubbWriter);
	    out.println("*RESOURCES");
	    out.println();
	    out.println("IPCKEY\t" + 0xCAFE);
	    out.println("MODEL\tSHM");
	    out.println("MASTER\tmachine");
	    out.println();
	    out.println("*MACHINES");
	    out.println();
	    // Must put quotes around hostName to protect against host names
	    // containing dots (as in localhost.localdomain)
	    out.println("\"" + hostName + "\"");
	    out.println("\tLMID=machine");
	    out.println("\tTUXDIR=\"" + TUXDIR + "\"");
	    out.println("\tAPPDIR=\"" + APPDIR + "\"");
	    out.println("\tTUXCONFIG=\"" + TUXCONFIG + "\"");
	    out.println();
	    out.println("*GROUPS");
	    out.println();
	    out.println("TESTGRP");
	    out.println("\tLMID=machine");
	    out.println("\tGRPNO=1");
	    out.println();
	    out.println("*SERVERS");
	    out.println();
	    out.println("JServer");
	    out.println("\tSRVGRP=TESTGRP");
	    out.println("\tSRVID=1");
	    out.println("\tCLOPT=\"-- -s TEST "
		+ TestServer.class.getName() + "\"");
	    out.println();
	    out.println("*SERVICES");
	} finally {
	    ubbWriter.close();
	}
    }
}
