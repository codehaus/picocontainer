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
 * Class ProxyGenerator
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public interface ProxyGenerator {

    /**
     * Set the interfaces to expose.
     *
     * @param interfacesToExpose the interfaces to expose
     */
    void setInterfacesToExpose(PublicationDescriptionItem[] interfacesToExpose);

    /**
     * Set the additional facades
     *
     * @param additionalFacades additional facades/interfaces to expose
     */
    void setAdditionalFacades(PublicationDescriptionItem[] additionalFacades);

    /**
     * Set the source generation directory
     *
     * @param srcGenDir the source generation directory (if appl).
     */
    void setSrcGenDir(String srcGenDir);

    /**
     * Set the m_classpath
     *
     * @param classpath the m_classpath for compilation (if appl).
     */
    void setClasspath(String classpath);

    /**
     * Set the class generation directory.
     *
     * @param classGenDir the class generation directory (if appl).
     */
    void setClassGenDir(String classGenDir);

    /**
     * Set the generation name.
     *
     * @param genName the generation name
     */
    void setGenName(String genName);

    /**
     * Generate source
     *
     * @param classLoader the classloader active during generation of source.
     * @throws org.nanocontainer.remoting.server.ProxyGenerationException
     *          for when source generation has failed.
     */
    void generateSrc(ClassLoader classLoader) throws ProxyGenerationException;

    /**
     * Generate class
     *
     * @param classLoader the classloader active during compilation/generation.
     */
    void generateClass(ClassLoader classLoader);

    /**
     * Generate deferred classes
     */
    void generateDeferredClasses();

    /**
     * verbose mode.
     *
     * @param trueFalse report on generation process
     */
    void verbose(boolean trueFalse);

    /**
     * Set the callback facades
     *
     * @param callbackFacades callback facades/interfaces to handle
     */
    void setCallbackFacades(PublicationDescriptionItem[] callbackFacades);
}
