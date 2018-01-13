/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.holders;

import java.nio.ByteBuffer;

/**
 * Class for passing java.nio.Bytebuffer parameters by reference.
 */
public class ByteBufferHolder
{
    /**
     * The parameter value.
     */
    public ByteBuffer value;

    /**
     * Initializes a new ByteBufferHolder object.
     *
     * @remarks
     * This constructor sets the initial value of the parameter to null.
     */
    public ByteBufferHolder()
    {
	this.value = null;
    }

    /**
     * Initializes a new ByteBufferHolder object.
     *
     * @param value
     * The initial value of the parameter.
     */
    public ByteBufferHolder(ByteBuffer value)
    {
	this.value = value;
    }
}
