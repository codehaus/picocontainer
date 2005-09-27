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
package org.nanocontainer.remoting.server;

import org.nanocontainer.remoting.commands.MethodRequest;
import org.nanocontainer.remoting.commands.Reply;

/**
 * Class MethodInvocationHandler
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public interface MethodInvocationHandler {

    /**
     * Handle a method invocation.
     *
     * @param request the method request.
     * @return the reply for the request.
     */
    Reply handleMethodInvocation(MethodRequest request, Object connectionDetails);

    /**
     * Add an implementation bean
     *
     * @param referenceID the ref id for the bean.
     * @param beanImpl    the bean.
     */
    void addImplementationBean(Long referenceID, Object beanImpl);

    /**
     * Replace an implementation bean
     *
     * @param implBean     the implementation bean
     * @param withImplBean the new implementation bean
     */
    void replaceImplementationBean(Object implBean, Object withImplBean);

    /**
     * Get or make a reference ID for a bean.
     *
     * @param implBean the implementation bean
     * @return the reference ID
     */
    Long getOrMakeReferenceIDForBean(Object implBean);

    /**
     * Get the most derived type for a bean
     *
     * @param beanImpl the implementation bean
     * @return the most derived class type.
     */
    Class getMostDerivedType(Object beanImpl);


    void setMethodInvocationMonitor(MethodInvocationMonitor monitor);


}
