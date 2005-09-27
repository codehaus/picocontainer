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
package org.nanocontainer.remoting.client.naming;

import org.nanocontainer.remoting.client.factories.DefaultInterfaceLookupFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * Class DefaultInitialContextFactory
 *
 * @author Vinay Chandrasekharan <a href="mailto:vinayc77@yahoo.com">vinayc77@yahoo.com</a>
 * @version $Revision: 1.2 $
 */
public class DefaultInitialContextFactory implements InitialContextFactory {

    /**
     * Method getInitialContext
     *
     * @param env
     * @return
     * @throws NamingException
     * @see InitialContextFactory#getInitialContext(Hashtable)
     */
    public Context getInitialContext(Hashtable env) throws NamingException {

        String transportStream = "";
        String s_url = (String) env.get(Context.PROVIDER_URL);
        int index = -1;

        index = (s_url.lastIndexOf("\\") > s_url.lastIndexOf("/")) ? s_url.lastIndexOf("\\") : s_url.lastIndexOf("/");
        transportStream = s_url.substring(index + 1);

        for (int i = 0; i < DefaultInterfaceLookupFactory.SUPPORTEDSTREAMS.length; i++) {
            if (transportStream.equalsIgnoreCase(DefaultInterfaceLookupFactory.SUPPORTEDSTREAMS[i])) {
                String host = s_url.substring(s_url.indexOf(":") + 3, s_url.lastIndexOf(":"));
                String port = s_url.substring(s_url.lastIndexOf(":") + 1, index);

                return new DefaultContext(host, port, transportStream, env);
            }
        }

        throw new NamingException("TransportStream[" + transportStream + "] not supported");
    }
}
