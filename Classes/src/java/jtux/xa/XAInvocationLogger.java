/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package jtux.xa;

import jtux.TUX;

import javax.transaction.xa.XAResource;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class XAInvocationLogger implements InvocationHandler
{
    private Object object;
    
    public XAInvocationLogger(Object object)
    {
        this.object = object;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable
    {
        StringBuffer buffer = new StringBuffer();
        
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(args[i]);
            }
        }
        
        TUX.userlog("DEBUG [XA]: Invoking " + object + "." + method.getName()
            + "(" + buffer.toString() + ")");
        
        try {
            Object result = method.invoke(object, args);
            if (!method.getReturnType().equals(Void.TYPE)) {
                TUX.userlog("DEBUG [XA]: Returning " + result);
            }
            return result;
        } catch (InvocationTargetException eInvocationTarget) {
            throw eInvocationTarget.getCause();
        }
    }
    
    public static XAResource wrap(XAResource xaResource)
    {
        return (XAResource) Proxy.newProxyInstance(
	    XAResource.class.getClassLoader(),
            new Class[] { XAResource.class },
	    new XAInvocationLogger(xaResource));
    }
}
