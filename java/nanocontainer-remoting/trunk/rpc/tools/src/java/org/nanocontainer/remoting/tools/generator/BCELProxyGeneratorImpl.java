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

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;
import org.nanocontainer.remoting.common.MethodNameHelper;
import org.nanocontainer.remoting.server.PublicationDescriptionItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * Class BCElProxyGeneratorImpl
 * This class generates NanoContainer Remoting stubs using Jakarta BCEL library.
 * <p/>
 * HOWTO: Use 'javap' to read the bytecodes of the stubs generated
 * by using the original stub-generator(which generates pure java code).
 *
 * @author <a href="mailto:vinayc77@yahoo.com">Vinay Chandran</a>
 * @version $Revision: 1.2 $
 */
public class BCELProxyGeneratorImpl extends AbstractProxyGenerator {

    //bcel
    private static final String STUB_PREFIX = "NanoContainerRemotingGenerated";
    private InstructionFactory m_factory;
    private ConstantPoolGen m_constantsPool;
    private ClassGen m_classGen;
    private ArrayList m_internalFieldRepresentingClasses;

    /**
     * Generate the class.
     *
     * @param classLoader the classloader to use during generation.
     * @see org.nanocontainer.remoting.server.ProxyGenerator#generateClass(ClassLoader)
     */
    public void generateClass(ClassLoader classLoader) {

        //create the Main Stubs:
        generateProxyClass(STUB_PREFIX + getGenName() + "_Main", getInterfacesToExpose());

        //Create the Additional Facades
        if (getAdditionalFacades() != null) {
            for (int i = 0; i < getAdditionalFacades().length; i++) {
                String encodedClassName = MethodNameHelper.encodeClassName(getAdditionalFacades()[i].getFacadeClass());
                generateProxyClass(STUB_PREFIX + getGenName() + "_" + encodedClassName, new PublicationDescriptionItem[]{getAdditionalFacades()[i]});

            }
        }

    }

    /**
     * Method generateProxyClass.
     * Create Proxy Implementation with all interface methods
     * Generating NanoContainerRemotingGeneratedGENNAME_Main class
     *
     * @param mGeneratedClassName the name of the class to generate.
     * @param interfacesToStubify the interfaces to stubify.
     */
    protected void generateProxyClass(String mGeneratedClassName, PublicationDescriptionItem[] interfacesToStubify) {
        //Start creating class
        createNewClassDeclaration(mGeneratedClassName, interfacesToStubify);
        /******** CONSTRUCTOR **************/
        //create constructor that takes ProxyHelper
        createConstructor(mGeneratedClassName);
        /******** FIELDS *************/
        //create fields
        createFields();
        /******** METHODS *************/
        //create fields
        createGetReferenceIDMethod(mGeneratedClassName);
        createHelperMethodForDotClassCalls(mGeneratedClassName);
        createInterfaceMethods(mGeneratedClassName, interfacesToStubify);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getClassGenDir() + "/" + mGeneratedClassName + ".class");
            m_classGen.getJavaClass().dump(fos);
            fos.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //<BCEL>  <!-- Enter the BCEL Arena -->

    /**
     * Method createAndInitializeClass.
     * This method starts creating the class.
     *
     * @param generatedClassName the bean class name
     */
    protected void createNewClassDeclaration(String generatedClassName, PublicationDescriptionItem[] interfacesToStubify) {

        String[] interfaces = new String[interfacesToStubify.length + 1];
        for (int i = 0; i < interfacesToStubify.length; i++) {
            PublicationDescriptionItem publicationDescriptionItem = interfacesToStubify[i];
            interfaces[i] = publicationDescriptionItem.getFacadeClass().getName();
        }
        interfaces[interfacesToStubify.length] = "org.nanocontainer.remoting.client.Proxy";


        m_classGen = new ClassGen(generatedClassName, "java.lang.Object", generatedClassName + ".java", Constants.ACC_PUBLIC | Constants.ACC_SUPER | Constants.ACC_FINAL, interfaces);
        m_constantsPool = m_classGen.getConstantPool();
        m_factory = new InstructionFactory(m_classGen, m_constantsPool);
        m_internalFieldRepresentingClasses = new ArrayList();

    }

