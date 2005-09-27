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

import org.nanocontainer.remoting.client.ClientContextFactory;
import org.nanocontainer.remoting.client.ClientInvocationHandler;
import org.nanocontainer.remoting.client.InvocationException;
import org.nanocontainer.remoting.client.NoSuchReferenceException;
import org.nanocontainer.remoting.client.NoSuchSessionException;
import org.nanocontainer.remoting.client.Proxy;
import org.nanocontainer.remoting.client.ProxyHelper;
import org.nanocontainer.remoting.commands.ExceptionReply;
import org.nanocontainer.remoting.commands.GarbageCollectionReply;
import org.nanocontainer.remoting.commands.GarbageCollectionRequest;
import org.nanocontainer.remoting.commands.GroupedMethodRequest;
import org.nanocontainer.remoting.commands.InvocationExceptionReply;
import org.nanocontainer.remoting.commands.MethodAsyncRequest;
import org.nanocontainer.remoting.commands.MethodFacadeArrayReply;
import org.nanocontainer.remoting.commands.MethodFacadeReply;
import org.nanocontainer.remoting.commands.MethodFacadeRequest;
import org.nanocontainer.remoting.commands.MethodReply;
import org.nanocontainer.remoting.commands.MethodRequest;
import org.nanocontainer.remoting.commands.NoSuchReferenceReply;
import org.nanocontainer.remoting.commands.NoSuchSessionReply;
import org.nanocontainer.remoting.commands.PublishedNameRequest;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.ReplyConstants;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class DefaultProxyHelper
 *
 * @author Paul Hammant
 * @author Vinay Chandrasekharan <a href="mailto:vinayc77@yahoo.com">vinayc77@yahoo.com</a>
 * @version $Revision: 1.3 $
 */
public final class DefaultProxyHelper implements ProxyHelper {

    private final transient AbstractFactory m_factory;
    private final transient ClientInvocationHandler m_clientInvocationHandler;
    private final transient String m_publishedServiceName;
    private final transient String m_objectName;
    private final transient Long m_referenceID;
    private final transient Long m_session;
    private ClientContextFactory m_clientContextClientFactory;
    private ArrayList queuedAsyncRequests = new ArrayList();

    /**
     * Constructor DefaultProxyHelper
     *
     * @param abstractFactory
     * @param clientInvocationHandler
     * @param pubishedServiceName
     * @param objectName
     * @param referenceID
     * @param session
     */
    public DefaultProxyHelper(AbstractFactory abstractFactory, ClientInvocationHandler clientInvocationHandler, String pubishedServiceName, String objectName, Long referenceID, Long session) {

        m_factory = abstractFactory;
        m_clientInvocationHandler = clientInvocationHandler;
        m_publishedServiceName = pubishedServiceName;
        m_objectName = objectName;
        m_referenceID = referenceID;
        m_session = session;
        if (abstractFactory == null) {
            throw new IllegalArgumentException("abstractFactory cannot be null");
        }
        if (clientInvocationHandler == null) {
            throw new IllegalArgumentException("clientInvocationHandler cannot be null");
        }

    }

    /**
     * Method registerImplObject
     *
     * @param implBean
     */
    public void registerImplObject(Object implBean) {
        m_factory.registerReferenceObject(implBean, m_referenceID);
    }

    /**
     * Method processObjectRequestGettingFacade
     *
     * @param returnClassType
     * @param methodSignature
     * @param args
     * @param objectName
     * @return
     * @throws Throwable
     */

    public Object processObjectRequestGettingFacade(Class returnClassType, String methodSignature, Object[] args, String objectName) throws Throwable {
        try {
            return processObjectRequestGettingFacade2(returnClassType, methodSignature, args, objectName);
        } catch (InvocationException ie) {
            m_clientInvocationHandler.getClientMonitor().invocationFailure(this.getClass(), this.getClass().getName(), ie);
            throw ie;
        }
    }

