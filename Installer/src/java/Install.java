/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

import java.io.*;

import java.util.*;

public class Install
{
    private static boolean isWindows = (File.separatorChar == '\\');

    private static File installDir = null;

    private static String product = null;

    private static String platform = null;

    private static File productDir = null;

    private static boolean productDirCreated = false;

    private static boolean rmdir(File dir)
    {
	File[] files = dir.listFiles();

	if (files != null) {
	    for (int i = 0; i < files.length; i++) {
		File file = files[i];
		if (file.isDirectory()) {
		    if (!rmdir(file)) {
			return false;
		    }
		} else {
		    if (!file.delete()) {
			return false;
		    }
		}
	    }
	}

	return dir.delete();
    }

    private static void error(String message)
    {
	System.err.println("ERROR: " + message);

	if (productDirCreated) {
	    if (!rmdir(productDir)) {
		System.err.println("Failed to clean up " + productDir
		    + ", please remove it manually.");
	    }
	}

	System.exit(1);
    }

    private static void configure(String[] args)
    {
	for (int i = 0; i < args.length; i++) {
	    String arg = args[i];
		if (arg.equalsIgnoreCase("-dir")) {
		if (installDir != null) {
		    error("Duplicate -dir option");
		}
		i++;
		if (i == args.length) {
		    error("Missing -dir argument");
		}
		installDir = new File(args[i]);
	    } else {
		error("Invalid command line option '" + arg + "'");
	    }
	}

	if (installDir == null) {
	    error("Missing -dir command line option");
	}
    }

    private static String getInstallProperty(Properties properties,
	String name)
    {
	String value = properties.getProperty(name);
	if (value == null) {
	    error("Install property " + name + " not set");
	}
	return value.trim();
    }

    private static void readProperties() throws IOException
    {
	InputStream is = Install.class.getResourceAsStream("properties");
	if (is == null) {
	    error("Install properties not found");
	}

	try {
	    Properties properties = new Properties();
	    properties.load(is);
	    product = getInstallProperty(properties, "Product");
	    platform = getInstallProperty(properties, "Platform");
	} finally {
	    is.close();
	}
    }

    private static void checkJavaVersion()
    {
	String[] version = System.getProperty("java.version").split("\\.");

	int majorVersion = Integer.parseInt(version[0]);
	int minorVersion = Integer.parseInt(version[1]);

	if (majorVersion > 1) {
	    return;
	}

	if ((majorVersion == 1) && (minorVersion >= 4)) {
	    return;
	}

	error(product + " requires Java version 1.4 or higher");
    }

    private static void prepareInstall() throws IOException
    {
	int lastDotIndex = product.lastIndexOf('.');
	if (lastDotIndex == -1) {
	    error("Invalid product " + product);
	}

	String productWithoutPatchLevel = product.substring(0, lastDotIndex);

	productDir = new File(installDir, product);

	if (productDir.exists()) {
	    error("" + productDir + " already exists");
	}

	if (!productDir.mkdirs()) {
	    error("Could not create directory " + productDir);
	}

	productDirCreated = true;
    }

    private static void createWin32Environment(PrintWriter out, 
	File jreDir) throws IOException
    {
	String vendorURL = System.getProperty("java.vendor.url");

	// On Windows, only JNI_LIBRARY needs to be set

	File jreBinDir = new File(jreDir, "bin");

	File jniLibrary = null;

	if (vendorURL.indexOf("sun.com") != -1) {
	    jniLibrary = new File(new File(jreBinDir, "server"), "jvm.dll");
	} else if (vendorURL.indexOf("bea.com") != -1) {
	    jniLibrary = new File(new File(jreBinDir, "jrockit"), "jvm.dll");
	} else {
	    error("Unsupported Java vendor URL: " + vendorURL);
	}

	if (!jniLibrary.exists()) {
	    // On Windows, even if PATH includes the bin directory of the
	    // full JDK, the installed client JRE in C:\Program Files may
	    // still take precedence => advice using full path to JDK
	    // java command instead.
	    error("JNI library " + jniLibrary + " does not exist. "
		+ " Try running this installer using the full path "
	        + " of the JDK's java executable.");
	}

	out.println("JNI_LIBRARY=" + jniLibrary);
    }

    private static void createLinux32Environment(PrintWriter out,
	File jreDir) throws IOException
    {
	String vendorURL = System.getProperty("java.vendor.url");

	File jreLibDir = new File(new File(jreDir, "lib"), "i386");

	File jniLibrary = null;

	if (vendorURL.indexOf("sun.com") != -1) {
	    jniLibrary = new File(new File(jreLibDir, "server"), "libjvm.so");
	} else if (vendorURL.indexOf("bea.com") != -1) {
	    jniLibrary = new File(new File(jreLibDir, "jrockit"), "libjvm.so");
	} else {
	    error("Unsupported Java vendor URL: " + vendorURL);
	}

	if (!jniLibrary.exists()) {
	    error("JNI library " + jniLibrary + " does not exist");
	}

	out.println("JNI_LIBRARY=" + jniLibrary);

	out.println("LD_LIBRARY_PATH=" + jniLibrary.getParent()
	    + ":" + new File(jreLibDir, "native_threads")
	    + ":" + jreLibDir + ":${LD_LIBRARY_PATH}");
    }