    /**
     * Method createConstructor.
     * This method adds a constructor that takes in a ProxyHelper Instance
     *
     * @param generatedClassName the bean class name
     */
    protected void createConstructor(String generatedClassName) {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Constants.ACC_PUBLIC, Type.VOID, new Type[]{new ObjectType("org.nanocontainer.remoting.client.ProxyHelper")}, new String[]{"arg0"}, "<init>", generatedClassName, il, m_constantsPool);
        il.append(m_factory.createLoad(Type.OBJECT, 0));
        il.append(m_factory.createInvoke("java.lang.Object", "<init>", Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
        il.append(m_factory.createLoad(Type.OBJECT, 0));
        il.append(m_factory.createLoad(Type.OBJECT, 1));
        il.append(m_factory.createFieldAccess(generatedClassName, "m_proxyHelper", new ObjectType("org.nanocontainer.remoting.client.ProxyHelper"), Constants.PUTFIELD));
        il.append(m_factory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        m_classGen.addMethod(method.getMethod());
        il.dispose();
    }

    /**
     * Method createFields.
     * =================adding===============
     * private transient org.nanocontainer.remoting.client.ProxyHelper m_proxyHelper;
     * =================adding===============
     * Add
     */
    protected void createFields() {
        FieldGen field;
        field = new FieldGen(Constants.ACC_PRIVATE | Constants.ACC_TRANSIENT, new ObjectType("org.nanocontainer.remoting.client.ProxyHelper"), "m_proxyHelper", m_constantsPool);
        m_classGen.addField(field.getField());
    }

    /**
     * Method createGetReferenceIDMethod.
     * =================adding=====================================
     * public Long nanocontainerRemotingGetReferenceID(Object factoryThatIsAsking) {
     * return m_proxyHelper.getReferenceID(factoryThatIsAsking);
     * }
     * =================adding=====================================
     *
     * @param generatedClassName the generated class name
     */
    protected void createGetReferenceIDMethod(String generatedClassName) {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Constants.ACC_PUBLIC, new ObjectType("java.lang.Long"), new Type[]{Type.OBJECT}, new String[]{"arg0"}, "nanocontainerRemotingGetReferenceID", generatedClassName, il, m_constantsPool);
        il.append(m_factory.createLoad(Type.OBJECT, 0));
        il.append(m_factory.createFieldAccess(generatedClassName, "m_proxyHelper", new ObjectType("org.nanocontainer.remoting.client.ProxyHelper"), Constants.GETFIELD));
        il.append(m_factory.createLoad(Type.OBJECT, 1));
        il.append(m_factory.createInvoke("org.nanocontainer.remoting.client.ProxyHelper", "getReferenceID", new ObjectType("java.lang.Long"), new Type[]{Type.OBJECT}, Constants.INVOKEINTERFACE));
        il.append(m_factory.createReturn(Type.OBJECT));
        method.setMaxStack();
        method.setMaxLocals();
        m_classGen.addMethod(method.getMethod());
        il.dispose();
    }

    /**
     * Method createHelperMethodForDotClassCalls.
     * This class creates a method class$(String) which is used
     * during SomeClass.class instruction
     *
     * @param generatedClassName the bean class name
     */
    protected void createHelperMethodForDotClassCalls(String generatedClassName) {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Constants.ACC_STATIC, new ObjectType("java.lang.Class"), new Type[]{Type.STRING}, new String[]{"arg0"}, "class$", generatedClassName, il, m_constantsPool);
        InstructionHandle ih0 = il.append(m_factory.createLoad(Type.OBJECT, 0));
        il.append(m_factory.createInvoke("java.lang.Class", "forName", new ObjectType("java.lang.Class"), new Type[]{Type.STRING}, Constants.INVOKESTATIC));
        InstructionHandle ih4 = il.append(m_factory.createReturn(Type.OBJECT));
        InstructionHandle ih5 = il.append(m_factory.createStore(Type.OBJECT, 1));
        il.append(m_factory.createNew("java.lang.NoClassDefFoundError"));
        il.append(InstructionConstants.DUP);
        il.append(m_factory.createLoad(Type.OBJECT, 1));
        il.append(m_factory.createInvoke("java.lang.Throwable", "getMessage", Type.STRING, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
        il.append(m_factory.createInvoke("java.lang.NoClassDefFoundError", "<init>", Type.VOID, new Type[]{Type.STRING}, Constants.INVOKESPECIAL));
        il.append(InstructionConstants.ATHROW);
        method.addExceptionHandler(ih0, ih4, ih5, new ObjectType("java.lang.ClassNotFoundException"));
        method.setMaxStack();
        method.setMaxLocals();
        m_classGen.addMethod(method.getMethod());
        il.dispose();
    }

    /**
     * Method createInterfaceMethods.
     * This methods shall iterate through the set of methods
     * of the interface creating equivalent methods for the
     * stubs in the process.
     *
     * @param generatedClassName  the generated class name
     * @param interfacesToStubify the interfaces to make stubs for.
     */
    protected void createInterfaceMethods(String generatedClassName, PublicationDescriptionItem[] interfacesToStubify) {
        for (int x = 0; x < interfacesToStubify.length; x++) {
            Class clazz = interfacesToStubify[x].getFacadeClass();

            if (isVerbose()) {
                System.out.println("ProxyGen: Processing interface: " + clazz.getName());
            }
            Method[] methods = getGeneratableMethods(clazz);
            generateEqualsMethod(generatedClassName);
            for (int i = 0; i < methods.length; i++) {
                createInterfaceMethod(generatedClassName, methods[i], interfacesToStubify[x]);
            }

        }

    }

    /**
     * Method createInterfaceMethod.
     * Add the java.lang.reflect.Method wrapper into the stub
     *
     * @param generatedClassName the bean class name
     * @param mth                the method
     */
    protected void createInterfaceMethod(String generatedClassName, Method mth, PublicationDescriptionItem interfaceToStubify) {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Constants.ACC_PUBLIC, getReturnType(mth), getArguments(mth), getArgumentNames(mth), mth.getName(), generatedClassName, il, m_constantsPool);


        //debug(getArguments(m));

        // **** TO Insert TEST Bytecode Inside the stub ,uncomment the subsequent lines
        //if (m_verbose)
        //    createTestMethod(il, "calling " + mth.getName());

        /*
         *  Declaration of Arrays
         * =======================
         *  Object[] args = new Object[__number__of__arguments];
         *  Class[] argClasses = new Class[__number__of__arguments];
         */

        int variableIndex, numberOfArguments;
        Class[] paramTypes = mth.getParameterTypes();
        numberOfArguments = paramTypes.length;
        variableIndex = getFreeIndexToStart(paramTypes);
        il.append(new PUSH(m_constantsPool, numberOfArguments));
        il.append(m_factory.createNewArray(Type.OBJECT, (short) 1));
        il.append(m_factory.createStore(Type.OBJECT, ++variableIndex));
        il.append(new PUSH(m_constantsPool, numberOfArguments));
        il.append(m_factory.createNewArray(new ObjectType("java.lang.Class"), (short) 1));
        il.append(m_factory.createStore(Type.OBJECT, ++variableIndex));

        /*
         *  Assigning parameter into Object[] and Class[] Array
         * ====================================================
         *   args[0] = v0;
         *   argClasses[0]=v0Class.class
         */
        //Used for adjustment of double/long datatype:
        createInterfaceMethodArgs(numberOfArguments, il, variableIndex, paramTypes, generatedClassName);

        //check if its a rollback  method
        InstructionHandle ih_rollback = null;
        InstructionHandle catchHandler = null;
        BranchInstruction gotoCall = null;
        InstructionHandle ih_tryEnd = null;
        if (interfaceToStubify.isRollback(mth)) {
            ih_rollback = il.append(m_factory.createLoad(Type.OBJECT, 0));
            il.append(m_factory.createFieldAccess(generatedClassName, "m_proxyHelper", new ObjectType("org.nanocontainer.remoting.client.ProxyHelper"), Constants.GETFIELD));
            il.append(m_factory.createInvoke("org.nanocontainer.remoting.client.ProxyHelper", "rollbackAsyncRequests", Type.VOID, Type.NO_ARGS, Constants.INVOKEINTERFACE));
            gotoCall = m_factory.createBranchInstruction(Constants.GOTO, null);
            ih_tryEnd = il.append(gotoCall);

            catchHandler = il.append(m_factory.createStore(Type.OBJECT, ++variableIndex));
            il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
            injectCommonExceptionCatchBlock(il, method, variableIndex);
            --variableIndex;
            //createTestMethod(il,"after rollback");
        }


        /* Within the stub put the
         * Call processObjectRequest on the instance ProxyHelper held within the stub
         * Thus,
         * Injecting the following
         * ================================================
         * try
         * {
         *      Object retVal = m_proxyHelper.processObjectRequest("foo1(int,
         *        float, java.lang.String, java.lang.Integer)",args,argClasses);
         *      return (java.lang.String) retVal;
         * }
         *  catch (Throwable t)
         *  {
         *         if (t instanceof RuntimeException)
         *         {
         *           throw (RuntimeException) t;
         *         }
		 *         else if (t instanceof Error)
         *         {
         *                throw (Error) t;
         *         }
		 *         else
         *         {
         *                t.printStackTrace();
         *                 throw new org.nanocontainer.remoting.client.
         *                      InvocationException("Should never get here:" +t.getMessage());
         *         }
         *  }
         * ================================================
         */
        InstructionHandle ihe1 = il.append(m_factory.createLoad(Type.OBJECT, 0));

        if (interfaceToStubify.isRollback(mth)) {
            gotoCall.setTarget(ihe1);
            method.addExceptionHandler(ih_rollback, ih_tryEnd, catchHandler, new ObjectType("java.lang.Throwable"));
        }

        il.append(m_factory.createFieldAccess(generatedClassName, "m_proxyHelper", new ObjectType("org.nanocontainer.remoting.client.ProxyHelper"), Constants.GETFIELD));
        // **** Check if the return type is facade ***
        Class returnClass = mth.getReturnType();
        if (returnClass.isArray()) {
            returnClass = returnClass.getComponentType();
        }

        if (isAdditionalFacade(mth.getReturnType())) {
            String encodedReturnClassName = "class$" + MethodNameHelper.encodeClassName(returnClass);
            addField(encodedReturnClassName);
            il.append(m_factory.createFieldAccess(generatedClassName, encodedReturnClassName, new ObjectType("java.lang.Class"), Constants.GETSTATIC));
            BranchInstruction ifnullReturnClass = m_factory.createBranchInstruction(Constants.IFNULL, null);
            il.append(ifnullReturnClass);
            il.append(m_factory.createFieldAccess(generatedClassName, encodedReturnClassName, new ObjectType("java.lang.Class"), Constants.GETSTATIC));
            BranchInstruction gotoReturnClass = m_factory.createBranchInstruction(Constants.GOTO, null);
            il.append(gotoReturnClass);

            InstructionHandle ihPushMethodName = il.append(new PUSH(m_constantsPool, returnClass.getName()));
            ifnullReturnClass.setTarget(ihPushMethodName);
            il.append(m_factory.createInvoke(generatedClassName, "class$", new ObjectType("java.lang.Class"), new Type[]{Type.STRING}, Constants.INVOKESTATIC));
            il.append(InstructionConstants.DUP);
            il.append(m_factory.createFieldAccess(generatedClassName, encodedReturnClassName, new ObjectType("java.lang.Class"), Constants.PUTSTATIC));
            InstructionHandle ihPushSignature = il.append(new PUSH(m_constantsPool, MethodNameHelper.getMethodSignature(mth)));
            gotoReturnClass.setTarget(ihPushSignature);
            il.append(m_factory.createLoad(Type.OBJECT, variableIndex - 1));
            il.append(new PUSH(m_constantsPool, MethodNameHelper.encodeClassName(getClassType(returnClass))));
            il.append(m_factory.createInvoke("org.nanocontainer.remoting.client.ProxyHelper", "processObjectRequestGettingFacade", Type.OBJECT, new Type[]{new ObjectType("java.lang.Class"), Type.STRING, new ArrayType(Type.OBJECT, 1), Type.STRING}, Constants.INVOKEINTERFACE));
        } else {
            //method signature = METHODNAME(arguments....)
            il.append(new PUSH(m_constantsPool, MethodNameHelper.getMethodSignature(mth)));
            variableIndex -= 2;
            il.append(m_factory.createLoad(Type.OBJECT, ++variableIndex));
            il.append(m_factory.createLoad(Type.OBJECT, ++variableIndex));
            //Check for async methods
            if (interfaceToStubify.isAsync(mth)) {
                il.append(m_factory.createInvoke("org.nanocontainer.remoting.client.ProxyHelper", "queueAsyncRequest", Type.VOID, new Type[]{Type.STRING, new ArrayType(Type.OBJECT, 1), new ArrayType(new ObjectType("java.lang.Class"), 1)}, Constants.INVOKEINTERFACE));


            } else {
                if (getBCELPrimitiveType(mth.getReturnType().getName()) == Type.VOID) {
                    il.append(m_factory.createInvoke("org.nanocontainer.remoting.client.ProxyHelper", "processVoidRequest", Type.VOID, new Type[]{Type.STRING, new ArrayType(Type.OBJECT, 1), new ArrayType(new ObjectType("java.lang.Class"), 1)}, Constants.INVOKEINTERFACE));
                } else {
                    il.append(m_factory.createInvoke("org.nanocontainer.remoting.client.ProxyHelper", "processObjectRequest", Type.OBJECT, new Type[]{Type.STRING, new ArrayType(Type.OBJECT, 1), new ArrayType(new ObjectType("java.lang.Class"), 1)}, Constants.INVOKEINTERFACE));
                    il.append(m_factory.createStore(Type.OBJECT, ++variableIndex));
                    il.append(m_factory.createLoad(Type.OBJECT, variableIndex));


                }
            }
        }

        //createTestMethod(il,"after remote call");

        InstructionHandle ihe2;
        if (interfaceToStubify.isCommit(mth)) {

            gotoCall = m_factory.createBranchInstruction(Constants.GOTO, null);
            ihe2 = il.append(gotoCall);
            variableIndex++;

        } else {
            if (mth.getReturnType().isPrimitive()) {
                if (getBCELPrimitiveType(mth.getReturnType().getName()) == Type.VOID) {
                    ihe2 = il.append(m_factory.createReturn(Type.VOID));
                } else {
                    il.append(m_factory.createCheckCast(new ObjectType(getJavaWrapperClass(mth.getReturnType().getName()))));
                    il.append(m_factory.createInvoke(getJavaWrapperClass(mth.getReturnType().getName()), mth.getReturnType().getName() + "Value", getBCELPrimitiveType(mth.getReturnType().getName()), Type.NO_ARGS, Constants.INVOKEVIRTUAL));
                    ihe2 = il.append(m_factory.createReturn(getBCELPrimitiveType(mth.getReturnType().getName())));
                }
            } else {
                il.append(m_factory.createCheckCast(new ObjectType(mth.getReturnType().getName())));
                ihe2 = il.append(m_factory.createReturn(Type.OBJECT));
            }
        }

        InstructionHandle ihe3 = il.append(m_factory.createStore(Type.OBJECT, variableIndex));

        //add custom exceptionHandling here
        Class[] exceptionClasses = mth.getExceptionTypes();
        InstructionHandle customHandler = null;
        BranchInstruction ifCustomExceptionBranch = null;
        for (int i = 0; i < exceptionClasses.length; i++) {

            customHandler = il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
            //create the series of custom exception handlers for the classes
            if (ifCustomExceptionBranch != null) {
                ifCustomExceptionBranch.setTarget(customHandler);
            }
            il.append(new INSTANCEOF(m_constantsPool.addClass(new ObjectType(exceptionClasses[i].getName()))));
            ifCustomExceptionBranch = m_factory.createBranchInstruction(Constants.IFEQ, null);
            il.append(ifCustomExceptionBranch);
            il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
            il.append(m_factory.createCheckCast(new ObjectType(exceptionClasses[i].getName())));
            il.append(InstructionConstants.ATHROW);
        }

        InstructionHandle defaultExceptionHandler = il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
        if (customHandler != null) {
            ifCustomExceptionBranch.setTarget(defaultExceptionHandler);
        }

        //add standard exception handling routine which handles any
        //other exception generated during the remote call
        injectCommonExceptionCatchBlock(il, method, variableIndex);

        method.addExceptionHandler(ihe1, ihe2, ihe3, new ObjectType("java.lang.Throwable"));

        //check if its a commit method
        if (interfaceToStubify.isCommit(mth)) {
            InstructionHandle ih_commit = il.append(m_factory.createLoad(Type.OBJECT, 0));
            gotoCall.setTarget(ih_commit);
            il.append(m_factory.createFieldAccess(generatedClassName, "m_proxyHelper", new ObjectType("org.nanocontainer.remoting.client.ProxyHelper"), Constants.GETFIELD));

            il.append(m_factory.createInvoke("org.nanocontainer.remoting.client.ProxyHelper", "commitAsyncRequests", Type.VOID, Type.NO_ARGS, Constants.INVOKEINTERFACE));
            InstructionHandle ih_return = il.append(m_factory.createReturn(Type.VOID));
            catchHandler = il.append(m_factory.createStore(Type.OBJECT, variableIndex));
            il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
            injectCommonExceptionCatchBlock(il, method, variableIndex);
            method.addExceptionHandler(ih_commit, ih_return, catchHandler, new ObjectType("java.lang.Throwable"));
        }


        method.setMaxStack();
        method.setMaxLocals();
        m_classGen.addMethod(method.getMethod());
        il.dispose();
    }

    private void generateEqualsMethod(String generatedClassName) {

        /* public boolean equals(Object o) {
         *   return m_proxyHelper.isEquals(this,o);
         * }
         */

        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Constants.ACC_PUBLIC, Type.BOOLEAN, new Type[]{Type.OBJECT}, new String[]{"arg0"}, "equals", generatedClassName, il, m_constantsPool);

        il.append(m_factory.createLoad(Type.OBJECT, 0));

        il.append(m_factory.createFieldAccess(generatedClassName, "m_proxyHelper", new ObjectType("org.nanocontainer.remoting.client.ProxyHelper"), Constants.GETFIELD));
        il.append(m_factory.createLoad(Type.OBJECT, 0));
        il.append(m_factory.createLoad(Type.OBJECT, 1));

        il.append(m_factory.createInvoke("org.nanocontainer.remoting.client.ProxyHelper", "isEquals", Type.BOOLEAN, new Type[]{Type.OBJECT, Type.OBJECT}, Constants.INVOKEINTERFACE));
        il.append(m_factory.createReturn(Type.INT));
        method.setMaxStack();
        method.setMaxLocals();
        m_classGen.addMethod(method.getMethod());
        il.dispose();
    }

