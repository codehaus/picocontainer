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

/**
 * Interface Server
 *
 * @author Paul Hammant
 * @author Vinay Chandrasekharan <a href="mailto:vinayc77@yahoo.com">vinayc77@yahoo.com</a>
 * @version * $Revision: 1.2 $
 */
public interface Publisher {

    /**
     * Publish a object for subsequent lookup.
     *
     * @param impl              the object implementing the principle interface.
     * @param asName            the lookup name of the published object
     * @param interfaceToExpose the principal interface being published
     * @throws PublicationException if there is a problem publishing
     */
    void publish(Object impl, String asName, Class interfaceToExpose) throws PublicationException;

    /**
     * Publish a object for subsequent lookup.
     *
     * @param impl                   the object implementing the principle interface.
     * @param asName                 the lookup name of the published object
     * @param publicationDescription describing complex publishing cases.
     * @throws PublicationException if there is a problem publishing
     */
    void publish(Object impl, String asName, PublicationDescription publicationDescription) throws PublicationException;

    /**
     * UnPublish a previously published object.
     *
     * @param impl          the object implementing the principle interface.
     * @param publishedName the lookup name of the published object
     * @throws org.nanocontainer.remoting.server.PublicationException
     *          if there is a problem publishing
     */
    void unPublish(Object impl, String publishedName) throws PublicationException;

    /**
     * Replace Published object with another.
     *
     * @param oldImpl       the old object implementing the principle interface.
     * @param publishedName the lookup name of the published object
     * @param withImpl      the new object implementing the principle interface.
     * @throws PublicationException if there is a problem publishing
     */
    void replacePublished(Object oldImpl, String publishedName, Object withImpl) throws PublicationException;

    /**
     * Get the MethodInvocationHandler for this transport.  Used in special adapters.
     *
     * @param methodRequest used as a hint for getting the right handler.
     * @param objectName    the object name relating to the method request.
     * @return a suitable MethodInvocationHandler
     */
    MethodInvocationHandler getMethodInvocationHandler(MethodRequest methodRequest, String objectName);

    /**
     * Get the MethodInvocationHandler for a published lookup name. Used in special adapters.
     *
     * @param publishedName the published lookup name.
     * @return a suitable MethodInvocationHandler
     */
    MethodInvocationHandler getMethodInvocationHandler(String publishedName);
}
