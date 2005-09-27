/* ====================================================================
 * Copyright 2005 NanoContainer Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nanocontainer.remoting.tools.generator;

import org.nanocontainer.remoting.common.MethodNameHelper;
import org.nanocontainer.remoting.server.ProxyGenerationException;
import org.nanocontainer.remoting.server.PublicationDescriptionItem;
import org.nanocontainer.remoting.tools.javacompiler.JavaCompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Vector;


/**
 * Class ProxyGeneratorImpl
 *
 * @author Paul Hammant
 * @author Mike Miller of www.gac.com
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @version $Revision: 1.3 $
 */
public class ProxyGeneratorImpl extends AbstractProxyGenerator {

    /**
     * Method generate
     *
     * @param classLoader the class loader to use.
     * @throws ProxyGenerationException if a problem during generation.
     */
    public void generateSrc(ClassLoader classLoader) throws ProxyGenerationException {

        // The lookupable service class source.
        makeSource(classLoader, "Main", getInterfacesToExpose());

        if (getAdditionalFacades() != null) {
            for (int i = 0; i < getAdditionalFacades().length; i++) {
                PublicationDescriptionItem facade = getAdditionalFacades()[i];

                makeSource(classLoader, MethodNameHelper.encodeClassName(facade.getFacadeClass()), new PublicationDescriptionItem[]{facade});
            }
        }
        if (getCallbackFacades() != null) {
            for (int i = 0; i < getCallbackFacades().length; i++) {
                PublicationDescriptionItem facade = getCallbackFacades()[i];

                makeSource(classLoader, MethodNameHelper.encodeClassName(facade.getFacadeClass()), new PublicationDescriptionItem[]{facade});
            }
        }

    }

    /**
     * Method generateClass
     *
     * @param classLoader the class loader to use.
     */
    public void generateClass(ClassLoader classLoader) {

        JavaCompiler jc = JavaCompiler.getDefaultCompiler();

        jc.setOutputDir(getClassGenDir());

        //jc.setM_compilerPath();
        jc.addClassPath(getClassGenDir());
        jc.addClassPath(getClasspath());
        jc.addDefaultClassPath();
        jc.doCompile(getSrcGenDir() + File.separator + "NanoContainerRemotingGenerated" + getGenName() + "_Main.java");
        System.out.println(jc.getCompilerMessage());

        if (getAdditionalFacades() != null) {
            for (int i = 0; i < getAdditionalFacades().length; i++) {
                String classname = MethodNameHelper.encodeClassName(getAdditionalFacades()[i].getFacadeClass());

                jc.doCompile(getSrcGenDir() + File.separator + "NanoContainerRemotingGenerated" + getGenName() + "_" + classname + ".java");
                System.out.println(jc.getCompilerMessage());
            }
        }
        if (getCallbackFacades() != null) {
            for (int i = 0; i < getCallbackFacades().length; i++) {
                String classname = MethodNameHelper.encodeClassName(getCallbackFacades()[i].getFacadeClass());

                jc.doCompile(getSrcGenDir() + File.separator + "NanoContainerRemotingGenerated" + getGenName() + "_" + classname + ".java");
                System.out.println(jc.getCompilerMessage());
            }
        }

    }

    /**
     * Method generateDeferredClasses
     */
    public void generateDeferredClasses() {

        JavaCompiler jc = JavaCompiler.getDefaultCompiler();

        jc.setOutputDir(getClassGenDir());
        jc.setCompilerPath(getSrcGenDir());
        jc.addClassPath(getClassGenDir());
        jc.addClassPath(getClasspath());
        jc.addDefaultClassPath();
        jc.doCompile("*.java");
        System.out.println(jc.getCompilerMessage());
    }