    private Object processObjectRequestGettingFacade2(Class returnClassType, String methodSignature, Object[] args, String objectName) throws Throwable {

        boolean arrayRetVal = objectName.endsWith("[]");
        String objNameWithoutArray = objectName.substring(0, objectName.length() - 2);
        MethodFacadeRequest request;

        if (arrayRetVal) {
            request = new MethodFacadeRequest(m_publishedServiceName, m_objectName, methodSignature, args, m_referenceID, objNameWithoutArray, m_session);
        } else {
            request = new MethodFacadeRequest(m_publishedServiceName, m_objectName, methodSignature, args, m_referenceID, objectName, m_session);
        }

        setContext(request);
        Reply reply = m_clientInvocationHandler.handleInvocation(request);

        if (reply.getReplyCode() == ReplyConstants.METHODFACADEREPLY) {
            MethodFacadeReply mfr = (MethodFacadeReply) reply;
            Long ref = mfr.getReferenceID();

            // it might be that the return value was intended to be null.
            if (ref == null) {
                return null;
            }

            Object implBean = m_factory.getImplObj(ref);

            if (implBean == null) {
                DefaultProxyHelper pHelper = new DefaultProxyHelper(m_factory, m_clientInvocationHandler, m_publishedServiceName, mfr.getObjectName(), ref, m_session);
                Object retFacade = m_factory.getInstance(m_publishedServiceName, mfr.getObjectName(), pHelper);

                pHelper.registerImplObject(retFacade);

                return retFacade;
            } else {
                return implBean;
            }
        } else if (reply.getReplyCode() == ReplyConstants.METHODFACADEARRAYREPLY) {
            MethodFacadeArrayReply mfar = (MethodFacadeArrayReply) reply;
            Long[] refs = mfar.getReferenceIDs();
            String[] objectNames = mfar.getObjectNames();
            Object[] implBeans = (Object[]) Array.newInstance(returnClassType, refs.length);

            for (int i = 0; i < refs.length; i++) {
                Long ref = refs[i];

                if (ref == null) {
                    implBeans[i] = null;
                } else {
                    Object o = m_factory.getImplObj(ref);

                    implBeans[i] = o;

                    if (implBeans[i] == null) {
                        DefaultProxyHelper bo2 = new DefaultProxyHelper(m_factory, m_clientInvocationHandler, m_publishedServiceName, objectNames[i], refs[i], m_session);
                        Object retFacade = null;

                        try {
                            retFacade = m_factory.getInstance(m_publishedServiceName, objectNames[i], bo2);
                        } catch (Exception e) {
                            System.out.println("objNameWithoutArray=" + returnClassType.getName());
                            System.out.flush();
                            e.printStackTrace();
                        }

                        bo2.registerImplObject(retFacade);

                        implBeans[i] = retFacade;
                    }
                }
            }

            return implBeans;
        } else {
            throw makeUnexpectedReplyThrowable(reply);
        }
    }

    /**
     * Method processObjectRequest
     *
     * @param methodSignature
     * @param args
     * @return
     * @throws Throwable
     */
    public Object processObjectRequest(String methodSignature, Object[] args, Class[] argClasses) throws Throwable {

        try {
            m_factory.marshallCorrection(m_publishedServiceName, methodSignature, args, argClasses);

            MethodRequest request = new MethodRequest(m_publishedServiceName, m_objectName, methodSignature, args, m_referenceID, m_session);
            setContext(request);
            Reply reply = m_clientInvocationHandler.handleInvocation(request);

            if (reply.getReplyCode() == ReplyConstants.METHODREPLY) {
                MethodReply or = (MethodReply) reply;

                return or.getReplyObject();
            } else {
                throw makeUnexpectedReplyThrowable(reply);
            }
        } catch (InvocationException ie) {
            m_clientInvocationHandler.getClientMonitor().invocationFailure(this.getClass(), this.getClass().getName(), ie);
            throw ie;
        }
    }


    /**
     * Method processVoidRequest
     *
     * @param methodSignature
     * @param args
     * @throws Throwable
     */
    public void processVoidRequest(String methodSignature, Object[] args, Class[] argClasses) throws Throwable {

        try {
            m_factory.marshallCorrection(m_publishedServiceName, methodSignature, args, argClasses);

            MethodRequest request = new MethodRequest(m_publishedServiceName, m_objectName, methodSignature, args, m_referenceID, m_session);

            //debug(args);
            setContext(request);
            Reply reply = m_clientInvocationHandler.handleInvocation(request);

            if (reply.getReplyCode() == ReplyConstants.METHODREPLY) {
                MethodReply or = (MethodReply) reply;

                return;
            } else {
                throw makeUnexpectedReplyThrowable(reply);
            }
        } catch (InvocationException ie) {
            m_clientInvocationHandler.getClientMonitor().invocationFailure(this.getClass(), this.getClass().getName(), ie);
            throw ie;
        }
    }

    public void queueAsyncRequest(String methodSignature, Object[] args, Class[] argClasses) {

        synchronized (queuedAsyncRequests) {

            GroupedMethodRequest request = new GroupedMethodRequest(methodSignature, args);
            queuedAsyncRequests.add(request);
        }

    }

