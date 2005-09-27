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
package org.nanocontainer.remoting.server.transports;

import org.nanocontainer.remoting.ClientContext;

public class DefaultServerSideClientContext implements ClientContext {
    private Long session;
    private ClientContext clientSideClientContext;
    private int hashCode;

    public DefaultServerSideClientContext(Long session, ClientContext clientSideClientContext) {
        this.session = session;
        this.clientSideClientContext = clientSideClientContext;
        if (session != null && clientSideClientContext != null) {
            hashCode = session.hashCode() + clientSideClientContext.hashCode();
        }
    }

    public int hashCode() {
        return hashCode;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DefaultServerSideClientContext) {
            DefaultServerSideClientContext other = (DefaultServerSideClientContext) obj;
            if (!session.equals(other.session)) {
                return false;
            }
            if (!clientSideClientContext.equals(other.clientSideClientContext)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public String toString() {
        return "DefaultServerSideClientContext[" + session + "," + clientSideClientContext.toString() + "]";
    }

}
