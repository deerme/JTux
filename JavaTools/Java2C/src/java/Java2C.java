/*
 * Copyright 2004 OTP Systems Oy. All rights reserved.
 */

package com.otpsystems.tools.jni;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class Java2C
{
    private static final Character SEMI_COLON = new Character(';');
    private static final Character OPEN_BRACE = new Character('{');
    private static final Character CLOSE_BRACE = new Character('}');
    private static final Character OPEN_PAREN = new Character('(');
    private static final Character CLOSE_PAREN = new Character(')');
    private static final Character COMMA = new Character(',');

    private static String outputDirectory;

    private PushbackReader input;

    private String packageName = null;

    private HashMap imports = new HashMap();

    Java2C(Reader input)
    {
	this.input = new PushbackReader(input);
    }

    Object getNextToken() throws IOException
    {
        StringBuffer identifier = null;

        boolean inComment = false;

        while (true) {

	    int c = input.read();
	    if (c == -1) {
	        if (identifier == null) {
		    return null;
		} else {
		    return identifier.toString();
		}
	    }

	    char ch = (char) c;

	    if (inComment) {
		if (ch == '\n') {
		    inComment = false;
		}
		continue;
	    }

	    if (ch == '#') {
		inComment = true;
		continue;
	    }

	    if (identifier != null) {
		if (Character.isJavaIdentifierPart(ch) || (ch == '.')
			|| (ch == '[') || (ch == ']')) {
		    identifier.append(ch);
		} else {
		    input.unread(c);
		    return identifier.toString();
		}
	    } else {
		if (Character.isJavaIdentifierStart(ch)) {
		    identifier = new StringBuffer();
		    identifier.append(ch);
		} else if (!Character.isWhitespace(ch)) {
		    return new Character(ch);
		}
	    }
	}
    }

    Object nextToken() throws IOException
    {
	Object token = getNextToken();
	if (token == null) {
	    throw new RuntimeException("Unexpected end of file");
	}
	return token;
    }

    void skip(Character ch) throws IOException
    {
	Object token = nextToken();
	if (!token.equals(ch)) {
	    throw new RuntimeException("Unexpected token: " + token
		+ ", expected " + ch);
	}
    }

    String readIdentifier() throws IOException
    {
	Object token = nextToken();
	if (token instanceof String) {
	    return (String) token;
	}
	throw new RuntimeException("Unexpected token: " + token
	    + ", expected identifier");
    }

    Class[] readTypeList() throws IOException
    {
	skip(OPEN_PAREN);

	LinkedList types = new LinkedList();

	Object token = nextToken();
	if (!token.equals(CLOSE_PAREN)) {
	    while (true) {
	        if (token instanceof String) {
		    types.add(JNI.parseType((String) token, packageName, 
			imports));
	        } else {
		    throw new RuntimeException("Unexpected token: " + token
			+ ", expected identifier");
	        }
		token = nextToken();
		if (token.equals(CLOSE_PAREN)) {
		    break;
		}
		if (!token.equals(COMMA)) {
		    throw new RuntimeException("Unexpected token: " + token
			+ ", expected " + COMMA + " or " + CLOSE_PAREN);
		}
		token = nextToken();
	    }
	}

	return (Class[]) types.toArray(new Class[0]);
    }

    void processPackageStatement() throws IOException
    {
	packageName = readIdentifier();
	skip(SEMI_COLON);
    }

    void processImportStatement() throws IOException
    {
	String fullClassName = readIdentifier();
	skip(SEMI_COLON);

	int lastDotPos = fullClassName.lastIndexOf('.');
	String className = (lastDotPos == -1) ? fullClassName
	    : fullClassName.substring(lastDotPos + 1);
	try {
	    imports.put(className, Class.forName(fullClassName));
	} catch (ClassNotFoundException eClassNotFound) {
	    throw new RuntimeException("No such class: " + fullClassName);
	}
    }

    void processClassAPIDefinition() throws IOException
    {
	String className = readIdentifier();
	if (packageName != null) {
	    className = packageName + '.' + className;
	}

	ClassAPIDefinition classAPIDefinition = new ClassAPIDefinition(
	    className);

	classAPIDefinition.read();

	classAPIDefinition.createCAPIFiles();
    }

    void process() throws IOException
    {
	Object token = getNextToken();
	while (token != null) {
	    if (token.equals("package")) {
		processPackageStatement();
	    } else if (token.equals("import")) {
		processImportStatement();
	    } else if (token.equals("class")) {
		processClassAPIDefinition();
	    } else {
		throw new RuntimeException("Unexpected token: " + token
		    + ", expected 'package', 'import' or 'class'");
	    }
	    token = getNextToken();
	}
    }

    static int year = Calendar.getInstance().get(Calendar.YEAR);

    class ClassAPIDefinition
    {
	private String className;

	private Class cls;

	private LinkedList members = new LinkedList();

	private HashMap aliases = new HashMap();

	private String packageName;

	private String usClassName;

	private String cache;

	ClassAPIDefinition(String className) 
	{
	    this.className = className;

	    try {
	        cls = Class.forName(className);
	    } catch (ClassNotFoundException eClassNotFound) {
		throw new RuntimeException("No such class: " + cls);
	    }

	    int dotPos = className.lastIndexOf('.');
	    if (dotPos == -1) {
		packageName = null;
	    } else {
		packageName = className.substring(0, dotPos);
	    }

	    usClassName = className.replace('.', '_');

	    cache = usClassName + "_cache";
	}

	void read() throws IOException
	{
	    skip(OPEN_BRACE);

	    Object token = nextToken();

	    while (!token.equals(CLOSE_BRACE)) {

		Member member;

		if (token.equals("field")) {
		    member = readField();
		} else if (token.equals("constructor")) {
		    member = readConstructor();
		} else if (token.equals("method")) {
		    member = readMethod();
		} else {
		    throw new RuntimeException("Unexpected token: " + token
			+ ", expected 'field', 'constructor' or 'method'");
		}

		members.add(member);

		token = nextToken();
		if (token.equals("alias")) {
		    String alias = readIdentifier();
		    aliases.put(member, alias);
		    skip(SEMI_COLON);
		} else if (!token.equals(SEMI_COLON)) {
		    throw new RuntimeException("Unexpected token: " + token
			+ ", expected 'alias' or " + SEMI_COLON);
		}

		token = nextToken();
	    }
	}

	Field readField() throws IOException
	{
	    return JNI.getField(cls, readIdentifier());
	}

	Constructor readConstructor() throws IOException
	{
	    return JNI.getConstructor(cls, readTypeList());
	}

	Method readMethod() throws IOException
	{
	    return JNI.getMethod(cls, readIdentifier(), readTypeList());
	}

	private PrintWriter out = null;

	private void P()
	{
	    out.println();
	}

	private void P(String s)
	{
	    out.println(s);
	}

        void printLegalInfo()
	{
	    P("/*");
	    P(" * Copyright " + year + " OTP Systems Oy. "
		+ "All rights reserved.");
	    P(" */");
	}

	void createCAPIFiles() throws IOException
	{
	    createHFile();
	    createCFile();
	}

	void createHFile() throws IOException
	{
	    File file = new File(outputDirectory, usClassName + ".h");
	    FileWriter writer = new FileWriter(file);
	    try {
		out = new PrintWriter(new CodeWriter(writer));
		writeHFile();
	    } finally {
		writer.close();
	    }
	}

	void createCFile() throws IOException
	{
	    File file = new File(outputDirectory, usClassName + ".c");
	    FileWriter writer = new FileWriter(file);
	    try {
		out = new PrintWriter(new CodeWriter(writer));
		writeCFile();
	    } finally {
		writer.close();
	    }
	}

        String makeParameterList(Class[] parameterTypes)
        {
	    StringBuffer buffer = new StringBuffer();

	    for (int i = 0; i < parameterTypes.length; i++) {
	        buffer.append(", ");
	        buffer.append(JNI.getNativeType(parameterTypes[i]));
	        buffer.append(" arg").append(i);
	    }

	    return buffer.toString();
        }

        String makeArgumentList(Class[] parameterTypes)
        {
	    StringBuffer buffer = new StringBuffer();

	    for (int i = 0; i < parameterTypes.length; i++) {
	        buffer.append(", arg").append(i);
	    }
	    
	    return buffer.toString();
	}

	String getMemberName(Member member)
	{
	    String alias = (String) aliases.get(member);
	    if (alias == null) {
		// The Java documentation says that the getName() method
		// of a constructor returns the simple name of the
		// constructor's declaring class. In practice this turns
		// out to be a fully qualified class name.
		// To resolve this problem, we always check the result
		// of Member.getName().
		String memberName = member.getName();
		int dotPos = memberName.lastIndexOf('.');
		if (dotPos == -1) {
		    return memberName;
		} else {
		    return memberName.substring(dotPos + 1);
		}
	    } else {
		return alias;
	    }
	}

	boolean isStatic(Member member)
	{
	    return Modifier.isStatic(member.getModifiers());
	}

	void writeMemberAccessorDeclaration(Member member)
	{
	    if (member instanceof Field) {
		P("OTP_FUNC_DECL");
		P(makeFieldGetterPrototype((Field) member) + ";");
		P();
		P("OTP_FUNC_DECL");
		P(makeFieldSetterPrototype((Field) member) + ";");
	    } else if (member instanceof Constructor) {
		P("OTP_FUNC_DECL");
		P(makeObjectCreatorPrototype((Constructor) member) + ";");
	    } else {
		P("OTP_FUNC_DECL");
		P(makeMethodInvokerPrototype((Method) member) + ";");
	    }
	}

	String makeFieldGetterPrototype(Field field)
	{
	    String prototype = JNI.getNativeType(field.getType()) + ' '
		+ usClassName + "_get_" + getMemberName(field) 
		+ "(JNIEnv *env";
	    if (!isStatic(field)) {
	       prototype = prototype + ", jobject obj";
	    }
	    return prototype + ')';
	}

	String makeFieldSetterPrototype(Field field)
	{
	    String prototype = "void " + usClassName + "_set_" 
		+ getMemberName(field) + "(JNIEnv *env";
	    if (!isStatic(field)) {
	       prototype = prototype + ", jobject obj";
	    }
	    return prototype + ", " + JNI.getNativeType(field.getType())
		+ " value)";
	}

	String makeObjectCreatorPrototype(Constructor constructor)
	{
	    return JNI.getNativeType(cls) + ' ' + usClassName + "_new_"
		+ getMemberName(constructor) + "(JNIEnv *env"
		+ makeParameterList(constructor.getParameterTypes()) + ')';
	}

	String makeMethodInvokerPrototype(Method method)
	{
	    String prototype = JNI.getNativeType(method.getReturnType()) + ' '
		+ usClassName + "_call_" + getMemberName(method)
		+ "(JNIEnv *env";
	    if (!isStatic(method)) {
	       prototype = prototype + ", jobject obj";
	    }
	    return prototype + makeParameterList(method.getParameterTypes())
		+ ')';
	}

	void writeMemberFieldDeclaration(Member member)
	{
	    String name = getMemberName(member);

	    if (member instanceof Field) {
		P("jfieldID f_" + name + ";");
	    } else if (member instanceof Constructor) {
		P("jmethodID c_" + name + ";");
	    } else {
		P("jmethodID m_" + name + ";");
	    }
	}

	void writeMemberAccessorImplementation(Member member)
	{
	    if (member instanceof Field) {
		Field field = (Field) member;
		P("OTP_FUNC_DEF");
		P(makeFieldGetterPrototype(field));
		writeFieldGetterBody(field);
		P();
		P("OTP_FUNC_DEF");
		P(makeFieldSetterPrototype(field));
		writeFieldSetterBody(field);
	    } else if (member instanceof Constructor) {
		Constructor constructor = (Constructor) member;
		P("OTP_FUNC_DEF");
		P(makeObjectCreatorPrototype(constructor));
		writeObjectCreatorBody(constructor);
	    } else {
		Method method = (Method) member;
		P("OTP_FUNC_DEF");
		P(makeMethodInvokerPrototype(method));
		writeMethodInvokerBody(method);
	    }
	}

	void writeInitCheck(boolean returnValue)
	{
	    P("if (" + cache + ".cls == NULL) {");
	    P("    if (" + cache + "_init(env) == -1) {");
	    P("        " + (returnValue ? "return 0;" : "return;"));
	    P("    }");
	    P("}");
	}

	void writeFieldGetterBody(Field field)
	{
	    String type = JNI.getTypeName(field.getType());
	    String name = getMemberName(field);

	    P("{");
	    writeInitCheck(true);
	    if (isStatic(field)) {
		P("return (*env)->GetStatic" + type + "Field(env, " + cache 
		    + ".cls, " + cache +  ".f_" + name + ");");
	    } else {
		P("return (*env)->Get" + type + "Field(env, obj, " + cache 
		    + ".f_" + name + ");");
	    }
	    P("}");
	}

	void writeFieldSetterBody(Field field)
	{
	    String type = JNI.getTypeName(field.getType());
	    String name = getMemberName(field);

	    P("{");
	    writeInitCheck(false);
	    if (isStatic(field)) {
		P("(*env)->SetStatic" + type + "Field(env, " + cache
		   + ".cls, " + cache + ".f_" + name + ", value);");
	    } else {
		P("(*env)->Set" + type + "Field(env, obj, " + cache
		   + ".f_" + name + ", value);");
	    }
	    P("}");
	}

	void writeObjectCreatorBody(Constructor constructor)
	{
	    String name = getMemberName(constructor);
	    String args = makeArgumentList(constructor.getParameterTypes());

	    P("{");
	    writeInitCheck(true);
	    P("    return (*env)->NewObject(env, " + cache + ".cls, " 
		+ cache + ".c_" + name + args + ");");
	    P("}");
	}

	void writeMethodInvokerBody(Method method)
	{
	    String returnType = JNI.getTypeName(method.getReturnType());
	    String name = getMemberName(method);
	    String args = makeArgumentList(method.getParameterTypes());

	    P("{");
	    if (method.getReturnType() == Void.TYPE) {
	        writeInitCheck(false);
		if (isStatic(method)) {
		    P("(*env)->CallStaticVoidMethod(env, " + cache + ".cls, "
		        + cache + ".m_" + name + args + ");");
		} else {
		    P("(*env)->CallVoidMethod(env, obj, " + cache +  ".m_" 
			+ name + args + ");");
		}
	    } else {
	        writeInitCheck(true);
		if (isStatic(method)) {
		    P("return (*env)->CallStatic" + returnType + "Method("
			+ "env, " + cache + ".cls, " + cache + ".m_" 
			+ name + args + ");");
		} else {
		    P("return (*env)->Call" + returnType + "Method("
			+ "env, obj, " + cache + ".m_" + name + args + ");");
		}
	    }
	    P("}");
	}

	void writeMemberFieldInitializer(Member member)
	{
	    if (member instanceof Field) {
		writeFieldMemberInitializer((Field) member);
	    } else if (member instanceof Constructor) {
		writeConstructorMemberInitializer((Constructor) member);
	    } else {
		writeMethodMemberInitializer((Method) member);
	    }
	}

	void writeFieldMemberInitializer(Field field)
	{
	    boolean isStatic = isStatic(field);
	    String name = getMemberName(field);

	    String getFieldId = isStatic ? "GetStaticFieldID" : "GetFieldID";

	    P(cache + ".f_" + name + " = (*env)->" + getFieldId 
		+ "(env, cls, \"" + field.getName()  + "\", \""
		+ JNI.getFieldSignature(field) + "\");");
	    P("if (" + cache + ".f_" + name + " == NULL) {");
	    P("    goto exception_after_monitor;");
	    P("}");
	}

	void writeConstructorMemberInitializer(Constructor constructor)
	{
	    String name = getMemberName(constructor);

	    P(cache + ".c_" + name + " = (*env)->GetMethodID(env, cls, "
		+ "\"<init>\", \"" + JNI.getConstructorSignature(constructor)
	       	+ "\");");
	    P("if (" + cache + ".c_" + name + " == NULL) {");
	    P("    goto exception_after_monitor;");
	    P("}");
	}

	void writeMethodMemberInitializer(Method method)
	{
	    boolean isStatic = isStatic(method);
	    String name = getMemberName(method);

	    String getMethodId = isStatic ? "GetStaticMethodID"
	       	: "GetMethodID";
	    P(cache + ".m_" + name + " = (*env)->" + getMethodId + "(env, cls, "
		+ "\"" + method.getName() + "\", \""
		+ JNI.getMethodSignature(method) + "\");");
	    P("if (" + cache + ".m_" + name + " == NULL) {");
	    P("    goto exception_after_monitor;");
	    P("}");
	}

	void writeHFile() throws IOException
	{
	    printLegalInfo();

	    P();
	    P("#ifndef " + usClassName + "_h_included");
	    P("#define " + usClassName + "_h_included");

	    P();
	    P("#ifndef OTP_FUNC_DECL");
	    P("#define OTP_FUNC_DECL /* empty */");
	    P("#endif");

	    P();
	    P("#include <jni.h>");

	    P();
	    P("OTP_FUNC_DECL");
            P("jclass " + usClassName + "_class(JNIEnv *env);");

	    P();
	    P("OTP_FUNC_DECL");
            P("jobject " + usClassName + "_new(JNIEnv *env);");

	    Iterator it = members.iterator();
	    while (it.hasNext()) {
		P();
		writeMemberAccessorDeclaration((Member) it.next());
	    }

	    P();
            P("#endif /* " + usClassName + "_h_included */");
	}

	void writeCFile() throws IOException
	{
	    printLegalInfo();

	    P();
	    P("#ifndef OTP_FUNC_DEF");
	    P("#define OTP_FUNC_DEF /* empty */");
	    P("#endif");
	    P();
	    P("#include \"" + usClassName + ".h\"");
	    P();
	    P("#include <jni.h>");

	    P();
	    P("typedef struct " + usClassName + " {");
	    P("    jclass cls;");
	    Iterator it = members.iterator();
	    while (it.hasNext()) {
		writeMemberFieldDeclaration((Member) it.next());
	    }
            P("} " + usClassName + ";");

	    // There is no need to explicitly initialize the static cache
	    // as all fields are guaranteed to be initialized to NULL
	    // (see ANSI C FAQ for example).

	    P();
	    P("static " + usClassName + " " + cache + ";");

    	    /*
     	     * This is tricky. This method may be called by different
     	     * threads at the same time. To prevent a race condition,
     	     * we grab the monitor of the local class reference before 
     	     * touching the global cache variable.
     	     */

	    P();
	    P("static int " + cache + "_init(JNIEnv *env)");
	    P("{");
            P("    jclass cls = (*env)->FindClass(env, \"" 
	        + cls.getName().replace('.', '/') + "\");");
            P("    if (cls == NULL) {");
	    P("        goto exception;");
	    P("    }");
	    P();
            P("    if ((*env)->MonitorEnter(env, cls) != 0) {");
	    P("        goto exception_after_cls;");
	    P("    }");
	    P();
	    // Extra check on cache.cls needed to prevent race condition
	    P("    if (" + cache + ".cls == NULL) {");
	    it = members.iterator();
	    while (it.hasNext()) {
		P();
		writeMemberFieldInitializer((Member) it.next());
	    }
	    P();
	    P("        " + cache + ".cls = (*env)->NewGlobalRef(env, cls);");
	    P("        if (" + cache + ".cls == NULL) {");
	    P("            goto exception_after_monitor;");
	    P("        }");
	    P("    }");
	    P();
            P("    (*env)->MonitorExit(env, cls);");
	    P();
            P("    (*env)->DeleteLocalRef(env, cls);");
	    P();
	    P("    return 0;");
	    P();
	    P("exception_after_monitor:");
	    P();
	    P("    (*env)->MonitorExit(env, cls);");
	    P();
	    P("exception_after_cls:");
	    P();
	    P("    (*env)->DeleteLocalRef(env, cls);");
	    P();
	    P("exception:");
	    P();
	    P("    return -1;");
	    P("}");

            P();
	    P("OTP_FUNC_DEF");
            P("jclass " + usClassName + "_class(JNIEnv *env)");
            P("{");
	    writeInitCheck(true);
            P("    return " + cache + ".cls;");
            P("}");

            P();
	    P("OTP_FUNC_DEF");
            P("jobject " + usClassName + "_new(JNIEnv *env)");
            P("{");
	    writeInitCheck(true);
            P("    return (*env)->AllocObject(env, " + cache + ".cls);");
            P("}");

	    it = members.iterator();
	    while (it.hasNext()) {
		P();
		writeMemberAccessorImplementation((Member) it.next());
	    }
	}
    }

    static int configure(String[] args) 
    {
	int i = 0;
	while (i < args.length) {
	    if (args[i].equals("-d")) {
		if (outputDirectory != null) {
		    System.err.println("duplicate -d");
		    System.exit(1);
		}
		i++;
		if (i == args.length) {
		    System.err.println("-d argument missing");
		    System.exit(1);
		}
		outputDirectory = args[i];
	    } else {
		break;
	    }
	    i++;
	}
	return i;
    }

    static void process(String fileName) throws IOException
    {
	FileReader reader = new FileReader(fileName);
	try {
	    LineNumberReader input = new LineNumberReader(reader);
	    input.setLineNumber(1); // Default starts at 0
	    try {
		new Java2C(input).process();
	    } catch (RuntimeException eRuntime) {
		System.err.println("Error in " + fileName + " at line "
		    + input.getLineNumber());
		throw eRuntime;
	    }
	} finally {
	    reader.close();
	}	
    }

    public static void main(String[] args) throws IOException
    {
	int i = configure(args);

	while (i < args.length) {
	    process(args[i]);
	    i++;
	}
    }
}