    public void commitAsyncRequests() throws Throwable {

        synchronized (queuedAsyncRequests) {

            try {
                GroupedMethodRequest[] rawRequests = new GroupedMethodRequest[queuedAsyncRequests.size()];
                queuedAsyncRequests.toArray(rawRequests);
                MethodAsyncRequest request = new MethodAsyncRequest(m_publishedServiceName, m_objectName, rawRequests, m_referenceID, m_session);

                //debug(args);
                setContext(request);
                Reply reply = m_clientInvocationHandler.handleInvocation(request);

                if (reply.getReplyCode() == ReplyConstants.METHODREPLY) {
                    MethodReply or = (MethodReply) reply;
                    return;
                } else {
                    throw makeUnexpectedReplyThrowable(reply);
                }
            } catch (InvocationException ie) {
                m_clientInvocationHandler.getClientMonitor().invocationFailure(this.getClass(), this.getClass().getName(), ie);
                throw ie;
            }
        }
    }

    public void rollbackAsyncRequests() {
        synchronized (queuedAsyncRequests) {
            queuedAsyncRequests.clear();
        }
    }


    /**
     * Method processVoidRequestWithRedirect
     *
     * @param methodSignature
     * @param args
     * @throws Throwable
     */
    public void processVoidRequestWithRedirect(String methodSignature, Object[] args, Class[] argClasses) throws Throwable {

        Object[] newArgs = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Proxy) {

                //TODO somehow get the reference details and put a redirect place holder here
            } else {
                newArgs[i] = args[i];
            }
        }

        processVoidRequest(methodSignature, newArgs, argClasses);
    }


    /**
     * @param reply The reply that represents the unexpected reply.
     * @return The exception that is pertient.
     */
    private Throwable makeUnexpectedReplyThrowable(Reply reply) {

        if (reply.getReplyCode() == ReplyConstants.EXCEPTIONREPLY) {
            ExceptionReply er = (ExceptionReply) reply;
            return er.getReplyException();
        } else if (reply.getReplyCode() == ReplyConstants.NOSUCHSESSIONREPLY) {
            NoSuchSessionReply nssr = (NoSuchSessionReply) reply;
            return new NoSuchSessionException(nssr.getSessionID());
        }
        //TODO remove some of these if clover indicates they are not used?
        else if (reply.getReplyCode() == ReplyConstants.NOSUCHREFERENCEREPLY) {
            NoSuchReferenceReply nsrr = (NoSuchReferenceReply) reply;
            return new NoSuchReferenceException(nsrr.getReferenceID());
        } else if (reply.getReplyCode() == ReplyConstants.INVOCATIONEXCEPTIONREPLY) {
            InvocationExceptionReply ier = (InvocationExceptionReply) reply;
            return new InvocationException(ier.getMessage());
        } else {
            return new InvocationException("Internal Error : Unknown reply type :" + reply.getClass().getName());
        }
    }

    /**
     * Method getReferenceID
     *
     * @param factory
     * @return
     */
    public Long getReferenceID(Object factory) {

        // this checks the m_factory because reference IDs should not be
        // given out to any requester.  It should be privileged information.
        if (factory == m_factory) {
            return m_referenceID;
        } else {
            return null;
        }
    }

    public boolean isEquals(Object o1, Object o2) {
        if (o2 == null) {
            return false;
        }
        if (o1 == o2) {
            return true;
        }
        if (o1.getClass() != o2.getClass()) {
            return false;
        }
        try {
            Object retVal = processObjectRequest("equals(java.lang.Object)", new Object[]{o2}, new Class[]{Object.class});
            return ((Boolean) retVal).booleanValue();
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                t.printStackTrace();
                throw new org.nanocontainer.remoting.client.InvocationException("Should never get here: " + t.getMessage());
            }
        }


    }

    protected void finalize() throws Throwable {

        synchronized (m_factory) {
            Reply reply = m_clientInvocationHandler.handleInvocation(new GarbageCollectionRequest(m_publishedServiceName, m_objectName, m_session, m_referenceID));
            if (reply instanceof ExceptionReply) {
                // This happens if the object can not be GCed on the remote server
                //  for any reason.
                // There is nothing that we can do about it from here, so just let
                //  this fall through.
                // One case where this can happen is if the server is restarted quickly.
                //  An object created in one ivocation will try to be gced in the second
                //  invocation.  As the object does not exist, an error is thrown.
                /*
                System.out.println("----> Got an ExceptionReply in response to a GarbageCollectionRequest" );
                ExceptionReply er = (ExceptionReply)reply;
                er.getReplyException().printStackTrace();
                */
            } else if (!(reply instanceof GarbageCollectionReply)) {
                System.err.println("----> Some problem during DGC! Make sure m_factory is closed. ");
            }
        }
        super.finalize();
    }

    public void setClientContextClientFactory(ClientContextFactory clientContextClientFactory) {
        m_clientContextClientFactory = clientContextClientFactory;
    }

    private synchronized void setContext(PublishedNameRequest request) {

        if (m_clientContextClientFactory == null) {
            m_clientContextClientFactory = new DefaultClientContextFactory();
        }
        request.setContext(m_clientContextClientFactory.getClientContext());

    }


}
