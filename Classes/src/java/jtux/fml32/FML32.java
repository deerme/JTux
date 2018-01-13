/* 
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

import jtux.atmi.ATMI;

import jtux.holders.IntHolder;

import jtux.JTuxInterop;
import jtux.TUX;

import java.nio.ByteBuffer;

import java.io.PrintWriter;
import java.io.File;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Provides methods and constants for executing FML32 system calls.
 */
public class FML32
{
    private FML32()
    {
       	// Nothing to do, just make sure it is not public.
    }

    // The following symbol table was generated using OTPTuxSymbolDumper

    /** FML field type */
    public final static int FLD_SHORT = 0;

    /** FML field type */
    public final static int FLD_LONG = 1;

    /** FML field type */
    public final static int FLD_CHAR = 2;

    /** FML field type */
    public final static int FLD_FLOAT = 3;

    /** FML field type */
    public final static int FLD_DOUBLE = 4;

    /** FML field type */
    public final static int FLD_STRING = 5;

    /** FML field type */
    public final static int FLD_CARRAY = 6;

    /** FML field type */
    public final static int FLD_PTR = 9;

    /** FML field type */
    public final static int FLD_FML32 = 10;

    /** FML field type */
    public final static int FLD_VIEW32 = 11;

    /** FML field type */
    public final static int FLD_MBSTRING = 12;

    /** Invalid field identifier */
    public final static int BADFLDID = 0;

    /** Initial field identifier for Fnext */
    public final static int FIRSTFLDID = 0;

    /** FML error code */
    public final static int FALIGNERR = 1;

    /** FML error code */
    public final static int FNOTFLD = 2;

    /** FML error code */
    public final static int FNOSPACE = 3;

    /** FML error code */
    public final static int FNOTPRES = 4;

    /** FML error code */
    public final static int FBADFLD = 5;

    /** FML error code */
    public final static int FTYPERR = 6;

    /** FML error code */
    public final static int FEUNIX = 7;

    /** FML error code */
    public final static int FBADNAME = 8;

    /** FML error code */
    public final static int FMALLOC = 9;

    /** FML error code */
    public final static int FSYNTAX = 10;

    /** FML error code */
    public final static int FFTOPEN = 11;

    /** FML error code */
    public final static int FFTSYNTAX = 12;

    /** FML error code */
    public final static int FEINVAL = 13;

    /** FML error code */
    public final static int FBADTBL = 14;

    /** FML error code */
    public final static int FBADVIEW = 15;

    /** FML error code */
    public final static int FVFSYNTAX = 16;

    /** FML error code */
    public final static int FVFOPEN = 17;

    /** FML error code */
    public final static int FBADACM = 18;

    /** FML error code */
    public final static int FNOCNAME = 19;

    /** FML error code */
    public final static int FEBADOP = 20;

    static FException newFException(int err)
    {
	switch (err) {
	    case FML32.FALIGNERR:
		throw new FALIGNERR();
	    case FML32.FNOTFLD:
		throw new FNOTFLD();
	    case FML32.FNOSPACE:
		throw new FNOSPACE();
	    case FML32.FNOTPRES:
		throw new FNOTPRES();
	    case FML32.FBADFLD:
		throw new FBADFLD();
	    case FML32.FTYPERR:
		throw new FTYPERR();
	    case FML32.FEUNIX:
		throw new FEUNIX();
	    case FML32.FBADNAME:
		throw new FBADNAME();
	    case FML32.FMALLOC:
		throw new FMALLOC();
	    case FML32.FSYNTAX:
		throw new FSYNTAX();
	    case FML32.FFTOPEN:
		throw new FFTOPEN();
	    case FML32.FFTSYNTAX:
		throw new FFTSYNTAX();
	    case FML32.FEINVAL:
		throw new FEINVAL();
	    case FML32.FBADTBL:
		throw new FBADTBL();
	    case FML32.FBADVIEW:
		throw new FBADVIEW();
	    case FML32.FVFSYNTAX:
		throw new FVFSYNTAX();
	    case FML32.FVFOPEN:
		throw new FVFOPEN();
	    case FML32.FBADACM:
		throw new FBADACM();
	    case FML32.FNOCNAME:
		throw new FNOCNAME();
	    case FML32.FEBADOP:
		throw new FEBADOP();
	    default:
	        return new FException(err);
	}
    }

    static FException newFException()
    {
	return newFException(JTuxInterop.Ferror32());
    }

    /*-----------------*/
    /* SFadd() methods */
    /*-----------------*/

