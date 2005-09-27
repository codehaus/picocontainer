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
package org.nanocontainer.remoting.test.tools.generator;

import junit.framework.TestCase;
import org.nanocontainer.remoting.client.factories.ClientSideClassFactory;
import org.nanocontainer.remoting.client.factories.DefaultProxyHelper;
import org.nanocontainer.remoting.client.monitors.DumbClientMonitor;
import org.nanocontainer.remoting.client.pingers.DefaultConnectionPinger;
import org.nanocontainer.remoting.client.transports.direct.DirectHostContext;
import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.server.ProxyGenerator;
import org.nanocontainer.remoting.server.PublicationDescriptionItem;
import org.nanocontainer.remoting.tools.generator.BCELProxyGeneratorImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Class BCELProxyGeneratorTest
 * Unit testing of BCELProxyGeneratorImpl
 *
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandrasekharan</a>
 * @version 1.0
 */
public class BCELProxyGeneratorTestCase extends TestCase {
    private ProxyGenerator m_proxyGenerator;
    private Class m_generatedProxyClass;
    private Object m_generatedProxyObject;
    private ClientSideClassFactory m_factory;
    /**
     * ********************* TestInterface ******************
     */
    public static final Class m_testInterfaceClass = TestRemoteInterface.class;

    public BCELProxyGeneratorTestCase(String testName) {
        super(testName);
    }

    private Class createNewClass() {
        if (m_generatedProxyClass != null) {
            return m_generatedProxyClass;
        }
        m_proxyGenerator.setGenName("Something");
        m_proxyGenerator.setInterfacesToExpose(new PublicationDescriptionItem[]{new PublicationDescriptionItem(m_testInterfaceClass)});
        m_proxyGenerator.setClassGenDir(".");
        m_proxyGenerator.verbose(true);
        m_proxyGenerator.generateClass(null);


        m_generatedProxyClass = ((BCELProxyGeneratorImpl) m_proxyGenerator).getGeneratedClass("NanoContainerRemotingGeneratedSomething_Main");
        return m_generatedProxyClass;
    }


    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        m_proxyGenerator = (ProxyGenerator) Class.forName("org.nanocontainer.remoting.tools.generator.BCELProxyGeneratorImpl").newInstance();
        //create the Proxy Class using the BCEL Generator
        createNewClass();
        m_proxyGenerator.verbose(true);
    }


    /**
     * Method testGeneratedClassName.
     * Checks whether 'Class' is created properly
     */
    public void testGeneratedClassNameOfProxy() {
        assertNotNull(m_proxyGenerator);
        assertNotNull(m_generatedProxyClass);
        assertEquals(m_generatedProxyClass.getName().equals("NanoContainerRemotingGeneratedSomething_Main"), true);
    }

    /**
     * Method testConstructorOfProxy.
     * Test if the instance is created properly using the lone
     * Constructor embedded within the Proxy implementation
     *
     * @throws Exception
     */
    public void testConstructorOfProxy() throws Exception {
        if (m_generatedProxyClass == null) {
            testGeneratedClassNameOfProxy();
        }
        TestInvocationHandler invocationHandler = new TestInvocationHandler(new DefaultThreadPool(), new DumbClientMonitor(), new DefaultConnectionPinger());
        //create the m_factory;
        m_factory = new ClientSideClassFactory(new DirectHostContext.WithSimpleDefaults(invocationHandler), false);
        DefaultProxyHelper defaultProxyHelper = new DefaultProxyHelper(m_factory, invocationHandler, "PublishedName", "ObjectName", new Long(1010), new Long(3030));

        Constructor[] _constructors = m_generatedProxyClass.getConstructors();
        //there shld be only 1 constructor for the generated proxy
        // one that takes BaseServedObject as the argument
        assertEquals(_constructors.length, 1);

        m_generatedProxyObject = _constructors[0].newInstance(new Object[]{defaultProxyHelper});
        assertNotNull(m_generatedProxyObject);

    }


    /**
     * Method testGetReferenceIDMethodOfProxy.
     * Testing
     * =================================
     * public Long nanocontainerRemotingGetReferenceID(Object factoryThatIsAsking) {
     * return mBaseServedObject.getReferenceID(factoryThatIsAsking);
     * }
     * =================================
     *
     * @throws Exception
     */
    public void testGetReferenceIDMethodOfProxy() throws Exception {
        if (m_generatedProxyObject == null) {
            testConstructorOfProxy();
        }

        Method _getReferenceIDMethod = m_generatedProxyClass.getMethod("nanocontainerRemotingGetReferenceID", new Class[]{Object.class});
        assertNotNull(_getReferenceIDMethod);
        Object _ret = _getReferenceIDMethod.invoke(m_generatedProxyObject, new Object[]{m_factory});
        assertEquals(new Long(1010), _ret);
    }

    /**
     * Method testGeneratedMethodsPassOne.
     * Testing
     * This test involves the crux of the stub-generation
     * routine.
     * 1. Pass an test interface for stub-generation
     * 2. Test the created stub
     *
     * @throws Exception
     */
    public void testGeneratedMethodsPassOne() throws Exception {
        if (m_generatedProxyObject == null) {
            testConstructorOfProxy();
        }


        Method[] methods = m_generatedProxyClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().indexOf("test") == -1) {
                continue;
            }
            //System.out.println("Testing method["+methods[i].getName()+"]");
            Object[] _arguments = new Object[methods[i].getParameterTypes().length];
            for (int j = 0; j < _arguments.length; j++) {

                _arguments[j] = m_testInterfaceClass.getField(methods[i].getName() + "_arg" + j).get(null);
                //System.out.println("argType["+methods[i].getParameterTypes()[j]+"]arg["+j+"]"+_arguments[j]);
            }
            if (methods[i].getParameterTypes().length == 0) {
                _arguments = null;
            }
            Object _ret = methods[i].invoke(m_generatedProxyObject, _arguments);

            if (methods[i].getReturnType() != Void.TYPE) {
                assertEquals(m_testInterfaceClass.getField(methods[i].getName() + "_retValue").get(null), _ret);
            }
        }
    }

}
