/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.fml32;

import jtux.holders.ByteBufferHolder;

import java.nio.ByteBuffer;

/**
 * Utility class for populating FML32 buffers.
 *
 * @remarks
 * The FBuilder class provides methods for adding new field occurrences to
 * an FML32 buffer and changing the values of existing field occurrences in
 * an FML32 buffer.
 * These methods call upon the abstract {@link #realloc} method to
 * automatically enlarge the involved buffer if necessary.
 * Derived classes must provide an implementation of the {@link #realloc}
 * method for a particular type of memory buffer.
 * The {@link jtux.fml32.TPFBuilder} class, for example, provides an
 * implementation of the {@link #realloc} method for Tuxedo typed
 * buffers.
 */
public abstract class FBuilder
{
    /**
     * Initializes a new instance of the FBuilder class.
     */
    protected FBuilder()
    {
    }

    /**
     * Reallocates a memoery buffer.
     *
     * @param fbfrRef
     * The memory buffer to reallocate.
     * Gets updated with the reallocated memory buffer.
     *
     * @param newSize
     * The new size of the buffer.
     */
    public abstract ByteBuffer realloc(ByteBuffer fbfr, int newSize);

    private void grow(ByteBufferHolder fbfrRef)
    {
	int newSize = (int) (FML32.Fsizeof(fbfrRef.value) * 1.5);

	fbfrRef.value = realloc(fbfrRef.value, newSize);
    }

    /*----------------*/
    /* Fadd() methods */
    /*----------------*/

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the added field.
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
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public void FaddByte(ByteBufferHolder fbfrRef, int fldid, byte value)
    {
	while (!FML32.SFaddByte(fbfrRef.value, fldid, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the added field.
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
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public void FaddShort(ByteBufferHolder fbfrRef, int fldid, short value)
    {
	while (!FML32.SFaddShort(fbfrRef.value, fldid, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the added field.
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
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public void FaddInt(ByteBufferHolder fbfrRef, int fldid, int value)
    {
	while (!FML32.SFaddInt(fbfrRef.value, fldid, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the added field.
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
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public void FaddFloat(ByteBufferHolder fbfrRef, int fldid, float value)
    {
	while (!FML32.SFaddFloat(fbfrRef.value, fldid, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the added field.
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
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public void FaddDouble(ByteBufferHolder fbfrRef, int fldid, double value)
    {
	while (!FML32.SFaddDouble(fbfrRef.value, fldid, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the added field.
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
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public void FaddString(ByteBufferHolder fbfrRef, int fldid, String value)
    {
	while (!FML32.SFaddString(fbfrRef.value, fldid, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the added field.
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
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public void FaddBytes(ByteBufferHolder fbfrRef, int fldid, byte[] value)
    {
	while (!FML32.SFaddBytes(fbfrRef.value, fldid, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Adds a field to an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the added field.
     *
     * @param fldid
     * The field identifier.
     *
     * @param value
     * The value of the field, as a Byte, Short, Integer, Float, Doube,
     * String or byte[].
     *
     * @remarks
     * The given value is converted to match the type of the field
     * using the conversion rules summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FException
     * See the Tuxedo CFadd32(3fml) manual page.
     */
    public void Fadd(ByteBufferHolder fbfrRef, int fldid, Object value)
    {
	while (!FML32.SFadd(fbfrRef.value, fldid, value)) {
	    grow(fbfrRef);
	}
    }

    /*----------------*/
    /* Fchg() methods */
    /*----------------*/

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the new field
     * occurrence value.
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
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public void FchgByte(ByteBufferHolder fbfrRef, int fldid, int occ,
	byte value)
    {
	while (!FML32.SFchgByte(fbfrRef.value, fldid, occ, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the new field
     * occurrence value.
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
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public void FchgShort(ByteBufferHolder fbfrRef, int fldid, int occ,
	short value)
    {
	while (!FML32.SFchgShort(fbfrRef.value, fldid, occ, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the new field
     * occurrence value.
     *
     * @param fldid
     * The field identifier.
     *
     * @param occ
     * The field occurrence number.
     *
     * @param value
     * The value of the field occurrence, as a int.
     *
     * @remarks
     * The given int value is converted to match the type of the field
     * using the conversion rules for FML source type long summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public void FchgInt(ByteBufferHolder fbfrRef, int fldid, int occ,
	int value)
    {
	while (!FML32.SFchgInt(fbfrRef.value, fldid, occ, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the new field
     * occurrence value.
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
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public void FchgFloat(ByteBufferHolder fbfrRef, int fldid, int occ,
	float value)
    {
	while (!FML32.SFchgFloat(fbfrRef.value, fldid, occ, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the new field
     * occurrence value.
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
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public void FchgDouble(ByteBufferHolder fbfrRef, int fldid, int occ,
	double value)
    {
	while (!FML32.SFchgDouble(fbfrRef.value, fldid, occ, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the new field
     * occurrence value.
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
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public void FchgString(ByteBufferHolder fbfrRef, int fldid, int occ,
	String value)
    {
	while (!FML32.SFchgString(fbfrRef.value, fldid, occ, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the new field
     * occurrence value.
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
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public void FchgBytes(ByteBufferHolder fbfrRef, int fldid, int occ,
	byte[] value)
    {
	while (!FML32.SFchgBytes(fbfrRef.value, fldid, occ, value)) {
	    grow(fbfrRef);
	}
    }

    /**
     * Sets the value of a field occurrence in an FML32 buffer.
     *
     * @param fbfrRef
     * The FML32 buffer.
     * Gets reallocated if needed to accomodate the new field
     * occurrence value.
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
     * using the conversion rules summarized
     * in Table 5-2 of the Tuxedo FML programming guide.
     *
     * @throws FException
     * See the Tuxedo CFchg32(3fml) manual page.
     */
    public void Fchg(ByteBufferHolder fbfrRef, int fldid, int occ, 
	Object value)
    {
	while (!FML32.SFchg(fbfrRef.value, fldid, occ, value)) {
	    grow(fbfrRef);
	}
    }
}
