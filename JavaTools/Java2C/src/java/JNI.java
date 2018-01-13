/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package com.otpsystems.tools.jni;

import java.lang.reflect.*;

import java.util.Map;

public class JNI
{
    public static String getTypeDescriptor(Class type)
    {
	if (type.isPrimitive()) {
	    if (type == Boolean.TYPE) {
	        return "Z";
	    } else if (type == Byte.TYPE) {
	        return "B";
	    } else if (type == Character.TYPE) {
	        return "C";
	    } else if (type == Short.TYPE) {
	        return "S";
	    } else if (type == Integer.TYPE) {
	        return "I";
	    } else if (type == Long.TYPE) {
	        return "L";
	    } else if (type == Float.TYPE) {
	        return "F";
	    } else if (type == Double.TYPE) {
	        return "D";
	    } else if (type == Void.TYPE) {
		return "V";
	    } else {
		throw new RuntimeException("Unknown primitive type: " + type);
	    }
	} else if (type.isArray()) {
	    return "[" + getTypeDescriptor(type.getComponentType());
	} else {
	    return "L" + type.getName().replace('.', '/') + ";";
	}
    }

    public static String getNativeType(Class type)
    {
	if (type.isPrimitive()) {
	    if (type == Boolean.TYPE) {
	        return "jboolean";
	    } else if (type == Byte.TYPE) {
	        return "jbyte";
	    } else if (type == Character.TYPE) {
	        return "jchar";
	    } else if (type == Short.TYPE) {
	        return "jshort";
	    } else if (type == Integer.TYPE) {
	        return "jint";
	    } else if (type == Long.TYPE) {
	        return "jlong";
	    } else if (type == Float.TYPE) {
	        return "jfloat";
	    } else if (type == Double.TYPE) {
	        return "jdouble";
	    } else if (type == Void.TYPE) {
		return "void";
	    } else {
		throw new RuntimeException("Unknown primitive type: " + type);
	    }
	} else if (type.isArray()) {
	    Class componentType = type.getComponentType();
	    if (componentType.isPrimitive()) {
		return getNativeType(componentType) + "Array";
	    } else {
		return "jobjectArray";
	    }
	} else if (type == Class.class) {
	    return "jclass";
	} else if (type == String.class) {
	    return "jstring";
	} else if (Throwable.class.isAssignableFrom(type)) {
	    return "jthrowable";
	} else {
	    return "jobject";
	}
    }

    public static String getTypeName(Class type)
    {
	if (type.isPrimitive()) {
	    if (type == Boolean.TYPE) {
	        return "Boolean";
	    } else if (type == Byte.TYPE) {
	        return "Byte";
	    } else if (type == Character.TYPE) {
	        return "Char";
	    } else if (type == Short.TYPE) {
	        return "Short";
	    } else if (type == Integer.TYPE) {
	        return "Int";
	    } else if (type == Long.TYPE) {
	        return "Long";
	    } else if (type == Float.TYPE) {
	        return "Float";
	    } else if (type == Double.TYPE) {
	        return "Double";
	    } else if (type == Void.TYPE) {
		return "Void";
	    } else {
		throw new RuntimeException("Unknown primitive type: " + type);
	    }
	} else {
	    return "Object";
	}
    }

    public static String getFieldSignature(Field field)
    {
	return getTypeDescriptor(field.getType());
    }

    public static String getMethodSignature(Method method)
    {
	StringBuffer sig = new StringBuffer();
	sig.append('(');

	Class[] parameterTypes = method.getParameterTypes();
	for (int i = 0; i < parameterTypes.length; i++) {
	    sig.append(getTypeDescriptor(parameterTypes[i]));
	}

	sig.append(')').append(getTypeDescriptor(method.getReturnType()));

	return sig.toString();
    }

    public static String getConstructorSignature(Constructor constructor)
    {
	StringBuffer sig = new StringBuffer();
	sig.append('(');

	Class[] parameterTypes = constructor.getParameterTypes();
	for (int i = 0; i < parameterTypes.length; i++) {
	    sig.append(getTypeDescriptor(parameterTypes[i]));
	}

	sig.append(")V");

	return sig.toString();
    }

