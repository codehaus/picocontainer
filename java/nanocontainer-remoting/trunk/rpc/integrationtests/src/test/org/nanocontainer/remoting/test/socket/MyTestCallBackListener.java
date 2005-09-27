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
package org.nanocontainer.remoting.test.socket;

import org.nanocontainer.remoting.test.TestCallBackListener;
import org.nanocontainer.remoting.test.TestInterface;

import java.util.HashMap;

/**
 * Test Custom Stream over sockets.
 *
 * @author Paul Hammant
 */
public class MyTestCallBackListener implements TestCallBackListener {
    private final HashMap results;

    public MyTestCallBackListener(HashMap results) {
        this.results = results;
    }

    public void serverCallingClient(String message) {
        results.put("1", message);
    }

    public void serverCallingClient2(TestInterface testInterface) {
        results.put("2", testInterface.toString());
    }
}

