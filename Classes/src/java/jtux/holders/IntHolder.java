/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.holders;

/**
 * Class for passing int parameters by reference.
 */
public class IntHolder
{
    /**
     * The parameter value.
     */
    public int value;

    /**
     * Initializes a new IntHolder object.
     *
     * @remarks
     * This constructor sets the initial value of the parameter to 0.
     */
    public IntHolder()
    {
	this.value = 0;
    }

    /**
     * Initializes a new IntHolder object.
     *
     * @param value
     * The initial value of the parameter.
     */
    public IntHolder(int value)
    {
	this.value = value;
    }
}
