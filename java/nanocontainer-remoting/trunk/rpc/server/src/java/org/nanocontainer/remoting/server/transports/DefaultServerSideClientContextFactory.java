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
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;

/**
 * @author Paul Hammant and Rune Johanessen (pairing for part)
 * @version $Revision: 1.2 $
 */

public class DefaultServerSideClientContextFactory implements ServerSideClientContextFactory {

    private static ThreadLocal c_contexts = new ThreadLocal();

    public ClientContext get() {
        return (ClientContext) c_contexts.get();
    }

    public void set(Long session, ClientContext clientContext) {
        c_contexts.set(new DefaultServerSideClientContext(session, clientContext));
    }

    public boolean isSet() {
        return c_contexts.get() != null;
    }

}