    private static void createSolEnvironment(PrintWriter out,
	File jreDir, String cpu) throws IOException
    {
	String vendorURL = System.getProperty("java.vendor.url");
	if (vendorURL.indexOf("sun.com") == -1) {
	    error("Unsupported Java vendor URL: " + vendorURL);
	}

	File jreLibDir = new File(new File(jreDir, "lib"), cpu);

	File jniLibrary = new File(new File(jreLibDir, "server"), "libjvm.so");

	if (!jniLibrary.exists()) {
	    error("JNI library " + jniLibrary + " does not exist");
	}

	out.println("JNI_LIBRARY=" + jniLibrary);

	out.println("LD_LIBRARY_PATH=" + jniLibrary.getParent()
	    + ":" + new File(jreLibDir, "native_threads")
	    + ":" + jreLibDir + ":${LD_LIBRARY_PATH}");
    }

    private static void createHpUxEnvironment(PrintWriter out,
	File jreDir, String cpu) throws IOException
    {
	String vendorURL = System.getProperty("java.vendor.url");
	if (vendorURL.indexOf("hp.com") == -1) {
	    error("Unsupported Java vendor URL: " + vendorURL);
	}

	File jreLibDir = new File(new File(jreDir, "lib"), cpu);

	File jniLibrary = new File(new File(jreLibDir, "server"), "libjvm.sl");

	if (!jniLibrary.exists()) {
	    error("JNI library " + jniLibrary + " does not exist");
	}

	out.println("JNI_LIBRARY=" + jniLibrary);

	out.println("SHLIB_PATH=" + jniLibrary.getParent()
	    + ":" + new File(jreLibDir, "native_threads")
	    + ":" + jreLibDir + ":${SHLIB_PATH}");

	out.println("LD_PRELOAD=${JNI_LIBRARY}:${LD_PRELOAD}");
    }

    private static void createTestEnvironment(PrintWriter out,
	File jreDir) throws IOException
    {
	out.println("JNI_LIBRARY=" + jreDir + File.separator + "...");
    }

    private static void createEnvironment(PrintWriter out, File jreDir)
	throws IOException
    {
	if (platform.equals("win32")) {
	    createWin32Environment(out, jreDir);
	} else if (platform.equals("linux32")) {
	    createLinux32Environment(out, jreDir);
	} else if (platform.equals("sol32")) {
	    createSolEnvironment(out, jreDir, "sparc");
	} else if (platform.equals("sol64")) {
	    createSolEnvironment(out, jreDir, "sparcv9");
	} else if (platform.equals("hpux32")) {
	    createHpUxEnvironment(out, jreDir, "PA_RISC");
	} else if (platform.equals("hpux64")) {
	    createHpUxEnvironment(out, jreDir, "PA_RISC2.0W");
	} else if (platform.equals("test")) {
	    createTestEnvironment(out, jreDir);
	} else {
	    error("Invalid platform: " + platform);
	}
    }

    private static void createEnvironment() throws IOException
    {
	FileWriter writer = new FileWriter(new File(productDir, "JTux.env"));
	try {
	    PrintWriter out = new PrintWriter(writer);
	    out.println("# Environment settings for " + product + "-"
		+ platform);
	    out.println();
	    createEnvironment(out, new File(System.getProperty("java.home")));
	} finally {
	    writer.close();
	}
    }

    private static void unpackArchive() throws IOException
    {
	InputStream is = Install.class.getResourceAsStream("archive");
	if (is == null) {
	    error("Install archive not found");
	}

	File tempFile = File.createTempFile("otp", null);
	try {
	    FileOutputStream fos = new FileOutputStream(tempFile);
	    try {
		byte[] buffer = new byte[64 * 1024];
		int bytesRead = is.read(buffer);
		while (bytesRead != -1) {
		    fos.write(buffer, 0, bytesRead);
		    bytesRead = is.read(buffer);
		}
	    } finally {
		fos.close();
	    }

	    // Must use tar on Unix to preserve permissions.

	    String command = (isWindows ? "jar" : "tar") + " xf " + tempFile;

	    Process unpack = Runtime.getRuntime().exec(command, null, 
		productDir);
	    try {
	        int result = unpack.waitFor();
		if (result != 0) {
		    error(command + " returned " + result);
		}
	    } catch (InterruptedException eInterrupted) {
		error("Interrupted");
	    }
	} finally {
	    tempFile.delete();
	}
    }

    public static void main(String[] args)
    {
	try {
	    configure(args);

	    readProperties();

	    checkJavaVersion();

	    prepareInstall();

	    if (!product.startsWith("JTuxWS-")) {
		createEnvironment();
	    }

	    unpackArchive();

	} catch (IOException eIO) {
	    error(eIO.getMessage());
	}
    }
}
