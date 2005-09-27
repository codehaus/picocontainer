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

import org.nanocontainer.remoting.commands.ExceptionReply;
import org.nanocontainer.remoting.commands.InvocationExceptionReply;
import org.nanocontainer.remoting.commands.MethodReply;
import org.nanocontainer.remoting.commands.MethodRequest;
import org.nanocontainer.remoting.commands.NoSuchReferenceReply;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.common.FacadeRefHolder;
import org.nanocontainer.remoting.server.MethodInvocationHandler;
import org.nanocontainer.remoting.server.MethodInvocationMonitor;
import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.Publisher;
import org.nanocontainer.remoting.server.monitors.NullMethodInvocationMonitor;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Class DefaultMethodInvocationHandler
 *
 * @author Paul Hammant
 * @author Vinay Chandrasekharan <a href="mailto:vinayc77@yahoo.com">vinayc77@yahoo.com</a>
 * @version $Revision: 1.2 $
 */
public class DefaultMethodInvocationHandler implements MethodInvocationHandler {

    private MessageFormat messageFormat = new MessageFormat("");

    /**
     * Beans for references
     */
    private WeakHashMap m_refBeans = new WeakHashMap();

    /**
     * References for beans.
     */
    private WeakHashMap m_beanRefs = new WeakHashMap();

    /**
     * Method map
     */
    private HashMap m_methodMap;

    /**
     * Next reference
     */
    private static int c_nextReference = 0;

    /**
     * The publisher
     */
    private Publisher m_publisher;

    /**
     * published thing
     */
    private String m_publishedThing;

    /**
     * The bean implementation
     */
    private Object m_beanImpl;

    /**
     * The publication description.
     */
    private final PublicationDescription m_publicationDescription;
    private MethodInvocationMonitor m_methodInvocationMonitor = new NullMethodInvocationMonitor();

    /**
     * Constructor DefaultMethodInvocationHandler
     *
     * @param publisher              The publisher
     * @param publishedThing         The published Thing
     * @param methodMap              The method map
     * @param publicationDescription The publication description
     */
    public DefaultMethodInvocationHandler(Publisher publisher, String publishedThing, HashMap methodMap, PublicationDescription publicationDescription) {

        m_publisher = publisher;
        m_publishedThing = publishedThing;
        m_methodMap = methodMap;
        m_publicationDescription = publicationDescription;
    }

    /**
     * Method toString
     *
     * @return a string.
     */
    public String toString() {
        return "DMIH:" + m_publishedThing;
    }

    /**
     * Add an Implementation Bean
     *
     * @param referenceID The reference ID
     * @param beanImpl    The bean implementaion
     */
    public void addImplementationBean(Long referenceID, Object beanImpl) {

        if (referenceID.equals(new Long(0))) {
            m_beanImpl = beanImpl;
        }

        m_refBeans.put(referenceID, new WeakReference(beanImpl));
        m_beanRefs.put(beanImpl, referenceID);
    }

    /**
     * Method replaceImplementationBean
     *
     * @param implBean     The bean implementaion
     * @param withImplBean The new bean implementaion.
     */
    public void replaceImplementationBean(Object implBean, Object withImplBean) {

        Long ref = (Long) m_beanRefs.get(implBean);

        m_refBeans.put(ref, new WeakReference(withImplBean));
        m_beanRefs.remove(implBean);
        m_beanRefs.put(withImplBean, ref);

        if (m_beanImpl == implBean) {
            m_beanImpl = withImplBean;
        }
    }

    /**
     * Get or make a reference ID for a bean
     *
     * @param implBean The bean implementaion
     * @return A reference ID
     */
    public Long getOrMakeReferenceIDForBean(Object implBean) {

        Long ref = (Long) m_beanRefs.get(implBean);

        if (ref == null) {
            ref = getNewReference();
            addImplementationBean(ref, implBean);
        }

        return ref;
    }

    /**
     * Handle a method invocation
     *
     * @param request The emthod request
     * @return The reply.
     */
    public Reply handleMethodInvocation(MethodRequest request, Object connectionDetails) {

        String methodSignature = request.getMethodSignature();

        if (!m_methodMap.containsKey(methodSignature)) {

            m_methodInvocationMonitor.missingMethod(methodSignature, connectionDetails);
            return new InvocationExceptionReply("Method '" + methodSignature + "' not present in impl");
        }

        Method method = (Method) m_methodMap.get(methodSignature);

        Object beanImpl = null;

        try {
            WeakReference wr = (WeakReference) m_refBeans.get(request.getReferenceID());

            if (wr == null) {
                m_methodInvocationMonitor.invalidReference(methodSignature, connectionDetails);
                return new NoSuchReferenceReply(request.getReferenceID());
            }

            beanImpl = wr.get();

            if (beanImpl == null) {
                m_methodInvocationMonitor.invalidReference(methodSignature, connectionDetails);
                return new NoSuchReferenceReply(request.getReferenceID());
            }

            Object[] args = request.getArgs();

            correctArgs(request, args);
            m_methodInvocationMonitor.methodInvoked(beanImpl.getClass(), methodSignature, connectionDetails);
            return new MethodReply(method.invoke(beanImpl, request.getArgs()));
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();

            m_methodInvocationMonitor.invocationTargetException(beanImpl.getClass(), methodSignature, ite, connectionDetails);
            if (t instanceof Serializable) {

                // NOTE Sever side stack traces will appear on the client side
                return new ExceptionReply(t);
            } else {
                return new InvocationExceptionReply("Exception was not serializable :" + t.getClass().getName());
            }
        } catch (Throwable t) {
            m_methodInvocationMonitor.invocationException(beanImpl == null ? null : beanImpl.getClass(), methodSignature, t, connectionDetails);
            return new InvocationExceptionReply("Some ServerSide exception problem :" + t.getMessage());
        }
    }

    /**
     * Correct the arguments for a request (seme are 'additional facades' and can;t be serialized).
     *
     * @param methodRequest The method request
     * @param args          The arguments to correct
     */
    private void correctArgs(MethodRequest methodRequest, Object[] args) {

        for (int i = 0; i < args.length; i++) {

            // TODO find a faster way to do this....
            if (args[i] instanceof FacadeRefHolder) {
                FacadeRefHolder frh = (FacadeRefHolder) args[i];
                DefaultMethodInvocationHandler methodInvocationHandler = (DefaultMethodInvocationHandler) m_publisher.getMethodInvocationHandler(frh.getObjectName());
                WeakReference wr = (WeakReference) methodInvocationHandler.m_refBeans.get(frh.getReferenceID());

                args[i] = wr.get();
            }
        }
    }

    /**
     * Get the most derived type.
     *
     * @param beanImpl The bean implementaion
     * @return The class
     */
    public Class getMostDerivedType(Object beanImpl) {
        return m_publicationDescription.getMostDerivedType(beanImpl);
    }

    /**
     * Encode a class name
     *
     * @param className The class name
     * @return the enoded class name.
     */
    public String encodeClassName(String className) {
        return className.replace('.', '$');
    }

    /**
     * Get the list of remote method names
     */
    public String[] getListOfMethods() {
        String[] methodNames = (String[]) m_methodMap.keySet().toArray(new String[0]);
        return methodNames;
    }

    /**
     * Get a new reference ID
     *
     * @return The reference
     */
    private Long getNewReference() {
        // approve everything and set session identifier.
        return new Long((++c_nextReference << 16) + ((long) (Math.random() * 65536)));
    }

    public void setMethodInvocationMonitor(MethodInvocationMonitor monitor) {
        m_methodInvocationMonitor = monitor;
    }

}
