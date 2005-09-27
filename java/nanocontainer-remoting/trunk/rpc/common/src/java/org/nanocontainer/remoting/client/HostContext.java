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
package org.nanocontainer.remoting.client;



/**
 * HostContext, as the name suggests, describes the
 * context of the calls made to the server which could be
 * Over Piped Streams or Over Custom Transport or Direct calls etc ..
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public interface HostContext {

    /**
     * Return the Invocation Handler that can talk over
     * the transport this particular context addresses.
     *
     * @return InvocationHandler
     */
    ClientInvocationHandler getInvocationHandler();
}
