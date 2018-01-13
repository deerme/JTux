/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.atmi;

/**
 * Thrown if a Tuxedo/Q operation failed.
 *
 * @remarks
 * A TPEDIAGNOSTIC carries a Tuxedo/Q diagnostic error code that 
 * identifies the type of failure that occurred.
 * The symbolic constants for these Tuxedo/Q diagnostic error 
 * codes are defined in the ATMI class. 
 */
public class TPEDIAGNOSTIC extends TPException
{
    private int diagnostic;

    TPEDIAGNOSTIC(int diagnostic)
    {
	super(ATMI.TPEDIAGNOSTIC);

	this.diagnostic = diagnostic;
    }

    /**
     * Returns the /Q diagnostic error code that identifies the reason why
     * this exception was thrown.
     */
    public int getDiagnostic()
    {
	return diagnostic;
    }

    private String getSymbolicName()
    {
	switch (diagnostic) {
	    case ATMI.QMEINVAL:
		return "QMEINVAL";
	    case ATMI.QMEBADRMID:
		return "QMEBADRMID";
	    case ATMI.QMENOTOPEN:
		return "QMENOTOPEN";
	    case ATMI.QMETRAN:
		return "QMETRAN";
	    case ATMI.QMEBADMSGID:
		return "QMEBADMSGID";
	    case ATMI.QMESYSTEM:
		return "QMESYSTEM";
	    case ATMI.QMEOS:
		return "QMEOS";
	    case ATMI.QMEABORTED:
		return "QMEABORTED";
	    case ATMI.QMEPROTO:
		return "QMEPROTO";
	    case ATMI.QMEBADQUEUE:
		return "QMEBADQUEUE";
	    case ATMI.QMENOMSG:
		return "QMENOMSG";
	    case ATMI.QMEINUSE:
		return "QMEINUSE";
	    case ATMI.QMENOSPACE:
		return "QMENOSPACE";
	    case ATMI.QMERELEASE:
		return "QMERELEASE";
	    case ATMI.QMEINVHANDLE:
		return "QMEINVHANDLE";
	    case ATMI.QMESHARE:
		return "QMESHARE";
	    default:
		return Integer.toString(diagnostic);
	}
    }

    /**
     * Returns a short message that describes the reason why this exception
     * was thrown.
     * This method extends the message returned by the TPException base
     * class with the symbolic name of the embedded /Q diagnostic error code.
     */ 
    public String getMessage()
    {
	return super.getMessage() + ": " + getSymbolicName();
    }
}