    /**
     * Create interface method's args.
     *
     * @param numberOfArguments  the number of arguments.
     * @param il                 an instruction list
     * @param variableIndex      a varible index.
     * @param paramTypes         parameter types
     * @param generatedClassName the generated class name.
     */
    private void createInterfaceMethodArgs(int numberOfArguments, InstructionList il, int variableIndex, Class[] paramTypes, String generatedClassName) {
        Type previousType = null;
        int loadIndex = 0;
        for (int i = 0; i < numberOfArguments; i++) {
            // assigning the obj ref's
            il.append(m_factory.createLoad(Type.OBJECT, variableIndex - 1));
            il.append(new PUSH(m_constantsPool, i));
            String className = paramTypes[i].getName();
            //adjust for any previous wider datatype (double/long)
            if (previousType != null && (previousType == Type.DOUBLE || previousType == Type.LONG)) {
                ++loadIndex;
            }
            if (paramTypes[i].isPrimitive()) {
                il.append(m_factory.createNew(getJavaWrapperClass(className)));
                il.append(InstructionConstants.DUP);
                il.append(m_factory.createLoad(getBCELPrimitiveType(className), ++loadIndex));
                il.append(m_factory.createInvoke(getJavaWrapperClass(className), "<init>", Type.VOID, new Type[]{getBCELPrimitiveType(className)}, Constants.INVOKESPECIAL));
                il.append(InstructionConstants.AASTORE);
            } else {

                //create the static fields for enabling .class calls
                String encodedFieldName;
                if (paramTypes[i].isArray()) {
                    int index = className.lastIndexOf('[');
                    if (className.charAt(index + 1) == 'L') {
                        encodedFieldName = "array$" + className.substring(1 + index, className.length() - 1).replace('.', '$');
                    } else {
                        encodedFieldName = "array$" + className.substring(1 + index, className.length());
                    }
                } else {
                    encodedFieldName = "class$" + className.replace('.', '$');
                }

                addField(encodedFieldName);
                // ******** TODO assign the obj reference
                il.append(m_factory.createLoad(Type.OBJECT, variableIndex - 1));
                il.append(new PUSH(m_constantsPool, i));
                il.append(m_factory.createLoad(Type.OBJECT, ++loadIndex));
                il.append(InstructionConstants.AASTORE);

                // *********TODO assign the class ref's
                il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
                il.append(new PUSH(m_constantsPool, i));
                il.append(m_factory.createFieldAccess(generatedClassName, encodedFieldName, new ObjectType("java.lang.Class"), Constants.GETSTATIC));
                BranchInstruction ifnull = m_factory.createBranchInstruction(Constants.IFNULL, null);
                il.append(ifnull);
                il.append(m_factory.createFieldAccess(generatedClassName, encodedFieldName, new ObjectType("java.lang.Class"), Constants.GETSTATIC));
                BranchInstruction goHeadToStoreRef = m_factory.createBranchInstruction(Constants.GOTO, null);
                il.append(goHeadToStoreRef);
                InstructionHandle ifnullStartHere = il.append(new PUSH(m_constantsPool, className));

                ifnull.setTarget(ifnullStartHere);

                il.append(m_factory.createInvoke(generatedClassName, "class$", new ObjectType("java.lang.Class"), new Type[]{Type.STRING}, Constants.INVOKESTATIC));
                il.append(InstructionConstants.DUP);
                il.append(m_factory.createFieldAccess(generatedClassName, encodedFieldName, new ObjectType("java.lang.Class"), Constants.PUTSTATIC));
                InstructionHandle storeClassRef = il.append(InstructionConstants.AASTORE);
                goHeadToStoreRef.setTarget(storeClassRef);

            }
            previousType = getBCELPrimitiveType(className);
        }
    }

