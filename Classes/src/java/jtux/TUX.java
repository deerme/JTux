/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.HashMap;

/**
 * Provides methods for writing to the Tuxedo ULOG and for getting
 * and setting environment variables.
 */
public class TUX
{
    static
    {
	if (JTuxInterop.isLibraryLoaded) {

	    // Prevent class from loading if ULOGPFX points to a
	    // non-existing directory. Otherwise, userlog() crashes.
	
	    String ulogpfx = JTuxInterop.tuxgetenv("ULOGPFX");
	    if (ulogpfx != null) {
	        File ulogdir = new File(ulogpfx).getParentFile();
	        if ((ulogdir == null) || !ulogdir.exists()) {
		    throw new IllegalStateException("ULOG directory "
		        + "does not exist (ULOGPFX = " + ulogpfx + ")");
	        }
	    }
        }
    }

    private static HashMap debugWriters = null;
        // Must put this *before* the static initialization block below,
	// otherwise debugWriters gets reset to null after setDebugWriters
	// has run!

    static
    {
	if (JTuxInterop.isLibraryLoaded) {
	    String debug = tuxgetenv("JTUX_DEBUG");
	    if (debug != null) {
	        userlog("INFO: JTUX_DEBUG=" + debug);
	        setDebugWriters(debug);
	    }
	}
    }

    TUX()
    {
       	// Nothing to do, just make sure it is not public.
    }

    /**
     * PrintWriter that writes to the Tuxedo ULOG.
     */
    public final static PrintWriter ULOG = new PrintWriter(
	new ULOGWriter(null), true);

    /**
     * Logs a message in the Tuxedo ULOG.
     *
     * @param message
     * The message to log.
     *
     * @remarks
     * Unlike Tuxedo's native userlog() function, the message passed to
     * this method may be of arbitrary length.
     */
    public static void userlog(String message)
    {
	JTuxInterop.userlog(message.getBytes());
    }

    private static void logStackTrace(Throwable e) // Used by native code
    {
	e.printStackTrace(ULOG);
    }

    /**
     * Returns the value of an environment variable.
     *
     * @param name
     * The name of the environment variable.
     *
     * @return
     * The value of the environment variable, or null if the environment
     * variable is not set.
     */
    public static String tuxgetenv(String name)
    {
	return JTuxInterop.tuxgetenv(name);
    }

    /**
     * Sets the value of an environment variable.
     *
     * @param setting
     * A string of the form <i>name</i>=<i>value</i>.
     */
    public static void tuxputenv(String setting)
    {
	if (JTuxInterop.tuxputenv(setting) != 0) {
	    throw new OutOfMemoryError("Failed to expand environment");
	}
    }

    /**
     * Reads environment variables from a file.
     *
     * @param file
     * The name of the file to read from.
     *
     * @param label
     * A label in the file that marks an additional set of environment
     * variables to read; may be null.
     *
     * @throws FileNotFoundException
     * The named file does not exist or is not a file.
     *
     * @throws IOException
     * The named file is not readable.
     */
    public static void tuxreadenv(String file, String label)
	throws FileNotFoundException, IOException
    {
	if (JTuxInterop.tuxreadenv(file, label) != 0) {
	    File f = new File(file);
	    if (!f.exists()) {
		throw new FileNotFoundException(file  + " does not exist");
	    }
	    if (!f.isFile()) {
		throw new FileNotFoundException(file + " is not a file");
	    }
	    if (!f.canRead()) {
		throw new IOException(file + " is not readable");
	    }
	    throw new RuntimeException("Failed to expand environment");
	}
    }

    /**
     * @internal
     */
    public static String tuxgetmbenc(int flags)
    {
	return tuxgetenv("TPMBENC");
    }

    /**
     * @internal
     */
    public static void tuxsetmbenc(String enc, int flags)
    {
	if (enc == null) {
	    tuxputenv("TPMBENC=");
	} else {
	    tuxputenv("TPMBENC=" + enc);
	}
    }

    /**
     * @internal
     */
    public static boolean tuxgetmbaconv(int flags)
    {
	return (tuxgetenv("TPMBACONV") != null);
    }

    private static class ULOGWriter extends Writer
    {
        private static ThreadLocal buffer = new ThreadLocal();

	private String tag;

	ULOGWriter(String tag)
	{
	    this.tag = tag;
	}

	private void writeLine(String line)
	{
	    if (tag == null) {
		TUX.userlog(line);
	    } else {
		TUX.userlog(tag + ": " + line);
	    }
	}

        public void close()
        {
	    flush();
        }

        public void flush()
        {
	    StringBuffer line = (StringBuffer) buffer.get();
	    if (line != null) {
	        writeLine(line.toString());
	        buffer.set(null);
	    }
	}

        public int findNewLine(char[] cbuf, int offset, int count)
        {
	    for (int i = offset; i < offset + count; i++) {
	        if (cbuf[i] == '\n') {
		    return i;
	        }
	    }
	    return -1;
        }

        public void write(char[] cbuf, int offset, int count)
        {
	    int end = offset + count;

	    StringBuffer line = (StringBuffer) buffer.get();

	    int newLinePos = findNewLine(cbuf, offset, count);
	    while (newLinePos != -1) {
	        int partLength = newLinePos - offset;
	        if ((newLinePos > offset) && (cbuf[newLinePos-1] == '\r')) {
		    partLength--;
	        }
	        if (line != null) {
		    line.append(cbuf, offset, partLength);
		    writeLine(line.toString());
		    line = null;
	        } else {
		    writeLine(new String(cbuf, offset, partLength));
	        }
	        offset = newLinePos + 1;
	        count = end - offset;
	        newLinePos = findNewLine(cbuf, offset, count);
	    }

	    if (count > 0) {
	        if (line == null) {
		    line = new StringBuffer();
	        }
	        line.append(cbuf, offset, count);
	    }

	    buffer.set(line);
        }
    }

    private static void setDebugWriters(String subsystemIDs)
    {
	if (subsystemIDs == null) {
	    debugWriters = null; // Turn global debug off
	} else {
	    debugWriters = new HashMap(); // Turn global debug on and reset
	    String[] subsystemIDArray = subsystemIDs.split(",");
	    for (int i = 0; i < subsystemIDArray.length; i++) {
		String key = subsystemIDArray[i].trim().toUpperCase();
		debugWriters.put(key, null); // Debug on but not used yet
	    }
	}
    }

    /**
     * @internal
     */
    public static PrintWriter getDebugWriter(String subsystemID)
    {
	if (debugWriters == null) { // Debug is off
	    return null;
	}

	String key = subsystemID.toUpperCase();

	if (debugWriters.containsKey(key)) { // Debug is on...
	    PrintWriter debugWriter = (PrintWriter) debugWriters.get(key);
	    if (debugWriter == null) { // ... but not used yet
		String debugTag = "DEBUG [" + key + "]";
		debugWriter = new PrintWriter(new ULOGWriter(debugTag), true);
		debugWriters.put(key, debugWriter);
	    }
	    return debugWriter;
	} else { // Debug is off for this subsystem
	    return null;
	}
    }
}
