/*
 * Copyright 2004 Cansulta Oy. All rights reserved.
 */

package jtux.jms.v10;

public class Adapter
{
    private Object object;

    Adapter(Object object)
    {
	this.object = object;
    }

    public Object getObject()
    {
	return object;
    }

    public String toString()
    {
	return super.toString() + "(" + object.toString() + ")";
    }
}
