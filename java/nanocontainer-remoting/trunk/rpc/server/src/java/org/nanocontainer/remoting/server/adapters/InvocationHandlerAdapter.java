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
package org.nanocontainer.remoting.server.adapters;


import org.nanocontainer.remoting.ClientContext;
import org.nanocontainer.remoting.Contextualizable;
import org.nanocontainer.remoting.commands.ClassReply;
import org.nanocontainer.remoting.commands.ClassRequest;
import org.nanocontainer.remoting.commands.ClassRetrievalFailedReply;
import org.nanocontainer.remoting.commands.ExceptionReply;
import org.nanocontainer.remoting.commands.GarbageCollectionReply;
import org.nanocontainer.remoting.commands.GarbageCollectionRequest;
import org.nanocontainer.remoting.commands.GroupedMethodRequest;
import org.nanocontainer.remoting.commands.InvocationExceptionReply;
import org.nanocontainer.remoting.commands.ListMethodsReply;
import org.nanocontainer.remoting.commands.ListMethodsRequest;
import org.nanocontainer.remoting.commands.ListReply;
import org.nanocontainer.remoting.commands.LookupReply;
import org.nanocontainer.remoting.commands.LookupRequest;
import org.nanocontainer.remoting.commands.MethodAsyncRequest;
import org.nanocontainer.remoting.commands.MethodFacadeArrayReply;
import org.nanocontainer.remoting.commands.MethodFacadeReply;
import org.nanocontainer.remoting.commands.MethodFacadeRequest;
import org.nanocontainer.remoting.commands.MethodReply;
import org.nanocontainer.remoting.commands.MethodRequest;
import org.nanocontainer.remoting.commands.NoSuchSessionReply;
import org.nanocontainer.remoting.commands.NotPublishedReply;
import org.nanocontainer.remoting.commands.OpenConnectionReply;
import org.nanocontainer.remoting.commands.OpenConnectionRequest;
import org.nanocontainer.remoting.commands.PingReply;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.ReplyConstants;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.commands.RequestConstants;
import org.nanocontainer.remoting.commands.RequestFailedReply;
import org.nanocontainer.remoting.commands.SameVMReply;
import org.nanocontainer.remoting.commands.SuspendedReply;
import org.nanocontainer.remoting.common.AuthenticationException;
import org.nanocontainer.remoting.common.MethodNameHelper;
import org.nanocontainer.remoting.common.Session;
import org.nanocontainer.remoting.server.Authenticator;
import org.nanocontainer.remoting.server.ClassRetrievalException;
import org.nanocontainer.remoting.server.ClassRetriever;
import org.nanocontainer.remoting.server.MethodInvocationHandler;
import org.nanocontainer.remoting.server.ServerInvocationHandler;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.authenticators.DefaultAuthenticator;
import org.nanocontainer.remoting.server.classretrievers.NoClassRetriever;
import org.nanocontainer.remoting.server.monitors.ConsoleServerMonitor;
import org.nanocontainer.remoting.server.transports.DefaultMethodInvocationHandler;
import org.nanocontainer.remoting.server.transports.DefaultServerSideClientContextFactory;

import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class InvocationHandlerAdapter
 *
 * @author Paul Hammant
 * @version $Revision: 1.3 $
 */
public class InvocationHandlerAdapter extends PublicationAdapter implements ServerInvocationHandler {

    private static long c_session = 0;
    private static final UID U_ID = new UID((short) 20729);
    private Long m_lastSession = new Long(0);
    private HashMap m_sessions = new HashMap();
    private boolean m_suspend = false;
    private ClassRetriever m_classRetriever = new NoClassRetriever();
    private Authenticator m_authenticator = new DefaultAuthenticator();
    private ServerMonitor m_serverMonitor;

    private ServerSideClientContextFactory m_clientContextFactory;


