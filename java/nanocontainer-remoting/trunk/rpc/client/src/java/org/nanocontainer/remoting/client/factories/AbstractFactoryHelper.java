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

import org.nanocontainer.remoting.client.Factory;
import org.nanocontainer.remoting.client.HostContext;
import org.nanocontainer.remoting.client.InterfaceLookup;
import org.nanocontainer.remoting.client.InterfaceLookupFactory;
import org.nanocontainer.remoting.common.ConnectionException;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class AbstractFactoryHelper
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public abstract class AbstractFactoryHelper implements InterfaceLookupFactory {

    protected String[] processFactoryString(String factoryString) {

        Vector terms = new Vector();
        StringTokenizer st = new StringTokenizer(factoryString, ":");

        while (st.hasMoreTokens()) {
            terms.add(st.nextToken());
        }

        String[] retval = new String[terms.size()];

        terms.copyInto(retval);

        return retval;
    }

    protected Factory createFactory(String type, HostContext hostContext, boolean optimize) throws ConnectionException {

        if (type.equalsIgnoreCase("s")) {
            return new ServerSideClassFactory(hostContext, optimize);
        } else if (type.equalsIgnoreCase("c")) {
            return new ClientSideClassFactory(hostContext, optimize);
        } else {
            return null;
        }
    }

    /**
     * Method getInterfaceLookup
     *
     * @param factoryString
     * @param optimize
     * @return
     * @throws ConnectionException
     */
    public final InterfaceLookup getInterfaceLookup(String factoryString, boolean optimize) throws ConnectionException {
        return getInterfaceLookup(factoryString, this.getClass().getClassLoader(), optimize);
    }


}
