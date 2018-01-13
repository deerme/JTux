/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package com.otpsystems.tools.jni;

import java.io.*;

public class CodeWriter extends Writer
{
    private Writer out;

    private int level;

    private boolean atBOL;

    private boolean skipEmptyLines;

    public CodeWriter(Writer out)
    {
	this.out = out;

	level = 0;

	atBOL = true;

	skipEmptyLines = true;
    }

    public void close() throws IOException
    {
	out.close();
    }

    public void flush() throws IOException
    {
	out.flush();
    }

    public void write(int ch) throws IOException
    {
	if (ch == '\r') {
	    if (!skipEmptyLines) {
	        out.write(ch);
	    }
	    return;
	}

	if (ch == '\n') {
	    if (!skipEmptyLines) {
	        out.write(ch);
	    }
	    if (atBOL) {
		skipEmptyLines = true;
	    } else {
	        atBOL = true;
	    }
	    return;
	}

	if (atBOL && ((ch == ' ') || (ch == '\t'))) {
	    return;
	}

	if ((ch == '}') || (ch == ')')) {
	    level--;
	}

	if (atBOL) {
	    for (int i = 0; i < level; i++) {
		out.write("    ");
	    }
	    atBOL = false;
	}

	out.write(ch);

	if ((ch == '{') || (ch == '(')) {
	    level++;
	}

	skipEmptyLines = false;
    }

    public void write(char[] cbuf, int off, int len) throws IOException
    {
	for (int i = off; i < len; i++) {
	    write((int) cbuf[i]);
	}
    }
}
