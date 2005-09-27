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

import org.nanocontainer.remoting.client.InterfaceLookup;
import org.nanocontainer.remoting.client.InterfaceLookupFactory;
import org.nanocontainer.remoting.client.factories.DefaultInterfaceLookupFactory;
import org.nanocontainer.remoting.common.ConnectionException;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * Class DefaultContext
 *
 * @author Vinay Chandrasekharan <a href="mailto:vinayc77@yahoo.com">vinayc77@yahoo.com</a>
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class DefaultContext implements Context {

    InterfaceLookupFactory m_interfaceLookupFactory = new DefaultInterfaceLookupFactory();
    InterfaceLookup m_interfaceLookup;

    /**
     * Constructor DefaultContext
     *
     * @param host
     * @param port
     * @param transportStream
     * @param env
     * @throws NamingException
     */
    DefaultContext(String host, String port, String transportStream, Hashtable env) throws NamingException {

        String proxyDetails = null;
        ClassLoader interfacesClassLoader = null;
        boolean optimize;

        proxyDetails = (String) env.get("proxy.type");

        {
            if (proxyDetails == null) {
                proxyDetails = "S";
            } else if (proxyDetails.equals("ClientSideClasses")) {
                proxyDetails = "C";
            } else if (proxyDetails.equals("ServerSideClasses")) {
                proxyDetails = "S";
            } else {
                throw new NamingException("proxy.type should be 'ClientSideClasses' or 'ServerSideClasses', you specified " + proxyDetails);
            }
        }

        String optimizeStr = (String) env.get("optimize");

        {
            if (optimizeStr == null) {
                optimize = true;
            } else if (optimizeStr.equals("true")) {
                optimize = true;
            } else if (optimizeStr.equals("false")) {
                optimize = false;
            } else {
                throw new NamingException("optimize should be 'true' or 'false', you specified " + optimizeStr);
            }
        }


        {
            interfacesClassLoader = (ClassLoader) env.get("interfaces.classloader");

            if (interfacesClassLoader == null) {
                interfacesClassLoader = this.getClass().getClassLoader();
            }

            try {

                m_interfaceLookup = m_interfaceLookupFactory.getInterfaceLookup(transportStream + ":" + host + ":" + port + ":" + proxyDetails, interfacesClassLoader, optimize);
                if (m_interfaceLookup == null) {
                    throw new IllegalArgumentException("InterfaceLookupFactory not found for '" + transportStream + " " + host + " " + port);
                }
            } catch (ConnectionException ace) {
                ace.printStackTrace();

                throw new NamingException(ace.getMessage());
            }
        }
    }

    /**
     * Method lookup
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public Object lookup(Name name) throws NamingException {
        return lookup(name.toString()); // this right?
    }

    /**
     * Method lookup
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public Object lookup(String name) throws NamingException {
        try {
            return m_interfaceLookup.lookup(name);
        } catch (ConnectionException ace) {
            throw new NamingException(ace.getMessage());
        }
    }

    /**
     * Method bind
     *
     * @param name
     * @param obj
     * @throws NamingException
     */
    public void bind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method bind
     *
     * @param name
     * @param obj
     * @throws NamingException
     */
    public void bind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method rebind
     *
     * @param name
     * @param obj
     * @throws NamingException
     */
    public void rebind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method rebind
     *
     * @param name
     * @param obj
     * @throws NamingException
     */
    public void rebind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method unbind
     *
     * @param name
     * @throws NamingException
     */
    public void unbind(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method unbind
     *
     * @param name
     * @throws NamingException
     */
    public void unbind(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method rename
     *
     * @param oldName
     * @param newName
     * @throws NamingException
     */
    public void rename(Name oldName, Name newName) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method rename
     *
     * @param oldName
     * @param newName
     * @throws NamingException
     */
    public void rename(String oldName, String newName) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method list
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public NamingEnumeration list(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method list
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public NamingEnumeration list(String name) throws NamingException {

        final String[] names = m_interfaceLookup.list();

        return new NamingEnumeration() {

            int size = names.length;
            int index = 0;

            public void close() {
            }

            public boolean hasMore() {
                return index < names.length;
            }

            public Object next() {
                return names[index++];
            }

            public boolean hasMoreElements() {
                return hasMore();
            }

            public Object nextElement() {
                return next();
            }
        };
    }

    /**
     * Method listBindings
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public NamingEnumeration listBindings(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method listBindings
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public NamingEnumeration listBindings(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method destroySubcontext
     *
     * @param name
     * @throws NamingException
     */
    public void destroySubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method destroySubcontext
     *
     * @param name
     * @throws NamingException
     */
    public void destroySubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method createSubcontext
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public Context createSubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method createSubcontext
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public Context createSubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method lookupLink
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public Object lookupLink(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method lookupLink
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public Object lookupLink(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method getNameParser
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public NameParser getNameParser(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method getNameParser
     *
     * @param name
     * @return
     * @throws NamingException
     */
    public NameParser getNameParser(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method composeName
     *
     * @param name
     * @param prefix
     * @return
     * @throws NamingException
     */
    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method composeName
     *
     * @param name
     * @param prefix
     * @return
     * @throws NamingException
     */
    public String composeName(String name, String prefix) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method addToEnvironment
     *
     * @param propName
     * @param propVal
     * @return
     * @throws NamingException
     */
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method removeFromEnvironment
     *
     * @param propName
     * @return
     * @throws NamingException
     */
    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method getEnvironment
     *
     * @return
     * @throws NamingException
     */
    public Hashtable getEnvironment() throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method close
     *
     * @throws NamingException
     */
    public void close() throws NamingException {
        m_interfaceLookup.close();
    }

    /**
     * Method getNameInNamespace
     *
     * @return
     * @throws NamingException
     */
    public String getNameInNamespace() throws NamingException {
        throw new UnsupportedOperationException();
    }
}
