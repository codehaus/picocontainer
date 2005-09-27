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

import org.nanocontainer.remoting.client.HostContext;
import org.nanocontainer.remoting.client.NotPublishedException;
import org.nanocontainer.remoting.commands.ClassReply;
import org.nanocontainer.remoting.commands.ClassRequest;
import org.nanocontainer.remoting.commands.ClassRetrievalFailedReply;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.ReplyConstants;
import org.nanocontainer.remoting.commands.RequestFailedReply;
import org.nanocontainer.remoting.common.ConnectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Class ServerSideClassFactory
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class ServerSideClassFactory extends AbstractFactory {

    private HashMap m_publishedServiceClassLoaders = new HashMap();

    public ServerSideClassFactory(HostContext hostContext, boolean allowOptimize) throws ConnectionException {
        super(hostContext, allowOptimize);
    }

    protected Class getFacadeClass(String publishedServiceName, String objectName) throws ConnectionException, ClassNotFoundException {

        TransportedClassLoader tcl = null;
        String proxyClassName = "NanoContainerRemotingGenerated" + publishedServiceName + "_" + objectName;

        if (m_publishedServiceClassLoaders.containsKey(proxyClassName)) {
            tcl = (TransportedClassLoader) m_publishedServiceClassLoaders.get(proxyClassName);
        } else {
            ClassReply cr = null;

            try {
                Reply ar = m_hostContext.getInvocationHandler().handleInvocation(new ClassRequest(publishedServiceName, objectName));

                if (ar.getReplyCode() >= ReplyConstants.PROBLEMREPLY) {
                    if (ar instanceof RequestFailedReply) {
                        throw new ConnectionException(((RequestFailedReply) ar).getFailureReason());
                    } else if (ar instanceof ClassRetrievalFailedReply) {
                        ClassRetrievalFailedReply crfr = (ClassRetrievalFailedReply) ar;

                        throw new ConnectionException("Class Retrieval Failed - " + crfr.getReason());
                    }    //TODO others.
                }

                cr = (ClassReply) ar;
            } catch (NotPublishedException npe) {
                throw new ConnectionException("Service " + publishedServiceName + " not published on Server");
            }

            tcl = new TransportedClassLoader(m_hostContext.getInvocationHandler().getInterfacesClassLoader());

            tcl.add(proxyClassName, cr.getProxyClassBytes());

            m_publishedServiceClassLoaders.put(proxyClassName, tcl);
        }

        return tcl.loadClass(proxyClassName);
    }

    protected Object getInstance(String publishedServiceName, String objectName, DefaultProxyHelper proxyHelper) throws ConnectionException {

        try {
            Class clazz = getFacadeClass(publishedServiceName, objectName);
            Constructor[] constructors = clazz.getConstructors();
            Object retVal = constructors[0].newInstance(new Object[]{proxyHelper});

            return retVal;
        } catch (InvocationTargetException ite) {
            throw new ConnectionException("Generated class not instantiated.", ite.getTargetException());
        } catch (ClassNotFoundException cnfe) {
            throw new ConnectionException("Generated class not found during lookup.", cnfe);
        } catch (InstantiationException ie) {
            throw new ConnectionException("Generated class not instantiable during lookup.", ie);
        } catch (IllegalAccessException iae) {
            throw new ConnectionException("Illegal access to generated class during lookup.", iae);
        }
    }

    /**
     * Method close
     */
    public void close() {
        m_hostContext.getInvocationHandler().close();
    }
}