    /**
     * Inject common exception catch blocks
     */
    public void injectCommonExceptionCatchBlock(InstructionList il, MethodGen method, int variableIndex) {
        il.append(new INSTANCEOF(m_constantsPool.addClass(new ObjectType("java.lang.RuntimeException"))));
        BranchInstruction b1 = m_factory.createBranchInstruction(Constants.IFEQ, null);
        il.append(b1);
        il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
        il.append(m_factory.createCheckCast(new ObjectType("java.lang.RuntimeException")));
        il.append(InstructionConstants.ATHROW);
        InstructionHandle ih1 = il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
        il.append(new INSTANCEOF(m_constantsPool.addClass(new ObjectType("java.lang.Error"))));
        BranchInstruction b2 = m_factory.createBranchInstruction(Constants.IFEQ, null);
        il.append(b2);
        il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
        il.append(m_factory.createCheckCast(new ObjectType("java.lang.Error")));
        il.append(InstructionConstants.ATHROW);
        InstructionHandle ih2 = il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
        il.append(m_factory.createInvoke("java.lang.Throwable", "printStackTrace", Type.VOID, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
        il.append(m_factory.createNew("org.nanocontainer.remoting.client.InvocationException"));
        il.append(InstructionConstants.DUP);
        il.append(m_factory.createNew("java.lang.StringBuffer"));
        il.append(InstructionConstants.DUP);
        il.append(new PUSH(m_constantsPool, "Should never get here: "));
        il.append(m_factory.createInvoke("java.lang.StringBuffer", "<init>", Type.VOID, new Type[]{Type.STRING}, Constants.INVOKESPECIAL));
        il.append(m_factory.createLoad(Type.OBJECT, variableIndex));
        il.append(m_factory.createInvoke("java.lang.Throwable", "getMessage", Type.STRING, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
        il.append(m_factory.createInvoke("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.STRING}, Constants.INVOKEVIRTUAL));
        il.append(m_factory.createInvoke("java.lang.StringBuffer", "toString", Type.STRING, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
        il.append(m_factory.createInvoke("org.nanocontainer.remoting.client.InvocationException", "<init>", Type.VOID, new Type[]{Type.STRING}, Constants.INVOKESPECIAL));
        il.append(InstructionConstants.ATHROW);

        b1.setTarget(ih1);
        b2.setTarget(ih2);

    }

    /**
     * Method getFreeIndexToStart.
     * Returns the index to start allocating the subsequent stack variables
     *
     * @param classes the classes
     * @return int the index
     */
    protected int getFreeIndexToStart(Class[] classes) {
        int index = 0;
        for (int i = 0; i < classes.length; i++) {
            if (getBCELType(classes[i]) == Type.DOUBLE || getBCELType(classes[i]) == Type.LONG) {
                index += 2;
            }
            index += 1;
        }

        return index;
    }

    /**
     * Method getArguments.
     * Convert the arguments of the method
     * into equivalent BCEL datatypes
     *
     * @param method the method for which arguments are needed.
     * @return Type[] an array of types
     */
    protected Type[] getArguments(Method method) {
        Class[] classes = method.getParameterTypes();
        if (classes.length == 0) {
            return Type.NO_ARGS;
        }

        Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i++) {
            types[i] = getBCELType(classes[i]);
        }
        return types;
    }

    /**
     * Method getReturnType.
     * Convert the returnType of the method into BCEL datatype
     *
     * @param method the method
     * @return Type the type
     */
    protected Type getReturnType(Method method) {
        return getBCELType(method.getReturnType());
    }

    /**
     * Method getArgumentNames.
     * The arguments are arg0,arg1,.....
     *
     * @param method the method
     * @return String[]
     */
    protected String[] getArgumentNames(Method method) {
        Class[] classes = method.getParameterTypes();
        String[] args = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            args[i] = "arg" + i;
        }
        return args;
    }

    /**
     * Method getBCELType.
     * Maps the java datatype and the BCEL datatype
     *
     * @param clazz the class
     * @return Type the type
     */
    protected Type getBCELType(Class clazz) {

        if (clazz.isPrimitive()) {
            return getBCELPrimitiveType(clazz.getName());
        } else if (!clazz.isArray()) {
            return new ObjectType(clazz.getName());
        } else {
            String className = clazz.getName();
            int index = className.lastIndexOf('[');
            int arrayDepth = className.indexOf('[') - className.lastIndexOf('[') + 1;
            if (className.charAt(index + 1) == 'L') {
                return new ArrayType(new ObjectType(clazz.getComponentType().getName()), arrayDepth);
            }

            return new ArrayType(getBCELPrimitiveType(className.substring(arrayDepth)), arrayDepth);
        }

    }

    /**
     * Method getBCELPrimitiveType.
     * Returns the BCEL Type given the Class Name
     *
     * @param javaDataType the java data type
     * @return Type the BCEL type
     */
    protected Type getBCELPrimitiveType(String javaDataType) {
        switch (javaDataType.charAt(0)) {

            case 'b':
                if (javaDataType.toString().charAt(1) == 'o') {
                    return Type.BOOLEAN;
                } else {
                    return Type.BYTE;
                }
            case 'c':
            case 'C':
                return Type.CHAR;
            case 's':
            case 'S':
                return Type.SHORT;
            case 'i':
            case 'I':
                return Type.INT;
            case 'l':
            case 'L':
                return Type.LONG;
            case 'f':
            case 'F':
                return Type.FLOAT;
            case 'd':
            case 'D':
                return Type.DOUBLE;
                //boolean array appears in this format
            case 'Z':
                return Type.BOOLEAN;
            case 'B':
                return Type.BYTE;
            case 'v':
            case 'V':
                return Type.VOID;
        }
        return null;
    }

    /**
     * Method getJavaWrapperClass.
     * Returns the String representing the Wrapper class given the
     * primitive datatype
     *
     * @param javaDataType the java data type
     * @return String the JavaSource type.
     */
    protected String getJavaWrapperClass(String javaDataType) {
        switch (javaDataType.charAt(0)) {

            case 'b':
                if (javaDataType.charAt(1) == 'o') {
                    return "java.lang.Boolean";
                } else {
                    return "java.lang.Byte";
                }
            case 'c':
            case 'C':
                return "java.lang.Character";
            case 's':
            case 'S':
                return "java.lang.Short";
            case 'i':
            case 'I':
                return "java.lang.Integer";
            case 'l':
            case 'L':
                return "java.lang.Long";
            case 'f':
            case 'F':
                return "java.lang.Float";
            case 'd':
            case 'D':
                return "java.lang.Double";
            case 'B':
                return "java.lang.Byte";
            case 'Z':
                return "java.lang.Boolean";
            case 'v':
            case 'V':
                return "java.lang.Void";
            case '[':
                return getJavaWrapperClass(javaDataType.substring(1));

        }
        return null; //never occurs;
    }

    /**
     * @param encodedFieldName the encoded field name
     */
    protected void addField(String encodedFieldName) {
        if (!m_internalFieldRepresentingClasses.contains(encodedFieldName)) {
            //System.out.println("method."+method.getName()+".addingfield["
            //  + _encodedFieldName + "]");
            FieldGen field = new FieldGen(Constants.ACC_STATIC, new ObjectType("java.lang.Class"), encodedFieldName, m_constantsPool);
            m_classGen.addField(field.getField());
            m_internalFieldRepresentingClasses.add(encodedFieldName);
        }

    }

    /**
     * @param il  the instruction list
     * @param msg the message
     */
    protected void createTestMethod(InstructionList il, String msg) {
        il.append(m_factory.createFieldAccess("java.lang.System", "out", new ObjectType("java.io.PrintStream"), Constants.GETSTATIC));
        il.append(new PUSH(m_constantsPool, msg));
        il.append(m_factory.createInvoke("java.io.PrintStream", "println", Type.VOID, new Type[]{Type.STRING}, Constants.INVOKEVIRTUAL));
    }

    /**
     * A debugging method
     *
     * @param prefix   the prefix to print
     * @param objArray the object array to print.
     */
    protected void debug(String prefix, Object[] objArray) {
        System.out.print(prefix);
        for (int i = 0; i < objArray.length; i++) {
            System.out.print(objArray[i] + ":");
        }
        System.out.println();
    }

    /**
     * Get a class for a generated class name.
     *
     * @param generatedClassName the generated class name
     * @return the class
     */
    public Class getGeneratedClass(String generatedClassName) {
        return getProxyClass(generatedClassName);
    }

    /**
     * Method getProxyClass.
     * This get the Class definition from the bytes
     *
     * @param className the clas name
     * @return Class the class
     */
    protected Class getProxyClass(String className) {

        /*
        FromJavaClassClassLoader fromJavaClassClassLoader =
            new FromJavaClassClassLoader();
        Class clazz =
            fromJavaClassClassLoader.getClassFromJavaClass(m_classGen.getJavaClass());
        */
        Class clazz = null;

        try {
            URLClassLoader urlCL = new URLClassLoader(new URL[]{new URL("file:/" + new File(getClassGenDir()).getCanonicalPath() + "/")}, //
                    this.getClass().getClassLoader());
            clazz = urlCL.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // this is OK, as null will be passed back.
        }

        return clazz;
    }

    //++++++++++++++++++testing
    //</BCEL>

}