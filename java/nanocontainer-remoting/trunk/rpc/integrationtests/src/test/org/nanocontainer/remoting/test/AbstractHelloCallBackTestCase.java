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
package org.nanocontainer.remoting.test;

public abstract class AbstractHelloCallBackTestCase extends AbstractHelloTestCase implements TestCallBackListener {

    protected String cbMessage;
    protected TestInterface cbServer;

    public AbstractHelloCallBackTestCase(String name) {
        super(name);
    }

    public void notestCallBack() throws Exception {
        testClient.addCallBackListener(this);
        testClient.ping();

        assertNotNull(cbMessage);
        assertNotNull(cbServer);
        assertEquals("Hello", cbMessage);
        assertEquals(this, cbServer);

    }

    public void serverCallingClient(String message) {
        cbMessage = message;
    }

    public void serverCallingClient2(TestInterface testInterface) {
        cbServer = testInterface;
    }
}
