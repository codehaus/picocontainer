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


/**
 * A ClassRetriever is a thing that allows the serverside NanoContainer Remoting deployer to choose
 * how class defs for proxies are retrieved. They may not want them in the normal
 * m_classpath.
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public interface ClassRetriever {

    /**
     * Get a Bean's bytes (from its classdefinition)
     *
     * @param publishedName the name the bean is published as.
     * @return a byte array for the bean's class representation.
     * @throws org.nanocontainer.remoting.server.ClassRetrievalException
     *          if the classdef cannot be found.
     */
    byte[] getProxyClassBytes(String publishedName) throws ClassRetrievalException;

}
