/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

// QMEINVHANDLE is neither documented in tpenqueue(3c) nor tpdequeue(3c) 
// => hide it from public view

/**
 * @internal
 */
public class QMEINVHANDLE extends TPEDIAGNOSTIC
{
    QMEINVHANDLE()
    {
	super(ATMI.QMEINVHANDLE);
    }
}