    /**
     * Method makeSource
     *
     * @param classloader        the classloader to use
     * @param name               the name of the source file.
     * @param interfacesToExpose the interfaces to expose.
     * @throws ProxyGenerationException if generation not possible.
     */
    public void makeSource(ClassLoader classloader, String name, PublicationDescriptionItem[] interfacesToExpose) throws ProxyGenerationException {

        // methdos could be in more than one interface.
        Vector methodsDone = new Vector();
        String filename = getSrcGenDir() + File.separator + "NanoContainerRemotingGenerated" + getGenName() + "_" + name + ".java";

        PrintWriter classSource = null;
        try {
            classSource = new PrintWriter(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            throw new ProxyGenerationException("Cannot make source output file '" + filename + "'");
        }

        classSource.print("public final class NanoContainerRemotingGenerated" + getGenName() + "_" + name);
        classSource.println(" implements org.nanocontainer.remoting.client.Proxy, ");
        generateInterfaceList(classSource, interfacesToExpose);
        classSource.println(" { ");
        classSource.println("  private transient org.nanocontainer.remoting.client.ProxyHelper m_proxyHelper;");


        // Generate Constructor
        classSource.println("  public NanoContainerRemotingGenerated" + getGenName() + "_" + name + " (org.nanocontainer.remoting.client.ProxyHelper proxyHelper) {");
        classSource.println("      m_proxyHelper = proxyHelper;");
        classSource.println("  }");

        // helper method for the m_factory.
        // from Proxy interface
        classSource.println("    public Long nanocontainerRemotingGetReferenceID(Object factoryThatIsAsking) {");
        classSource.println("        return m_proxyHelper.getReferenceID(factoryThatIsAsking);");
        classSource.println("    }");

        makeSourceInterfaces(classSource, interfacesToExpose, methodsDone);

        classSource.print("}");
        classSource.close();
    }

    private void makeSourceInterfaces(PrintWriter classSource, PublicationDescriptionItem[] interfacesToExpose, Vector methodsDone) throws ProxyGenerationException {

        generateEqualsMethod(classSource);
        methodsDone.add("equals(java.lang.Object)");

        for (int x = 0; x < interfacesToExpose.length; x++) {
            PublicationDescriptionItem interfaceToExpose = interfacesToExpose[x];
            Class clazz = interfaceToExpose.getFacadeClass();

            if (isVerbose()) {
                System.out.println("ProxyGen: Processing interface: " + clazz.getName());
            }

            Method[] methods = getGeneratableMethods(clazz);

            for (int y = 0; y < methods.length; y++) {
                Method method = methods[y];
                String methodSignature = MethodNameHelper.getMethodSignature(method);

                if (isVerbose()) {
                    System.out.println("ProxyGen:   Processing method: " + methodSignature);
                }

                if (!methodsDone.contains(methodSignature)) {
                    makeSourceInterfacesMethodsNotDone(classSource, methodsDone, methodSignature, method, interfaceToExpose.isAsync(method), interfaceToExpose.isCommit(method), interfaceToExpose.isRollback(method));

                }
            }
        }
    }

    private void generateEqualsMethod(PrintWriter classSource) {
        classSource.println("  public boolean equals(Object o) {");
        classSource.println("    return m_proxyHelper.isEquals(this,o);");
        classSource.println("  }");
    }

    private void makeSourceInterfacesMethodsNotDone(PrintWriter classSource, Vector methodsDone, String methodSignature, Method method, boolean async, boolean commit, boolean rollback) throws ProxyGenerationException {
        methodsDone.add(methodSignature);

        Class rClass = method.getReturnType();

        if (!(method.getReturnType() instanceof Serializable)) {
            throw new ProxyGenerationException("Return type " + rClass + " must be serializable");
        }

        String mName = method.getName();

        classSource.print("  public " + generateReturnValue(rClass) + " " + mName + " (");

        Class[] argTypes = method.getParameterTypes();

        for (int i = 0; i < argTypes.length; i++) {
            String cn = (argTypes[i].isArray() ? argTypes[i].getComponentType().getName() : argTypes[i].getName());

            if (!(argTypes[i] instanceof Serializable)) {
                throw new ProxyGenerationException("Argument type " + cn + " must be serializable");
            }

            generateParameter(classSource, argTypes[i], i);
        }

        classSource.print(") ");

        Class[] throwsTypes = method.getExceptionTypes();

        for (int i = 0; i < throwsTypes.length; i++) {
            generateThrows(classSource, i, throwsTypes);
        }

        classSource.println("{");
        if (async) {
            generateAsyncMethodBody(classSource, argTypes, rClass, methodSignature, method);
        } else if (commit) {
            generateAsyncMethodBody(classSource, argTypes, rClass, methodSignature, method);
            generateAsyncCommitMethodBody(classSource);
        } else if (rollback) {
            generateAsyncRollbackMethodBody(classSource);
            generateNonAsyncMethodBody(classSource, argTypes, rClass, methodSignature, method);
        } else {
            generateNonAsyncMethodBody(classSource, argTypes, rClass, methodSignature, method);
        }
        classSource.println("  " + "" + "" + "}");
    }

    private void generateNonAsyncMethodBody(PrintWriter classSource, Class[] argTypes, Class rClass, String methodSignature, Method method) {

        generateMethodBody(classSource, argTypes, rClass, methodSignature, method, "processVoidRequest");

    }

    private void generateAsyncMethodBody(PrintWriter classSource, Class[] argTypes, Class rClass, String methodSignature, Method method) {

        generateMethodBody(classSource, argTypes, rClass, methodSignature, method, "queueAsyncRequest");

    }

    private void generateMethodBody(PrintWriter classSource, Class[] argTypes, Class rClass, String methodSignature, Method method, String proxyMethodName) {
        Class[] throwsTypes;
        generateMethodArgs(classSource, argTypes);

        classSource.println("    try {");

        if (rClass.toString().equals("void")) {
            classSource.println("      m_proxyHelper." + proxyMethodName + "(\"" + methodSignature + "\",args,argClasses);");
        } else {
            makeSourceInterfacesMethodsNotDoneNotVoid(classSource, method, rClass, methodSignature);

        }

        classSource.println("    } catch (Throwable t) {");

        throwsTypes = method.getExceptionTypes();

        for (int i = 0; i < throwsTypes.length; i++) {
            generateThrowHandler(classSource, i, throwsTypes);
        }

        classSource.println("      if (t instanceof RuntimeException) { ");
        classSource.println("        throw (RuntimeException) t;");
        classSource.println("      } else if (t instanceof Error) { ");
        classSource.println("        throw (Error) t;");
        classSource.println("      } else { ");
        classSource.println("        t.printStackTrace(); ");
        classSource.println("        throw new org.nanocontainer.remoting.client." + "InvocationException(\"Should never get here: \" +t.getMessage());");
        classSource.println("      }");
        classSource.println("    }");
    }

    private void generateMethodArgs(PrintWriter classSource, Class[] argTypes) {
        classSource.println("    Object[] args = new Object[" + argTypes.length + "];");
        classSource.println("    Class[] argClasses = new Class[" + argTypes.length + "];");

        for (int i = 0; i < argTypes.length; i++) {
            generateAssignLine(classSource, argTypes[i], i);
        }
    }


    private void generateAsyncCommitMethodBody(PrintWriter classSource) {
        classSource.println("    try {");

        classSource.println("      m_proxyHelper.commitAsyncRequests();");
        classSource.println("    } catch (Throwable t) {");
        classSource.println("      if (t instanceof RuntimeException) { ");
        classSource.println("        throw (RuntimeException) t;");
        classSource.println("      } else if (t instanceof Error) { ");
        classSource.println("        throw (Error) t;");
        classSource.println("      } else { ");
        classSource.println("        t.printStackTrace(); ");
        classSource.println("        throw new org.nanocontainer.remoting.client." + "InvocationException(\"Should never get here: \" +t.getMessage());");
        classSource.println("      }");
        classSource.println("    }");

    }

    private void generateAsyncRollbackMethodBody(PrintWriter classSource) {
        Vector v = new Vector();
        v.removeAllElements();

        classSource.println("    try {");
        classSource.println("      m_proxyHelper.rollbackAsyncRequests();");
        classSource.println("    } catch (Throwable t) {");
        classSource.println("      if (t instanceof RuntimeException) { ");
        classSource.println("        throw (RuntimeException) t;");
        classSource.println("      } else if (t instanceof Error) { ");
        classSource.println("        throw (Error) t;");
        classSource.println("      } else { ");
        classSource.println("        t.printStackTrace(); ");
        classSource.println("        throw new org.nanocontainer.remoting.client." + "InvocationException(\"Should never get here: \" +t.getMessage());");
        classSource.println("      }");
        classSource.println("    }");

    }


    private void makeSourceInterfacesMethodsNotDoneNotVoid(PrintWriter classSource, Method method, Class rClass, String methodSignature) {
        boolean isFacadeRetVal = isAdditionalFacade(method.getReturnType());

        if (isFacadeRetVal) {
            Class retClassType = rClass;
            if (rClass.isArray()) {
                retClassType = rClass.getComponentType();
            }

            classSource.println("  Object retVal = m_proxyHelper.processObjectRequestGettingFacade(" + retClassType.getName() + ".class , \"" + methodSignature + "\",args,\"" + MethodNameHelper.encodeClassName(getClassType(rClass)) + "\");");
            classSource.println("      return (" + getClassType(rClass) + ") retVal;");
        } else {
            classSource.println("      Object retVal = m_proxyHelper.processObjectRequest(\"" + methodSignature + "\",args,argClasses);");
            generateReturnLine(classSource, rClass);
        }
    }

    private void generateThrowHandler(PrintWriter classSource, int i, Class[] throwsTypes) {

        if (i == 0) {
            classSource.println("      if (t instanceof " + throwsTypes[i].getName() + ") { ");
        } else {
            classSource.println("      } else if (t instanceof " + throwsTypes[i].getName() + ") { ");
        }

        classSource.println("        throw (" + throwsTypes[i].getName() + ") t;");

        if (i + 1 == throwsTypes.length) {
            classSource.print("      } else");
        }
    }

    private void generateThrows(PrintWriter classSource, int i, Class[] throwsTypes) {

        if (i == 0) {
            classSource.print("throws ");
        }

        classSource.print(throwsTypes[i].getName());

        if (i + 1 < throwsTypes.length) {
            classSource.print(", ");
        }
    }

    private void generateInterfaceList(PrintWriter classSource, PublicationDescriptionItem[] interfacesToExpose) {

        for (int x = 0; x < interfacesToExpose.length; x++) {
            classSource.print(interfacesToExpose[x].getFacadeClass().getName());

            if (x + 1 < interfacesToExpose.length) {
                classSource.print(", ");
            }
        }
    }

    private void generateParameter(PrintWriter classSource, Class cl, int i) {

        if (i > 0) {
            classSource.print(", ");
        }

        if (cl.isArray()) {
            classSource.print(cl.getComponentType().getName() + "[]");
        } else {
            classSource.print(cl.getName());
        }

        classSource.print(" v" + i);
    }

    private void generateAssignLine(PrintWriter classSource, Class clazz, int i) {

        String cn = clazz.getName();

        if (cn.equals("int")) {
            classSource.println("    args[" + i + "] = new Integer(v" + i + ");");
        } else if (cn.equals("short")) {
            classSource.println("    args[" + i + "] = new Short(v" + i + ");");
        } else if (cn.equals("float")) {
            classSource.println("    args[" + i + "] = new Float(v" + i + ");");
        } else if (cn.equals("double")) {
            classSource.println("    args[" + i + "] = new Double(v" + i + ");");
        } else if (cn.equals("long")) {
            classSource.println("    args[" + i + "] = new Long(v" + i + ");");
        } else if (cn.equals("char")) {
            classSource.println("    args[" + i + "] = new Character(v" + i + ");");
        } else if (cn.equals("boolean")) {
            classSource.println("    args[" + i + "] = new Boolean(v" + i + ");");
        } else if (cn.equals("byte")) {
            classSource.println("    args[" + i + "] = new Byte(v" + i + ");");
        } else {
            classSource.println("    args[" + i + "] = v" + i + ";");
            classSource.println("    argClasses[" + i + "] = " + generateReturnValue(clazz) + ".class;");
        }
    }

    private void generateReturnLine(PrintWriter classSource, Class rClass) {

        String cn = rClass.getName();

        if (cn.equals("boolean")) {
            classSource.println("      return ((Boolean) retVal).booleanValue();");
        } else if (cn.equals("int")) {
            classSource.println("      return ((Integer) retVal).intValue();");
        } else if (cn.equals("short")) {
            classSource.println("      return ((Short) retVal).shortValue();");
        } else if (cn.equals("float")) {
            classSource.println("      return ((Float) retVal).floatValue();");
        } else if (cn.equals("double")) {
            classSource.println("      return ((Double) retVal).doubleValue();");
        } else if (cn.equals("long")) {
            classSource.println("      return ((Long) retVal).longValue();");
        } else if (cn.equals("char")) {
            classSource.println("      return ((Character) retVal).charValue();");
        } else if (cn.equals("void")) {
            classSource.println("      return;");
        } else if (cn.equals("byte")) {
            classSource.println("      return ((Byte) retVal).byteValue();");
        } else if (cn.equals("[B")) {
            classSource.println("      return (byte[]) retVal;");
        } else if (cn.equals("[C")) {
            classSource.println("      return (char[]) retVal;");
        } else if (cn.equals("[D")) {
            classSource.println("      return (double[]) retVal;");
        } else if (cn.equals("[F")) {
            classSource.println("      return (float[]) retVal;");
        } else if (cn.equals("[I")) {
            classSource.println("      return (int[]) retVal;");
        } else if (cn.equals("[J")) {
            classSource.println("      return (long[]) retVal;");
        } else if (cn.equals("[S")) {
            classSource.println("      return (short[]) retVal;");
        } else if (cn.equals("[Z")) {
            classSource.println("      return (boolean[]) retVal;");
        } else if (rClass.getName().startsWith("[L")) {
            classSource.println("      return (" + cn.substring(2, cn.length() - 1) + "[]) retVal;");
        } else {
            classSource.println("      return (" + cn + ") retVal;");
        }
    }


    private String generateReturnValue(Class rClass) {

        String cn = rClass.getName();

        if (cn.equals("[B")) {
            return "byte[]";
        } else if (cn.equals("[C")) {
            return "char[]";
        } else if (cn.equals("[D")) {
            return "double[]";
        } else if (cn.equals("[F")) {
            return "float[]";
        } else if (cn.equals("[I")) {
            return "int[]";
        } else if (cn.equals("[J")) {
            return "long[]";
        } else if (cn.equals("[S")) {
            return "short[]";
        } else if (cn.equals("[Z")) {
            return "boolean[]";
        } else if (cn.startsWith("[L")) {
            return cn.substring(2, cn.length() - 1) + "[]";
        } else {
            return cn;
        }
    }

}