    public static String encode(String s)
    {
	return s.replaceAll("_", "_1").replace('.', '_');
    }

    public static String getNativeFunctionName(Method method)
    {
	String className = method.getDeclaringClass().getName();

	return "Java_" + encode(className) + '_' + encode(method.getName());
    }

    // Class.getField() only returns public fields => we need to recursively
    // use getDeclaredField() to access any field.

    public static Field getField(Class cls, String fieldName)
    {
	try {
	    return cls.getDeclaredField(fieldName);
	} catch (NoSuchFieldException eNoSuchField) {
	    Class supercls = cls.getSuperclass();
	    if (supercls == null) {
		throw new RuntimeException(eNoSuchField);
	    }
	    try {
	        return getField(supercls, fieldName);
	    } catch (RuntimeException eRuntime) {
		throw new RuntimeException(eNoSuchField);
	    }
	}
    }

    // Class.getMethod() only returns public methods => we need to recursively
    // use getDeclaredMethod() to access any method. Idem for getConstructor().
 
    public static Method getMethod(Class cls, String methodName,
	Class[] parameterTypes) 
    {
	try {
	    return cls.getDeclaredMethod(methodName, parameterTypes);
	} catch (NoSuchMethodException eNoSuchMethod) {
	    Class supercls = cls.getSuperclass();
	    if (supercls == null) {
		throw new RuntimeException(eNoSuchMethod);
	    }
	    try {
	        return getMethod(supercls, methodName, parameterTypes);
	    } catch (RuntimeException eRuntime) {
		throw new RuntimeException(eNoSuchMethod);
	    }
	}
    }

    // Hmm, this could be wrong.

    public static Constructor getConstructor(Class cls,
	Class[] parameterTypes)
    {
	try {
	    return cls.getDeclaredConstructor(parameterTypes);
	} catch (NoSuchMethodException eNoSuchMethod) {
	    Class supercls = cls.getSuperclass();
	    if (supercls == null) {
		throw new RuntimeException(eNoSuchMethod);
	    }
	    try {
	        return getConstructor(supercls, parameterTypes);
	    } catch (RuntimeException eRuntime) {
		throw new RuntimeException(eNoSuchMethod);
	    }
	}
    }

    public static Class parseType(String type, String packageName,
	Map imports)
    {
	if (type.endsWith("[]")) {
	    String componentType = type.substring(0, type.length() - 2).trim();
	    Class componentClass = parseType(componentType, packageName,
		    imports);
	    return Array.newInstance(componentClass, 0).getClass();
	} else if (type.equals("boolean")) {
	    return Boolean.TYPE;
	} else if (type.equals("byte")) {
	    return Byte.TYPE;
	} else if (type.equals("char")) {
	    return Character.TYPE;
	} else if (type.equals("short")) {
	    return Short.TYPE;
	} else if (type.equals("int")) {
	    return Integer.TYPE;
	} else if (type.equals("long")) {
	    return Long.TYPE;
	} else if (type.equals("float")) {
	    return Float.TYPE;
	} else if (type.equals("double")) {
	    return Double.TYPE;
	} else if (type.equals("void")) {
	    return Void.TYPE;
	} else {
	    if (type.indexOf('.') == -1) {

	        Class cls = (Class) imports.get(type);
	        if (cls != null) {
		    return cls;
		}

		String fullyQualifiedType = (packageName == null) ? type
		    : packageName + '.' + type;

		try {
		    return Class.forName(fullyQualifiedType);
		} catch (ClassNotFoundException eClassNotFound) {
		    // fall through
		}

	        try {
	            return Class.forName("java.lang." + type);
	        } catch (ClassNotFoundException eClassNotFound) {
		        // fall through
	        }
	    } else {
		try {
		    return Class.forName(type);
		} catch (ClassNotFoundException eClassNotFound) {
		    // fall through
		}

	    }

	    throw new RuntimeException("Unknown type: " + type);
	}
    }
}
