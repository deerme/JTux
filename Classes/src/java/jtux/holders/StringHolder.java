/*
 * Copyright 2003, 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.holders;

/**
 * Class for passing String parameters by reference.
 */
public class StringHolder
{
    /**
     * The parameter value.
     */
    public String value;

    /**
     * Initializes a new StringHolder object.
     *
     * @remarks
     * This constructor sets the initial value of the parameter to null.
     */
    public StringHolder()
    {
	this.value = null;
    }

    /**
     * Initializes a new StringHolder object.
     *
     * @param value
     * The initial value of the parameter.
     */
    public StringHolder(String value)
    {
	this.value = value;
    }
}
