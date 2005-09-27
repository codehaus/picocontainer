/* ====================================================================
 * Copyright 2005 NanoContainer Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.nanocontainer.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nanocontainer.remoting.client.factories;

import org.nanocontainer.remoting.client.HostContext;
import org.nanocontainer.remoting.common.ConnectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Class ClientSideClassFactory
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class ClientSideClassFactory extends AbstractFactory {

    public ClientSideClassFactory(HostContext hostContext, boolean allowOptimize) throws ConnectionException {
        super(hostContext, allowOptimize);
    }

    protected Class getFacadeClass(String publishedServiceName, String objectName) throws ConnectionException, ClassNotFoundException {

        String className = "NanoContainerRemotingGenerated" + publishedServiceName + "_" + objectName;

        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            return this.getClass().getClassLoader().loadClass(className);
        }
    }

    /**
     * Method getInstance
     *
     * @param publishedServiceName
     * @param objectName
     * @return
     * @throws ConnectionException
     */
    protected Object getInstance(String publishedServiceName, String objectName, DefaultProxyHelper proxyHelper) throws ConnectionException {

        try {
            Class clazz = getFacadeClass(publishedServiceName, objectName);
            Constructor[] constructors = clazz.getConstructors();
            Object retVal = constructors[0].newInstance(new Object[]{proxyHelper});

            return retVal;
        } catch (InvocationTargetException ite) {
            throw new ConnectionException("Generated class not instantiated : " + ite.getTargetException().getMessage());
        } catch (ClassNotFoundException cnfe) {
            throw new ConnectionException("Generated class not found during lookup : " + cnfe.getMessage());
        } catch (InstantiationException ie) {
            throw new ConnectionException("Generated class not instantiable during lookup : " + ie.getMessage());
        } catch (IllegalAccessException iae) {
            throw new ConnectionException("Illegal access to generated class during lookup : " + iae.getMessage());
        }
    }

    /**
     * Method close
     */
    public void close() {
        m_hostContext.getInvocationHandler().close();
    }
}
