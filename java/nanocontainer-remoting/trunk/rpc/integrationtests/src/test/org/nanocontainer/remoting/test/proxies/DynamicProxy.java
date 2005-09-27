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
package org.nanocontainer.remoting.test.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Directly copied from Avalon-Excalibur for comparison testing in NanoContainer Remoting
 *
 * @author Peter Donald
 * @author Paul Hammant
 * @version CVS $Revision: 1.2 $ $Date: 2004/02/08 06:06:45 $
 * @since 4.0b5
 */
public final class DynamicProxy implements InvocationHandler {

    private transient Object m_object;

    /**
     * Private constructor that blockpublishing instantiation outside this class.
     *
     * @param object the underlying object
     */
    private DynamicProxy(final Object object) {
        m_object = object;
    }

    /**
     * Create a proxy object that has all of it's underlying
     * interfaces implemented by proxy.
     *
     * @param object the underling object to proxy
     * @return the proxied object
     */
    public static Object newInstance(final Object object) {
        return newInstance(object, object.getClass().getInterfaces());
    }

    /**
     * Create a proxy object that has specified interfaces implemented by proxy.
     *
     * @param object     the underling object to proxy
     * @param interfaces
     * @return the proxied object
     */
    public static Object newInstance(final Object object, final Class[] interfaces) {

        final ClassLoader classLoader = object.getClass().getClassLoader();
        final DynamicProxy proxy = new DynamicProxy(object);

        return Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }

    /**
     * Invoke the specified method on underlying object.
     * This is called by proxy object.
     *
     * @param proxy  the proxy object
     * @param method the method invoked on proxy object
     * @param args   the arguments supplied to method
     * @return the return value of method
     * @throws Throwable if an error occurs
     */
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        try {
            return method.invoke(m_object, args);
        } catch (final InvocationTargetException ite) {
            throw ite.getTargetException();
        }
    }
}
