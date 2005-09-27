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

import org.nanocontainer.remoting.Authentication;
import org.nanocontainer.remoting.client.ClientInvocationHandler;
import org.nanocontainer.remoting.client.Factory;
import org.nanocontainer.remoting.client.HostContext;
import org.nanocontainer.remoting.client.Proxy;
import org.nanocontainer.remoting.commands.ExceptionReply;
import org.nanocontainer.remoting.commands.ListReply;
import org.nanocontainer.remoting.commands.ListRequest;
import org.nanocontainer.remoting.commands.LookupReply;
import org.nanocontainer.remoting.commands.LookupRequest;
import org.nanocontainer.remoting.commands.NotPublishedReply;
import org.nanocontainer.remoting.commands.OpenConnectionReply;
import org.nanocontainer.remoting.commands.OpenConnectionRequest;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.ReplyConstants;
import org.nanocontainer.remoting.commands.SameVMReply;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.FacadeRefHolder;

import java.lang.ref.WeakReference;
import java.rmi.server.UID;
import java.util.HashMap;


/**
 * Class AbstractFactory
 *
 * @author Paul Hammant
 * @author Peter Royal <a href="mailto:proyal@managingpartners.com">proyal@managingpartners.com</a>
 * @version $Revision: 1.3 $
 */
public abstract class AbstractFactory implements Factory {

    private static final UID U_ID = new UID((short) 20729);
    private static final int STEM_LEN = "NanoContainerRemotingGenerated".length();
    protected final HostContext m_hostContext;
    protected ClientInvocationHandler m_clientInvocationHandler;
    protected final HashMap m_refObjs = new HashMap();
    private transient String m_textToSign;
    protected Long m_session;


    public AbstractFactory(HostContext hostContext, boolean allowOptimize) throws ConnectionException {
        m_hostContext = hostContext;
        m_clientInvocationHandler = m_hostContext.getInvocationHandler();
        m_clientInvocationHandler.initialize();

        UID machineID = allowOptimize ? U_ID : null;

        if (!(m_hostContext instanceof AbstractSameVmBindableHostContext)) {
            machineID = null;
        }
        Reply reply = m_clientInvocationHandler.handleInvocation(new OpenConnectionRequest(machineID));

        if (reply instanceof SameVMReply) {
            if (m_hostContext instanceof AbstractSameVmBindableHostContext) {
                AbstractSameVmBindableHostContext sameVmBindableHostContext = (AbstractSameVmBindableHostContext) m_hostContext;
                HostContext hContext = sameVmBindableHostContext.makeSameVmHostContext();
                if (hContext == null) {
                    // Registry not found, or a different instance to the one
                    // the server placed its piped instance in.
                    // revert to non optimized.
                    reply = m_clientInvocationHandler.handleInvocation(new OpenConnectionRequest(null));
                } else {
                    m_clientInvocationHandler = m_hostContext.getInvocationHandler();
                    reply = m_clientInvocationHandler.handleInvocation(new OpenConnectionRequest());
                }
            } else {
                throw new ConnectionException("SameVM instruction for non rebindable host context.");
            }
        }

        if (reply instanceof OpenConnectionReply) {
            m_textToSign = ((OpenConnectionReply) reply).getTextToSign();
            m_session = ((OpenConnectionReply) reply).getSession();
        } else {

            throw new ConnectionException("Setting of host context blocked for reasons of unknown, server-side reply: (" + reply.getClass().getName() + ")");
        }

    }

