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

/**
 * DynamicClassFactory creates stubs(@see DynamicStub) for the given
 * publishedName at the time of invocation.Using this m_factory removes the need
 * for any compiled stubs corresponding to the remote interface
 * to be present on the client side to invoke any remote method on the server.
 *
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 */
public class DynamicClassFactory extends AbstractFactory {

    public DynamicClassFactory(HostContext hostContext, boolean allowOptimize) throws ConnectionException {
        super(hostContext, allowOptimize);
    }

    //-------AbstractFactory Overrides------//
    /**
     * @see org.nanocontainer.remoting.client.factories.AbstractFactory#getFacadeClass(String, String)
     */
    protected Class getFacadeClass(String publishedServiceName, String objectName) throws ConnectionException, ClassNotFoundException {
        //NOT USED
        return null;
    }

    /**
     * @see org.nanocontainer.remoting.client.factories.AbstractFactory#getInstance(String, String, DefaultProxyHelper)
     */
    protected Object getInstance(String publishedServiceName, String objectName, DefaultProxyHelper proxyHelper) throws ConnectionException {
        return new DynamicStub(publishedServiceName, objectName, proxyHelper);
    }


    /**
     * @see org.nanocontainer.remoting.client.InterfaceLookup#close()
     */

    public void close() {
        m_hostContext.getInvocationHandler().close();
    }
}