    static boolean SFaddByte(ByteBuffer fbfr, int fldid, byte value)
    {
	if (JTuxInterop.CFaddByte32(fbfr, fldid, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFaddShort(ByteBuffer fbfr, int fldid, short value)
    {
	if (JTuxInterop.CFaddShort32(fbfr, fldid, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFaddInt(ByteBuffer fbfr, int fldid, int value)
    {
	if (JTuxInterop.CFaddInt32(fbfr, fldid, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFaddFloat(ByteBuffer fbfr, int fldid, float value)
    {
	if (JTuxInterop.CFaddFloat32(fbfr, fldid, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFaddDouble(ByteBuffer fbfr, int fldid, double value)
    {
	if (JTuxInterop.CFaddDouble32(fbfr, fldid, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFaddString(ByteBuffer fbfr, int fldid, String value)
    {
	byte[] bytes = (value == null) ? null : value.getBytes();

	if (JTuxInterop.CFaddBytes32(fbfr, fldid, bytes) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFaddBytes(ByteBuffer fbfr, int fldid, byte[] value)
    {
	if (JTuxInterop.CFaddBytes32(fbfr, fldid, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFadd(ByteBuffer fbfr, int fldid, Object value)
    {
	if (value instanceof Byte) {
	    return SFaddByte(fbfr, fldid, ((Byte) value).byteValue());
	} else if (value instanceof Short) {
	    return SFaddShort(fbfr, fldid, ((Short) value).shortValue());
	} else if (value instanceof Integer) {
	    return SFaddInt(fbfr, fldid, ((Integer) value).intValue());
	} else if (value instanceof Float) {
	    return SFaddFloat(fbfr, fldid, ((Float) value).floatValue());
	} else if (value instanceof Double) {
	    return SFaddDouble(fbfr, fldid, ((Double) value).doubleValue());
	} else if (value instanceof String) {
	    return SFaddString(fbfr, fldid, (String) value);
	} else if (value instanceof byte[]) {
	    return SFaddBytes(fbfr, fldid, (byte[]) value);
	} else {
	    throw new IllegalArgumentException("Unsupported value type: "
		+ value.getClass().getName());
	}
    }

    /*----------------*/
    /* Fadd() methods */
    /*----------------*/

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param value
     * The value of the field, as a byte.
     *
     * @remarks
     * The given byte value is converted to match the type of the field
     * using the conversion rules for FML source type char summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder} if you want the buffer to be
     * expanded automatically when adding field occurrences.
     *
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public static void FaddByte(ByteBuffer fbfr, int fldid, byte value)
	throws FNOSPACE
    {
	if (!SFaddByte(fbfr, fldid, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param value
     * The value of the field, as a short.
     *
     * @remarks
     * The given short value is converted to match the type of the field
     * using the conversion rules for FML source type short summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder} if you want the buffer to be
     * expanded automatically when adding field occurrences.
     *
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public static void FaddShort(ByteBuffer fbfr, int fldid, short value)
	throws FNOSPACE
    {
	if (!SFaddShort(fbfr, fldid, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param value
     * The value of the field, as an int.
     *
     * @remarks
     * The given int value is converted to match the type of the field
     * using the conversion rules for FML source type long summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder} if you want the buffer to be
     * expanded automatically when adding field occurrences.
     *
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public static void FaddInt(ByteBuffer fbfr, int fldid, int value)
	throws FNOSPACE
    {
	if (!SFaddInt(fbfr, fldid, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param value
     * The value of the field, as a float.
     *
     * @remarks
     * The given float value is converted to match the type of the field
     * using the conversion rules for FML source type float summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder} if you want the buffer to be
     * expanded automatically when adding field occurrences.
     *
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public static void FaddFloat(ByteBuffer fbfr, int fldid, float value)
	throws FNOSPACE
    {
	if (!SFaddFloat(fbfr, fldid, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param value
     * The value of the field, as a double.
     *
     * @remarks
     * The given double value is converted to match the type of the field
     * using the conversion rules for FML source type double summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder} if you want the buffer to be
     * expanded automatically when adding field occurrences.
     *
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public static void FaddDouble(ByteBuffer fbfr, int fldid, double value)
	throws FNOSPACE
    {
	if (!SFaddDouble(fbfr, fldid, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param value
     * The value of the field, as a String.
     *
     * @remarks
     * The given String value is converted to match the type of the field
     * using the conversion rules for FML source type string summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder} if you want the buffer to be
     * expanded automatically when adding field occurrences.
     *
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public static void FaddString(ByteBuffer fbfr, int fldid, String value)
	throws FNOSPACE
    {
	if (!SFaddString(fbfr, fldid, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param value
     * The value of the field, as a byte[].
     *
     * @remarks
     * The given byte[] value is converted to match the type of the field
     * using the conversion rules for FML source type carray summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder} if you want the buffer to be
     * expanded automatically when adding field occurrences.
     *
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public static void FaddBytes(ByteBuffer fbfr, int fldid, byte[] value)
	throws FNOSPACE
    {
	if (!SFaddBytes(fbfr, fldid, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param value
     * The value of the field, as a Byte, Short, Integer, Float, Double,
     * String or byte[].
     *
     * @remarks
     * The given value is converted to match the type of the field
     * using the conversion rules for FML source type char summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder} if you want the buffer to be
     * expanded automatically when adding field occurrences.
     *
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public static void Fadd(ByteBuffer fbfr, int fldid, Object value)
	throws FNOSPACE
    {
	if (!SFadd(fbfr, fldid, value)) {
	    throw new FNOSPACE();
	}
    }

    /*-----------------*/
    /* SFchg() methods */
    /*-----------------*/

    static boolean SFchgByte(ByteBuffer fbfr, int fldid, int occ,
	byte value)
    {
	if (JTuxInterop.CFchgByte32(fbfr, fldid, occ, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFchgShort(ByteBuffer fbfr, int fldid, int occ,
	short value)
    {
	if (JTuxInterop.CFchgShort32(fbfr, fldid, occ, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFchgInt(ByteBuffer fbfr, int fldid, int occ,
	int value)
    {
	if (JTuxInterop.CFchgInt32(fbfr, fldid, occ, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFchgFloat(ByteBuffer fbfr, int fldid, int occ,
	float value)
    {
	if (JTuxInterop.CFchgFloat32(fbfr, fldid, occ, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFchgDouble(ByteBuffer fbfr, int fldid, int occ,
	double value)
    {
	if (JTuxInterop.CFchgDouble32(fbfr, fldid, occ, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFchgString(ByteBuffer fbfr, int fldid, int occ,
	String value)
    {
	byte[] bytes = (value == null) ? null : value.getBytes();

	if (JTuxInterop.CFchgBytes32(fbfr, fldid, occ, bytes) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFchgBytes(ByteBuffer fbfr, int fldid, int occ,
	byte[] value)
    {
	if (JTuxInterop.CFchgBytes32(fbfr, fldid, occ, value) == -1) {
	    if (JTuxInterop.Ferror32() == FNOSPACE) {
		return false;
	    }
	    throw newFException();
	}
	return true;
    }

    static boolean SFchg(ByteBuffer fbfr, int fldid, int occ, Object v)
    {
	if (v instanceof Byte) {
	    return SFchgByte(fbfr, fldid, occ, ((Byte) v).byteValue());
	} else if (v instanceof Short) {
	    return SFchgShort(fbfr, fldid, occ, ((Short) v).shortValue());
	} else if (v instanceof Integer) {
	    return SFchgInt(fbfr, fldid, occ, ((Integer) v).intValue());
	} else if (v instanceof Float) {
	    return SFchgFloat(fbfr, fldid, occ, ((Float) v).floatValue());
	} else if (v instanceof Double) {
	    return SFchgDouble(fbfr, fldid, occ, ((Double) v).doubleValue());
	} else if (v instanceof String) {
	    return SFchgString(fbfr, fldid, occ, (String) v);
	} else if (v instanceof byte[]) {
	    return SFchgBytes(fbfr, fldid, occ, (byte[]) v);
	} else {
	    throw new IllegalArgumentException("Unsupported value type: "
		+ v.getClass().getName());
	}
    }

    /*----------------*/
    /* Fchg() methods */
    /*----------------*/

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @param value
     * The value of the field occurrence, as a byte.
     *
     * @remarks
     * The given byte value is converted to match the type of the field
     * using the conversion rules for FML source type char summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder FBuilder} if you want
     * the buffer to be expanded automatically when setting field
     * occurrences.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public static void FchgByte(ByteBuffer fbfr, int fldid, int occ,
	byte value) throws FNOSPACE
    {
	if (!SFchgByte(fbfr, fldid, occ, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @param value
     * The value of the field occurrence, as a short.
     *
     * @remarks
     * The given short value is converted to match the type of the field
     * using the conversion rules for FML source type short summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder FBuilder} if you want
     * the buffer to be expanded automatically when setting field
     * occurrences.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public static void FchgShort(ByteBuffer fbfr, int fldid, int occ,
	short value) throws FNOSPACE
    {
	if (!SFchgShort(fbfr, fldid, occ, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @param value
     * The value of the field occurrence, as an int.
     *
     * @remarks
     * The given int value is converted to match the type of the field
     * using the conversion rules for FML source type long summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder FBuilder} if you want
     * the buffer to be expanded automatically when setting field
     * occurrences.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public static void FchgInt(ByteBuffer fbfr, int fldid, int occ,
	int value) throws FNOSPACE
    {
	if (!SFchgInt(fbfr, fldid, occ, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @param value
     * The value of the field occurrence, as a float.
     *
     * @remarks
     * The given float value is converted to match the type of the field
     * using the conversion rules for FML source type float summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder FBuilder} if you want
     * the buffer to be expanded automatically when setting field
     * occurrences.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public static void FchgFloat(ByteBuffer fbfr, int fldid, int occ,
	float value) throws FNOSPACE
    {
	if (!SFchgFloat(fbfr, fldid, occ, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @param value
     * The value of the field occurrence, as a double.
     *
     * @remarks
     * The given double value is converted to match the type of the field
     * using the conversion rules for FML source type double summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder FBuilder} if you want
     * the buffer to be expanded automatically when setting field
     * occurrences.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public static void FchgDouble(ByteBuffer fbfr, int fldid, int occ,
	double value) throws FNOSPACE
    {
	if (!SFchgDouble(fbfr, fldid, occ, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @param value
     * The value of the field occurrence, as a String.
     *
     * @remarks
     * The given String value is converted to match the type of the field
     * using the conversion rules for FML source type string summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder FBuilder} if you want
     * the buffer to be expanded automatically when setting field
     * occurrences.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public static void FchgString(ByteBuffer fbfr, int fldid, int occ,
	String value) throws FNOSPACE
    {
	if (!SFchgString(fbfr, fldid, occ, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @param value
     * The value of the field occurrence, as a byte[].
     *
     * @remarks
     * The given byte[] value is converted to match the type of the field
     * using the conversion rules for FML source type carray summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder FBuilder} if you want
     * the buffer to be expanded automatically when setting field
     * occurrences.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public static void FchgBytes(ByteBuffer fbfr, int fldid, int occ,
	byte[] value) throws FNOSPACE
    {
	if (!SFchgBytes(fbfr, fldid, occ, value)) {
	    throw new FNOSPACE();
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @param value
     * The value of the field occurrence, as a Byte, Short, Integer,
     * Float, Double, String or byte[].
     *
     * @remarks
     * The given value is converted to match the type of the field
     * using the conversion rules summarized in Table 5-2 of the
     * Tuxedo FML programming guide.
     *
     * @throws FNOSPACE
     * There is not enough space in the buffer for the new field value.
     * Use an {@link jtux.fml32.FBuilder FBuilder} if you want
     * the buffer to be expanded automatically when setting field
     * occurrences.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public static void Fchg(ByteBuffer fbfr, int fldid, int occ, Object value)
	throws FNOSPACE
    {
	if (!SFchg(fbfr, fldid, occ, value)) {
	    throw new FNOSPACE();
	}
    }

    /*----------------*/
    /* Fget() methods */
    /*----------------*/

    /**
     * Returns the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @return
     * The value of the field occurrence, as a byte.
     *
     * @remarks
     * The value of the field occurrence is converted to a byte
     * using the conversion rules for FML destination type char
     * summerized in Table 5-2 of the Tuxedo FML programming
     * guide.
     *
     * @throws	FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo CFget32(3fml) manual page.
     */
    public static byte FgetByte(ByteBuffer fbfr, int fldid, int occ)
	throws FNOTPRES
    {
	byte value = JTuxInterop.CFgetByte32(fbfr, fldid, occ);
	if ((value == 0) && (JTuxInterop.Ferror32() != 0)) {
	    throw newFException();
	}
	return value;
    }

    /**
     * Returns the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @return
     * The value of the field occurrence, as a short.
     *
     * @remarks
     * The value of the field occurrence is converted to a short
     * using the conversion rules for FML destination type short
     * summerized in Table 5-2 of the Tuxedo FML programming
     * guide.
     *
     * @throws	FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo CFget32(3fml) manual page.
     */
    public static short FgetShort(ByteBuffer fbfr, int fldid, int occ)
    {
	short value = JTuxInterop.CFgetShort32(fbfr, fldid, occ);
	if ((value == 0) && (JTuxInterop.Ferror32() != 0)) {
	    throw newFException();
	}
	return value;
    }

    /**
     * Returns the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @return
     * The value of the field occurrence, as an int.
     *
     * @remarks
     * The value of the field occurrence is converted to an int
     * using the conversion rules for FML destination type long
     * summerized in Table 5-2 of the Tuxedo FML programming
     * guide.
     *
     * @throws	FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo CFget32(3fml) manual page.
     */
    public static int FgetInt(ByteBuffer fbfr, int fldid, int occ)
    {
	int value = JTuxInterop.CFgetInt32(fbfr, fldid, occ);
	if ((value == 0) && (JTuxInterop.Ferror32() != 0)) {
	    throw newFException();
	}
	return value;
    }

    /**
     * Returns the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @return
     * The value of the field occurrence, as a float.
     *
     * @remarks
     * The value of the field occurrence is converted to a float
     * using the conversion rules for FML destination type float
     * summerized in Table 5-2 of the Tuxedo FML programming
     * guide.
     *
     * @throws	FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo CFget32(3fml) manual page.
     */
    public static float FgetFloat(ByteBuffer fbfr, int fldid, int occ)
    {
	float value = JTuxInterop.CFgetFloat32(fbfr, fldid, occ);
	if ((value == 0) && (JTuxInterop.Ferror32() != 0)) {
	    throw newFException();
	}
	return value;
    }

    /**
     * Returns the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @return
     * The value of the field occurrence, as a double.
     *
     * @remarks
     * The value of the field occurrence is converted to a double
     * using the conversion rules for FML destination type double
     * summerized in Table 5-2 of the Tuxedo FML programming
     * guide.
     *
     * @throws	FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo CFget32(3fml) manual page.
     */
    public static double FgetDouble(ByteBuffer fbfr, int fldid, int occ)
    {
	double value = JTuxInterop.CFgetDouble32(fbfr, fldid, occ);
	if ((value == 0) && (JTuxInterop.Ferror32() != 0)) {
	    throw newFException();
	}
	return value;
    }

    /**
     * Returns the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @return
     * The value of the field occurrence, as a String.
     *
     * @remarks
     * The value of the field occurrence is converted to a String
     * using the conversion rules for FML destination type string
     * summerized in Table 5-2 of the Tuxedo FML programming
     * guide.
     *
     * @throws	FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo CFget32(3fml) manual page.
     */
    public static String FgetString(ByteBuffer fbfr, int fldid, int occ)
    {
	byte[] value = JTuxInterop.CFfindBytes32(fbfr, fldid, occ);
	if (value == null) {
	    throw newFException();
	}
	return new String(value);
    }

    /**
     * Returns the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @return
     * The value of the field occurrence, as a byte[].
     *
     * @remarks
     * The value of the field occurrence is converted to a byte[]
     * using the conversion rules for FML destination type carray
     * summerized in Table 5-2 of the Tuxedo FML programming
     * guide.
     *
     * @throws	FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo CFget32(3fml) manual page.
     */
    public static byte[] FgetBytes(ByteBuffer fbfr, int fldid, int occ)
    {
	byte[] value = JTuxInterop.CFfindBytes32(fbfr, fldid, occ);
	if (value == null) {
	    throw newFException();
	}
	return value;
    }

    /**
     * Returns the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @return
     * The value of the field occurrence, as a Byte, Short, Integer,
     * Float, Double, String of byte[].
     *
     * @remarks
     * The type of the returned value depends on the type of the
     * FML field as specified by the following table.
     * <table>
     * <th><td>FML field type</td><td>Return type</td></th>
     * <tr><td>char</td><td>Byte</td></tr>
     * <tr><td>short</td><td>Short</td></tr>
     * <tr><td>long</td><td>Integer</td></tr>
     * <tr><td>float</td><td>Float</td></tr>
     * <tr><td>double</td><td>Double</td></tr>
     * <tr><td>string</td><td>String</td></tr>
     * <tr><td>carray</td><td>byte[]</td></tr>
     * </table>
     *
     * @throws FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo CFget32(3fml) manual page.
     */
    public static Object Fget(ByteBuffer fbfr, int fldid, int occ)
    {
	switch (Fldtype(fldid)) {
	    case FLD_CHAR:
		return new Byte(FgetByte(fbfr, fldid, occ));
	    case FLD_SHORT:
		return new Short(FgetShort(fbfr, fldid, occ));
	    case FLD_LONG:
		return new Integer(FgetInt(fbfr, fldid, occ));
	    case FLD_FLOAT:
		return new Float(FgetFloat(fbfr, fldid, occ));
	    case FLD_DOUBLE:
		return new Double(FgetDouble(fbfr, fldid, occ));
	    case FLD_STRING:
		return FgetString(fbfr, fldid, occ);
	    case FLD_CARRAY:
		return FgetBytes(fbfr, fldid, occ);
	    default:
		throw new IllegalArgumentException("Unsupported field type "
		    + Fldtype(fldid));
	}
    }

    /*---------------*/
    /* Other methods */
    /*---------------*/

    /**
     * Deletes a field occurrence from an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @throws FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo Fdel32(3fml) manual page.
     */
    public static void Fdel(ByteBuffer fbfr, int fldid, int occ)
	throws FNOTPRES
    {
	if (JTuxInterop.Fdel32(fbfr, fldid, occ) == -1) {
	    throw newFException();
	}
    }

    /**
     * Deletes all occurrences of a particular field from an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @throws FNOTPRES
     * The buffer does not contain any occurrences of the requested field.
     *
     * @throws FException
     * See the Tuxedo Fdelall32(3fml) manual page.
     */
    public static void Fdelall(ByteBuffer fbfr, int fldid)
	throws FNOTPRES
    {
	if (JTuxInterop.Fdelall32(fbfr, fldid) == -1) {
	    throw newFException();
	}
    }

    /**
     * Initializes a memory buffer as an FML32 buffer.
     *
     * @param fbfr
     * The memory buffer.
     *
     * @param buflen
     * The size of the FML32 region of the buffer; must not exceed the
     * size of the buffer.
     *
     * @throws FException
     * See the Tuxedo Finit32(3fml) manual page.
     */
    public static void Finit(ByteBuffer fbfr, int buflen)
    {
	if (fbfr == null) {
	    throw new IllegalArgumentException("fbfr must not be null");
	}

	if (buflen > fbfr.capacity()) {
	    throw new IllegalArgumentException("buflen must not exceed "
		+ "the size of the buffer");
	}
	
	if (JTuxInterop.Finit32(fbfr, buflen) == -1) {
	    throw newFException();
	}
    }

    /**
     * Returns the FML32 field identifier for a given field name.
     *
     * @param name
     * The field name.
     *
     * @return
     * The field identifier for {@paramref name}.
     *
     * @throws FBADNAME
     * The given field name cannot be found in the FML field tables.
     *
     * @throws FException
     * See the Tuxedo Fldid32(3fml) manual page.
     */
    public static int Fldid(String name) throws FBADNAME
    {
	int result = JTuxInterop.Fldid32(name);
	if (result == BADFLDID) {
	    throw newFException();
	}
	return result;
    }

    /**
     * Returns the field number of an FML32 field identifier.
     *
     * @param fldid
     * The field identifier.
     *
     * @return
     * The field number of {@paramref fldid}.
     */
    public static int Fldno(int fldid)
    {
	return JTuxInterop.Fldno32(fldid);
    }

    /**
     * Returns the field type of an FML32 field identifier.
     *
     * @param fldid
     * The field identifier.
     *
     * @return
     * The field type of {@paramref fldid}.
     */
    public static int Fldtype(int fldid)
    {
	return JTuxInterop.Fldtype32(fldid);
    }

    /**
     * Returns the length of the value of a field occurrence
     * in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @return
     * The length of the value of the field occurrence.
     *
     * @throws FNOTPRES
     * The buffer does not contain the requested field occurrence.
     *
     * @throws FException
     * See the Tuxedo Flen32(3fml) manual page.
     */
    public static int Flen(ByteBuffer fbfr, int fldid, int occ) 
	throws FNOTPRES
    {
	int result = JTuxInterop.Flen32(fbfr, fldid, occ);
	if (result == -1) {
	    throw newFException();
	}
	return result;
    }

    /**
     * Constructs an FML32 field identifier from a field type
     * and field number.
     *
     * @param fldtype
     * The field type.
     *
     * @param fldno
     * The field number.
     *
     * @return
     * The field identifier constructed from {@paramref fldtype} and
     * {@paramref fldno}.
     *
     * @throws FException
     * See the Tuxedo Fmkfldid32(3fml) manual page.
     */
    public static int Fmkfldid(int fldtype, int fldno)
    {
	int result = JTuxInterop.Fmkfldid32(fldtype, fldno);
	if (result == BADFLDID) {
	    throw newFException();
	}
	return result;
    }

    /**
     * @internal
     */
    public static String Fldname(int fldid)
    {
	String result = JTuxInterop.Fname32(fldid);
	if ((result == null) && (JTuxInterop.Ferror32() != FBADFLD)) {
	    throw newFException();
	}
	return result;
    }

    /**
     * Returns the the field name for an FML32 field identifier.
     *
     * @param fldid
     * The field identifier.
     *
     * @return
     * The field name for {@paramref fldid}.
     *
     * @throws	FBADFLD
     * The given field identifier is invalid or cannot be found in the
     * FML field tables.
     *
     * @throws FException
     * See the Tuxedo Fname32(3fml) manual page.
     */
    public static String Fname(int fldid) throws FBADFLD
    {
	String result = JTuxInterop.Fname32(fldid);
	if (result == null) {
	    throw newFException();
	}
	return result;
    }

    /**
     * Gets the next field occurrence in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldidRef
     * The field identifier for which to get the next field occurrence.
     * Gets updated with the field identifier of the next field
     * occurrence.
     *
     * @param occRef
     * The field occurrence number for wich to get the next field
     * occurrence.
     * Gets updated with the field occurrence number of the next
     * field occurrence.
     *
     * @return
     * True if the given field occurrence has a next field occurrence,
     * false otherwise.
     *
     * @throws FException
     * See the Tuxedo Fnext32(3fml) manual page.
     *
     * @exmple
     * The following Java code fragment shows how to iterate through the
     * field occurrences in an FML32 buffer using {@link #Fnext}.
     * <pre>
     * IntHolder fldidRef = new IntHolder(FML32.FIRSTFLDID);
     * IntHolder occRef = new IntHolder(0);
     *
     * while (FML32.Fnext(fbfr, fldidRef, occRef)) {
     *     Object value = FML32.Fget(fbfr, fldidRef.value, occRef.value);
     *     // Do something with value
     * }
     * </pre>
     */
    public static boolean Fnext(ByteBuffer fbfr, IntHolder fldidRef,
	IntHolder occRef)
    {
	int result = JTuxInterop.Fnext32(fbfr, fldidRef, occRef);
	if (result == -1) {
	    throw newFException();
	}
	return (result != 0);
    }

    // Fneeded() skipped because I don't trust this method.

    /**
     * Returns the total number of field occurrences in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @return
     * The total number of field occurrences in {@paramref fbfr}.
     *
     * @throws FException
     * See the Tuxedo Fnum32(3fml) manual page.
     */
    public static int Fnum(ByteBuffer fbfr)
    {
	int result = JTuxInterop.Fnum32(fbfr);
	if (result == -1) {
	    throw newFException();
	}
	return result;
    }

    /**
     * Returns the number of occurrences of a field in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @param fldid
     * The field identifier.
     *
     * @return
     * The number of occurrences of {@paramref fldid} in {@paramref fbfr}.
     *
     * @throws FException
     * See the Tuxedo Foccur32(3fml) manual page.
     */
    public static int Foccur(ByteBuffer fbfr, int fldid)
    {
	int result = JTuxInterop.Foccur32(fbfr, fldid);
	if (result == -1) {
	    throw newFException();
	}
	return result;
    }

    // Fpres() skipped because of weird (i.e. absent) failure semantics

    /**
     * Returns the size of an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @return
     * The size of the FML32 region in {@paramref fbfr}.
     *
     * @throws FException
     * See the Tuxedo Fsizeof32(3fml) manual page.
     */
    public static int Fsizeof(ByteBuffer fbfr)
    {
	int result = JTuxInterop.Fsizeof32(fbfr);
	if (result == -1) {
	    throw newFException();
	}
	return result;
    }

    /**
     * Returns the name of the type of an FML field identifier.
     *
     * @param fldid
     * The field identifier.
     *
     * @return
     * The name of the type of {@paramref fldid}.
     *
     * @throws FException
     * See the Tuxedo Ftype32(3fml) manual page.
     */
    public static String Ftype(int fldid)
    {
	String result = JTuxInterop.Ftype32(fldid);
	if (result == null) {
	    throw newFException();
	}
	return result;
    }

    /**
     * Returns the amount of unused space in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @return
     * The amount of unused space in the FML32 region of {@paramref fbfr}.
     *
     * @throws FException
     * See the Tuxedo Funused32(3fml) manual page.
     */
    public static int Funused(ByteBuffer fbfr)
    {
	int result = JTuxInterop.Funused32(fbfr);
	if (result == -1) {
	    throw newFException();
	}
	return result;
    }

    /**
     * Returns the amount of used space in an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @return
     * The amount of used space in the FML32 region of {@paramref fbfr}.
     *
     * @throws FException
     * See the Tuxedo Fused32(3fml) manual page.
     */
    public static int Fused(ByteBuffer fbfr)
    {
	int result = JTuxInterop.Fused32(fbfr);
	if (result == -1) {
	    throw newFException();
	}
	return result;
    }

    /**
     * Unloads the mapping table used to resolve FML32 field identifiers
     * to field names.
     */
    public static void Fidnm_unload()
    {
	JTuxInterop.Fidnm_unload32();
    }

    /**
     * Unloads the mapping table used to resolve field names to FML32 field
     * identifiers.
     */
    public static void Fnmid_unload()
    {
	JTuxInterop.Fnmid_unload32();
    }

    /**
     * Returns a string representation of the contents of an FML32 buffer.
     *
     * @param fbfr
     * The FML32 buffer.
     *
     * @return
     * A string representation of the contents of {@paramref fbfr}.
     *
     * @remarks
     * The actual string representation is not specified and should not
     * be relied upon.
     */
    public static String toString(ByteBuffer fbfr)
    {
	// We cannot use new FieldMap(fbfr).toString() because
	// new FieldMap(fbfr) will fail if fbfr contains unsupported
	// field types.

	if (fbfr == null) {
	    return "null";
	}

	StringBuffer s = new StringBuffer();

	IntHolder fldid = new IntHolder(FIRSTFLDID);
	IntHolder occ = new IntHolder(0);

	s.append('{');

	boolean isFirst = true;

	while (Fnext(fbfr, fldid, occ)) {

	    if (isFirst) {
		isFirst = false;
	    } else {
		s.append(", ");
	    }

	    String fldname = Fldname(fldid.value);
	    if (fldname == null) {
		s.append(fldid.value);
	    } else {
		s.append(fldname);
	    }

	    s.append("=[");

	    int fldtype = Fldtype(fldid.value);

	    int occur = Foccur(fbfr, fldid.value);
	    for (int i = 0; i < occur; i++) {

		if (i > 0) {
		    s.append(", ");
		}

		switch (fldtype) {
		    case FLD_CHAR:
			s.append(FgetByte(fbfr, fldid.value, i));
			break;
		    case FLD_SHORT:
			s.append(FgetShort(fbfr, fldid.value, i));
			break;
		    case FLD_LONG:
			s.append(FgetInt(fbfr, fldid.value, i));
			break;
		    case FLD_FLOAT:
			s.append(FgetFloat(fbfr, fldid.value, i));
			break;
		    case FLD_DOUBLE:
			s.append(FgetDouble(fbfr, fldid.value, i));
			break;
		    case FLD_STRING:
			s.append('\'');
			s.append(FgetString(fbfr, fldid.value, i));
			s.append('\'');
			break;
		    default:
			s.append("<...>");
		}
	    }

	    s.append(']');

	    occ.value = occur;
	}

	s.append('}');

	return s.toString();
    }

    /**
     * Registers an FML32 field mapping table at runtime.
     *
     * @param fieldTable
     * The FML field table file.
     */
    public static void addFieldTable(File fieldTable)
    {
	// According to section field_table(5) of the Tuxedo reference
	// guide, the FLDTBLDIR32 environment variable is a colon-separated
	// list of directory names. It does not say that it is a semi-colon
	// separated list on Windows. To prevent any problems caused by
	// directories containing colons on Windows, we do not use the
	// FLDTBLDIR32 environment variable but simply put the absolute path
	// name of the field table in the FIELDTBLS32 environment variable.
	// The same section documents that this is possible and that in this
	// case, the FLDTBLDIR32 environment variable is not consulted.
	// This only works, however, if the field table path does not
	// contain a comma.

	String fieldTablePath = fieldTable.getAbsolutePath();
	if (fieldTablePath.indexOf(',') != -1) {
	    throw new IllegalArgumentException("Field table path '"
		+ fieldTablePath + "' contains a comma");
	}
	
	String FIELDTBLS32 = TUX.tuxgetenv("FIELDTBLS32");
	if (FIELDTBLS32 == null) {
	    FIELDTBLS32 = fieldTablePath;
	} else {
	    FIELDTBLS32 = FIELDTBLS32 + ',' + fieldTablePath;
	}
	TUX.tuxputenv("FIELDTBLS32=" + FIELDTBLS32);

	// After setting FIELDTBLS32, we need to unload the current field
	// mappings to force Fname32() and Fldid32() to reload the new
	// list of field tables.
	
	Fidnm_unload();
	Fnmid_unload();
    }
}