    public InvocationHandlerAdapter(ClassRetriever classRetriever, Authenticator authenticator, ServerMonitor serverMonitor, ServerSideClientContextFactory clientContextFactory) {
        this.m_classRetriever = classRetriever;
        this.m_authenticator = authenticator;
        this.m_serverMonitor = serverMonitor;
        this.m_clientContextFactory = clientContextFactory;
    }

    /**
     * Handle an invocation
     *
     * @param request The request
     * @return The reply.
     */
    public Reply handleInvocation(Request request, Object connectionDetails) {

        try {
            if (m_suspend == true) {
                return new SuspendedReply();
            }

            // Method request is positioned first as
            // it is the one we want to be most speedy.
            if (request.getRequestCode() == RequestConstants.METHODREQUEST) {

                MethodRequest methodRequest = (MethodRequest) request;
                setClientContext(methodRequest);
                return doMethodRequest(methodRequest, connectionDetails);

            } else if (request.getRequestCode() == RequestConstants.METHODFACADEREQUEST) {
                MethodFacadeRequest methodFacadeRequest = (MethodFacadeRequest) request;
                setClientContext(methodFacadeRequest);
                return doMethodFacadeRequest(methodFacadeRequest, connectionDetails);

            } else if (request.getRequestCode() == RequestConstants.METHODASYNCREQUEST) {
                MethodAsyncRequest methodAsyncRequest = (MethodAsyncRequest) request;
                setClientContext(methodAsyncRequest);
                return doMethodAsyncRequest(methodAsyncRequest, connectionDetails);

            } else if (request.getRequestCode() == RequestConstants.GCREQUEST) {

                return doGarbageCollectionRequest(request);

            } else if (request.getRequestCode() == RequestConstants.LOOKUPREQUEST) {
                return doLookupRequest(request);

            } else if (request.getRequestCode() == RequestConstants.CLASSREQUEST) {
                return doClassRequest(request);

            } else if (request.getRequestCode() == RequestConstants.OPENCONNECTIONREQUEST) {
                OpenConnectionRequest openConnectionRequest = (OpenConnectionRequest) request;
                return doOpenConnectionRequest(openConnectionRequest.getMachineID());

            } else if (request.getRequestCode() == RequestConstants.PINGREQUEST) {

                // we could communicate back useful state info in this transaction.
                return new PingReply();
            } else if (request.getRequestCode() == RequestConstants.LISTREQUEST) {
                return doListRequest();
            } else if (request.getRequestCode() == RequestConstants.LISTMETHODSREQUEST) {
                return doListMethodsRequest(request);
            } else {
                return new RequestFailedReply("Unknown request :" + request.getClass().getName());
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            if (request instanceof MethodRequest) {
                String methd = ((MethodRequest) request).getMethodSignature();
                getServerMonitor().unexpectedException(InvocationHandlerAdapter.class, "InvocationHandlerAdapter.handleInvocation() NPE processing method " + methd, npe);
                throw new NullPointerException("Null pointer exception, processing method " + methd);
            } else {
                getServerMonitor().unexpectedException(InvocationHandlerAdapter.class, "InvocationHandlerAdapter.handleInvocation() NPE", npe);
                throw npe;
            }
        }
    }

    protected synchronized ServerSideClientContextFactory getClientContextFactory() {
        if (m_clientContextFactory == null) {
            m_clientContextFactory = new DefaultServerSideClientContextFactory();
        }
        return m_clientContextFactory;
    }

    private void setClientContext(Contextualizable request) {
        Long session = request.getSession();
        ClientContext clientSideClientContext = request.getContext();

        // *always* happens before method invocations.
        getClientContextFactory().set(session, clientSideClientContext);

    }


    /**
     * Do a Method Facade Request
     *
     * @param facadeRequest the request
     * @return The reply
     */
    private Reply doMethodFacadeRequest(MethodFacadeRequest facadeRequest, Object connectionDetails) {

        if (!sessionExists(facadeRequest.getSession()) && (connectionDetails == null || !connectionDetails.equals("callback"))) {
            return new NoSuchSessionReply(facadeRequest.getSession());
        }

        String publishedThing = facadeRequest.getPublishedServiceName() + "_" + facadeRequest.getObjectName();

        if (!isPublished(publishedThing)) {
            return new NotPublishedReply();
        }

        //if( !sessionExists( facadeRequest.getSession() ) )
        //{
        //    return new ExceptionReply(
        //        new InvocationException( "TODO - you dirty rat/hacker" ) );
        //}

        MethodInvocationHandler methodInvocationHandler = getMethodInvocationHandler(publishedThing);
        Reply ar = methodInvocationHandler.handleMethodInvocation(facadeRequest, connectionDetails);

        if (ar.getReplyCode() == ReplyConstants.EXCEPTIONREPLY) {
            return ar;
        } else if (ar.getReplyCode() >= ReplyConstants.PROBLEMREPLY) {
            return ar;
        } else if (ar.getReplyCode() == ReplyConstants.METHODREPLY) {
            Object methodReply = ((MethodReply) ar).getReplyObject();

            if (methodReply == null) {
                return new MethodFacadeReply(null, null);    // null passing
            } else if (!methodReply.getClass().isArray()) {
                return doMethodFacadeRequestNonArray(methodReply, facadeRequest);
            } else {
                return doMethodFacadeRequestArray(methodReply, facadeRequest);

            }
        } else {
            // unknown reply type from
            return new RequestFailedReply("TODO");
        }
    }

    /**
     * Do a method facade request, returning an array
     *
     * @param methodReply         The array to process.
     * @param methodFacadeRequest The request
     * @return The reply
     */
    private Reply doMethodFacadeRequestArray(Object methodReply, MethodFacadeRequest methodFacadeRequest) {
        Object[] beanImpls = (Object[]) methodReply;
        Long[] refs = new Long[beanImpls.length];
        String[] objectNames = new String[beanImpls.length];

        if (!sessionExists(methodFacadeRequest.getSession())) {
            return new NoSuchSessionReply(methodFacadeRequest.getSession());
        }

        for (int i = 0; i < beanImpls.length; i++) {
            Object impl = beanImpls[i];
            MethodInvocationHandler mainMethodInvocationHandler = getMethodInvocationHandler(methodFacadeRequest.getPublishedServiceName() + "_Main");

            objectNames[i] = MethodNameHelper.encodeClassName(mainMethodInvocationHandler.getMostDerivedType(beanImpls[i]).getName());

            MethodInvocationHandler methodInvocationHandler2 = getMethodInvocationHandler(methodFacadeRequest.getPublishedServiceName() + "_" + objectNames[i]);

            if (methodInvocationHandler2 == null) {
                return new NotPublishedReply();
            }

            //TODO a decent ref number for main?
            if (beanImpls[i] == null) {
                refs[i] = null;
            } else {
                refs[i] = methodInvocationHandler2.getOrMakeReferenceIDForBean(beanImpls[i]);

                Session sess = (Session) m_sessions.get(methodFacadeRequest.getSession());

                sess.addBeanInUse(refs[i], beanImpls[i]);
            }
        }

        return new MethodFacadeArrayReply(refs, objectNames);
    }

    /**
     * Do a method facade request, returning things other that an array
     *
     * @param beanImpl            The returned object to process.
     * @param methodFacadeRequest The request
     * @return The reply
     */
    private Reply doMethodFacadeRequestNonArray(Object beanImpl, MethodFacadeRequest methodFacadeRequest) {

        if (!sessionExists(methodFacadeRequest.getSession())) {
            return new NoSuchSessionReply(methodFacadeRequest.getSession());
        }

        MethodInvocationHandler mainMethodInvocationHandler = getMethodInvocationHandler(methodFacadeRequest.getPublishedServiceName() + "_Main");

        String objectName = MethodNameHelper.encodeClassName(mainMethodInvocationHandler.getMostDerivedType(beanImpl).getName());

        MethodInvocationHandler methodInvocationHandler = getMethodInvocationHandler(methodFacadeRequest.getPublishedServiceName() + "_" + objectName);

        if (methodInvocationHandler == null) {
            return new NotPublishedReply();
        }

        //if( !sessionExists( methodFacadeRequest.getSession() ) )
        //{
        //    return new ExceptionReply(
        //        new InvocationException( "TODO - you dirty rat/hacker" ) );
        //}

        //TODO a decent ref number for main?
        Long newRef = methodInvocationHandler.getOrMakeReferenceIDForBean(beanImpl);

        // make sure the bean is not garbage collected.
        Session sess = (Session) m_sessions.get(methodFacadeRequest.getSession());

        sess.addBeanInUse(newRef, beanImpl);

        //long newRef2 = asih2.getOrMakeReferenceIDForBean(beanImpl);
        return new MethodFacadeReply(newRef, objectName);
    }

    /**
     * Do a method request
     *
     * @param methodRequest The request
     * @return The reply
     */
    private Reply doMethodRequest(MethodRequest methodRequest, Object connectionDetails) {

        if (!sessionExists(methodRequest.getSession()) && (connectionDetails == null || !connectionDetails.equals("callback"))) {
            return new NoSuchSessionReply(methodRequest.getSession());
        }

        String publishedThing = methodRequest.getPublishedServiceName() + "_" + methodRequest.getObjectName();

        if (!isPublished(publishedThing)) {
            return new NotPublishedReply();
        }

        MethodInvocationHandler methodInvocationHandler = getMethodInvocationHandler(publishedThing);

        return methodInvocationHandler.handleMethodInvocation(methodRequest, connectionDetails);
    }

    private Reply doMethodAsyncRequest(MethodAsyncRequest methodRequest, Object connectionDetails) {

        if (!sessionExists(methodRequest.getSession())) {
            return new NoSuchSessionReply(methodRequest.getSession());
        }

        String publishedThing = methodRequest.getPublishedServiceName() + "_" + methodRequest.getObjectName();

        if (!isPublished(publishedThing)) {
            return new NotPublishedReply();
        }

        MethodInvocationHandler methodInvocationHandler = getMethodInvocationHandler(publishedThing);

        GroupedMethodRequest[] requests = methodRequest.getGroupedRequests();
        for (int i = 0; i < requests.length; i++) {
            GroupedMethodRequest rawRequest = requests[i];
            methodInvocationHandler.handleMethodInvocation(new MethodRequest(methodRequest.getPublishedServiceName(), methodRequest.getObjectName(), rawRequest.getMethodSignature(), rawRequest.getArgs(), methodRequest.getReferenceID(), methodRequest.getSession()), connectionDetails);
        }

        return new MethodReply();

    }


    /**
     * DO a lokkup request
     *
     * @param request The request
     * @return The reply
     */
    private Reply doLookupRequest(Request request) {
        LookupRequest lr = (LookupRequest) request;

        try {
            m_authenticator.checkAuthority(lr.getAuthentication(), lr.getPublishedServiceName());
        } catch (AuthenticationException aae) {
            return new ExceptionReply(aae);
        }

        //TODO a decent ref number for main?
        return new LookupReply(new Long(0));
    }

    /**
     * Do a class request
     *
     * @param request The request
     * @return The reply
     */
    private Reply doClassRequest(Request request) {
        ClassRequest cr = (ClassRequest) request;
        String publishedThing = cr.getPublishedServiceName() + "_" + cr.getObjectName();

        try {
            return new ClassReply(m_classRetriever.getProxyClassBytes(publishedThing));
        } catch (ClassRetrievalException e) {
            return new ClassRetrievalFailedReply(e.getMessage());
        }
    }

    /**
     * Do an OpenConnection request
     *
     * @return The reply.
     */
    private Reply doOpenConnectionRequest(UID machineID) {
        if (machineID != null && machineID.equals(U_ID)) {
            return new SameVMReply();
        } else {
            Long session = getNewSession();
            m_sessions.put(session, new Session(session));
            return new OpenConnectionReply(m_authenticator.getTextToSign(), session);
        }
    }

    /**
     * Do a ListRequest
     *
     * @return The reply
     */
    private Reply doListRequest() {
        //return the list of published objects to the server
        Iterator iterator = getIteratorOfPublishedObjects();
        Vector vecOfPublishedObjectNames = new Vector();

        while (iterator.hasNext()) {
            final String item = (String) iterator.next();

            if (item.endsWith("_Main")) {
                vecOfPublishedObjectNames.add(item.substring(0, item.lastIndexOf("_Main")));
            }
        }

        String[] listOfPublishedObjectNames = new String[vecOfPublishedObjectNames.size()];

        System.arraycopy(vecOfPublishedObjectNames.toArray(), 0, listOfPublishedObjectNames, 0, vecOfPublishedObjectNames.size());

        return new ListReply(listOfPublishedObjectNames);
    }

    /**
     * Do a GarbageCollection Request
     *
     * @param request The request
     * @return The reply
     */
    private Reply doGarbageCollectionRequest(Request request) {
        GarbageCollectionRequest gcr = (GarbageCollectionRequest) request;
        String publishedThing = gcr.getPublishedServiceName() + "_" + gcr.getObjectName();

        if (!isPublished(publishedThing)) {
            return new NotPublishedReply();
        }

        Long session = gcr.getSession();
        if (!sessionExists(session)) {
            return new InvocationExceptionReply("TODO - you dirty rat/hacker");
        }

        Session sess = (Session) m_sessions.get(session);
        if (sess == null) {
            System.err.println("DEBUG- GC on missing session - " + session);
        } else {
            if (gcr.getReferenceID() == null) {
                System.err.println("DEBUG- GC on missing referenceID -" + gcr.getReferenceID());
            } else {
                sess.removeBeanInUse(gcr.getReferenceID());
            }
        }

        MethodInvocationHandler methodInvocationHandler = getMethodInvocationHandler(publishedThing);

        return new GarbageCollectionReply();
    }

    /**
     * Do a ListMethods Request
     *
     * @param request The request
     * @return The reply
     */
    private Reply doListMethodsRequest(Request request) {
        ListMethodsRequest lReq = (ListMethodsRequest) request;
        String publishedThing = lReq.getPublishedName() + "_Main";

        if (!isPublished(publishedThing)) {
            //Should it throw an exception back?
            return new ListMethodsReply(new String[0]);
        }

        DefaultMethodInvocationHandler methodInvocationHandler = (DefaultMethodInvocationHandler) getMethodInvocationHandler(publishedThing);

        return new ListMethodsReply(methodInvocationHandler.getListOfMethods());
    }

    /**
     * Does a session exist
     *
     * @param session The session
     * @return true if it exists
     */
    private boolean sessionExists(Long session) {

        if (m_lastSession.equals(session)) {

            // buffer last session for performance.
            return true;
        } else {
            if (m_sessions.containsKey(session)) {
                m_lastSession = session;

                return true;
            }
        }

        return false;
    }


    /**
     * Get a new session ID
     *
     * @return The session
     */
    private Long getNewSession() {
        // approve everything and set session identifier.
        return new Long((++c_session << 16) + ((long) (Math.random() * 65536)));
    }

    /**
     * Suspend an service
     */
    public void suspend() {
        m_suspend = true;
    }

    /**
     * Resume an service
     */
    public void resume() {
        m_suspend = false;
    }

    public void setServerMonitor(ServerMonitor serverMonitor) {
        m_serverMonitor = serverMonitor;
    }

    public synchronized ServerMonitor getServerMonitor() {
        if (m_serverMonitor == null) {
            m_serverMonitor = new ConsoleServerMonitor();
        }
        return m_serverMonitor;
    }

}
