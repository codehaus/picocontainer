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
package org.nanocontainer.remoting.client;



/**
 * Interface ProxyHelper
 *
 * @author Paul Hammant
 * @author Vinay Chandrasekharan <a href="mailto:vinayc77@yahoo.com">vinayc77@yahoo.com</a>
 * @version $Revision: 1.2 $
 */
public interface ProxyHelper {

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
    Object processObjectRequestGettingFacade(Class returnClassType, String methodSignature, Object[] args, String objectName) throws Throwable;

    /**
     * Method processObjectRequest
     *
     * @param methodSignature
     * @param args
     * @return
     * @throws Throwable
     */
    Object processObjectRequest(String methodSignature, Object[] args, Class[] argClasses) throws Throwable;

    /**
     * Method processVoidRequest
     *
     * @param methodSignature the method signature
     * @param args            The Arguments themselves
     * @throws Throwable
     */
    void processVoidRequest(String methodSignature, Object[] args, Class[] argClasses) throws Throwable;

    /**
     * Method queueAsyncRequest
     *
     * @param methodSignature the method signature
     * @param args            The Arguments themselves
     */
    void queueAsyncRequest(String methodSignature, Object[] args, Class[] argClasses);

    /**
     * Method commitAsyncRequests
     *
     * @throws Throwable
     */
    void commitAsyncRequests() throws Throwable;

    /**
     * Method rollbackAsyncRequests
     */
    void rollbackAsyncRequests();

    /**
     * Method processVoidRequestWithRedirect
     *
     * @param methodSignature
     * @param args
     * @throws Throwable
     */
    void processVoidRequestWithRedirect(String methodSignature, Object[] args, Class[] argClasses) throws Throwable;

    /**
     * Method getReferenceID
     *
     * @param factory
     * @return
     */
    Long getReferenceID(Object factory);

    boolean isEquals(Object o1, Object o2);

    void setClientContextClientFactory(ClientContextFactory clientContextClientFactory);


}
