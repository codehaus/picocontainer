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
package org.nanocontainer.remoting.server.monitors;

import org.nanocontainer.remoting.common.BadConnectionException;
import org.nanocontainer.remoting.server.ServerMonitor;

import java.io.IOException;

/**
 * Class NullServerMonitor
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class NullServerMonitor implements ServerMonitor {

    public void closeError(Class clazz, String s, IOException e) {
    }

    public void badConnection(Class clazz, String s, BadConnectionException bce) {
    }

    public void classNotFound(Class clazz, ClassNotFoundException e) {
    }

    public void unexpectedException(Class clazz, String s, Exception e) {
    }

    public void stopServerError(Class clazz, String s, Exception e) {
    }

}
