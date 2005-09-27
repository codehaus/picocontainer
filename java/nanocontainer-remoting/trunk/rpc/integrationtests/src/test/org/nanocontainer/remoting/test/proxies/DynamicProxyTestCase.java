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

import org.nanocontainer.remoting.test.AbstractHelloCallBackTestCase;
import org.nanocontainer.remoting.test.TestInterface;
import org.nanocontainer.remoting.test.TestInterfaceImpl;

/**
 * Test Dynamic Proxy (reflection) for comparison sake
 *
 * @author Paul Hammant
 */
public class DynamicProxyTestCase extends AbstractHelloCallBackTestCase {

    public DynamicProxyTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        testServer = new TestInterfaceImpl();
        testClient = (TestInterface) DynamicProxy.newInstance(testServer, new Class[]{TestInterface.class});

    }

}