    /**
     * Method lookup
     *
     * @param publishedServiceName
     * @param authentication
     * @return
     * @throws ConnectionException
     */
    public Object lookup(String publishedServiceName, Authentication authentication) throws ConnectionException {

        Reply ar = m_clientInvocationHandler.handleInvocation(new LookupRequest(publishedServiceName, authentication, m_session));

        if (ar.getReplyCode() >= ReplyConstants.PROBLEMREPLY) {
            if (ar instanceof NotPublishedReply) {
                throw new ConnectionException("Service " + publishedServiceName + " not published");
            } else if (ar instanceof ExceptionReply) {
                ExceptionReply er = (ExceptionReply) ar;

                throw (ConnectionException) er.getReplyException();
            } else {
                throw new ConnectionException("Problem doing lookup on service");
            }
        } else if (ar instanceof ExceptionReply) {
            ExceptionReply er = (ExceptionReply) ar;
            Throwable t = er.getReplyException();

            if (t instanceof ConnectionException) {
                throw (ConnectionException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new ConnectionException("Problem doing lookup on service [exception: " + t.getMessage() + "]");
            }
        } else if (!(ar instanceof LookupReply)) {
            throw new UnsupportedOperationException("Unexpected reply to lookup [reply: " + ar + "]");
        }

        LookupReply lr = (LookupReply) ar;
        DefaultProxyHelper baseObj = new DefaultProxyHelper(this, m_clientInvocationHandler, publishedServiceName, "Main", lr.getReferenceID(), m_session);
        Object retVal = getInstance(publishedServiceName, "Main", baseObj);

        baseObj.registerImplObject(retVal);

        return retVal;
    }

    protected abstract Class getFacadeClass(String publishedServiceName, String objectName) throws ConnectionException, ClassNotFoundException;

    protected abstract Object getInstance(String publishedServiceName, String objectName, DefaultProxyHelper proxyHelper) throws ConnectionException;

    /**
     * Method registerReferenceObject
     *
     * @param obj
     * @param referenceID
     */
    public final void registerReferenceObject(Object obj, Long referenceID) {

        synchronized (this) {
            m_refObjs.put(referenceID, new WeakReference(obj));
        }

        //Object o = m_refObjs.get(referenceID);
    }

    /**
     * Method getReferenceID
     *
     * @param obj
     * @return
     */
    public final Long getReferenceID(Proxy obj) {
        return obj.nanocontainerRemotingGetReferenceID(this);
    }

    /**
     * Method getImplObj
     *
     * @param referenceID
     * @return
     */
    public final Object getImplObj(Long referenceID) {

        WeakReference wr = null;

        synchronized (this) {
            wr = (WeakReference) m_refObjs.get(referenceID);
        }

        if (wr == null) {
            return null;
        }

        Object obj = wr.get();

        if (obj == null) {
            m_refObjs.remove(referenceID);
        }

        return obj;
    }

    /**
     * Method lookup
     *
     * @param publishedServiceName
     * @return
     * @throws ConnectionException
     */
    public final Object lookup(String publishedServiceName) throws ConnectionException {
        return lookup(publishedServiceName, null);
    }

    /**
     * Method getTextToSignForAuthentication
     *
     * @return
     */
    public String getTextToSignForAuthentication() {
        return m_textToSign;
    }

    /**
     * Method list
     */
    public String[] list() {

        Reply ar = m_clientInvocationHandler.handleInvocation(new ListRequest());

        if (ar instanceof ListReply) {
            return ((ListReply) ar).getListOfPublishedObjects();
        } else {
            return new String[]{};
        }
    }


    /**
     * Is the service published.
     *
     * @param publishedServiceName
     * @return
     */
    public boolean hasService(String publishedServiceName) {
        String[] services = list();
        for (int i = 0; i < services.length; i++) {
            String service = services[i];
            if (service.equals(publishedServiceName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Wraps the reference to the remote obj within the FacadeRefHolder obj.
     *
     * @param obj
     * @param objectName
     * @return
     */
    private FacadeRefHolder makeFacadeRefHolder(Proxy obj, String objectName) {

        Long refID = getReferenceID(obj);

        return new FacadeRefHolder(refID, objectName);
    }


    public void marshallCorrection(String remoteObjName, String methodSignature, Object[] args, Class[] argClasses) {

        for (int i = 0; i < args.length; i++) {
            Class argClass = argClasses[i];
            if (argClass == null) {
                continue;
            }
            //All remote references implement Proxy interface
            if (args[i] instanceof Proxy) {
                Proxy proxy = (Proxy) args[i];

                if (getReferenceID(proxy) != null) {
                    //The stripping "NanoContainerRemotingGenerated" from the proxy names generated by ProxyGenerator
                    String objName = args[i].getClass().getName().substring(STEM_LEN);

                    args[i] = makeFacadeRefHolder(proxy, objName);
                }
            } else // Let the specific InvocationHandlers be given the last chance to modify the arguments.
            {
                args[i] = m_clientInvocationHandler.resolveArgument(remoteObjName, methodSignature, argClasses[i], args[i]);
            }
        }
    }

}
