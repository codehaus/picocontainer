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
package org.nanocontainer.remoting.tools.generator;

import org.nanocontainer.remoting.server.ProxyGenerationException;
import org.nanocontainer.remoting.server.ProxyGenerator;
import org.nanocontainer.remoting.server.PublicationDescriptionItem;

import java.lang.reflect.Method;


/**
 * Abstract parent for Proxy Generators
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $*
 */

public abstract class AbstractProxyGenerator implements ProxyGenerator {

    private String m_classGenDir;
    private String m_genName;
    private String m_srcGenDir;
    private String m_classpath;
    private boolean m_verbose;
    private PublicationDescriptionItem[] m_additionalFacades;
    private PublicationDescriptionItem[] m_callbackFacades;
    private PublicationDescriptionItem[] m_interfacesToExpose;

    /**
     * Get the directory name of the class generation directory.
     *
     * @return the dir name.
     */
    public String getClassGenDir() {
        return m_classGenDir;
    }

    /**
     * Get the generation name of the class
     *
     * @return the name.
     */

    public String getGenName() {
        return m_genName;
    }

    /**
     * Get the source directory name
     *
     * @return the dir name.
     */

    public String getSrcGenDir() {
        return m_srcGenDir;
    }


    /**
     * Get the m_classpath used during creation
     *
     * @return m_classpath
     */

    public String getClasspath() {
        return m_classpath;
    }

    /**
     * Is verbose debugging level
     *
     * @return verbose or not
     */
    public boolean isVerbose() {
        return m_verbose;
    }

    /**
     * Get the additional facades
     *
     * @return the additional facades
     */
    public PublicationDescriptionItem[] getAdditionalFacades() {
        return m_additionalFacades;
    }

    public PublicationDescriptionItem[] getCallbackFacades() {
        return m_callbackFacades;
    }

    /**
     * Get the interfaces to expose.
     *
     * @return the interfaces
     */
    public PublicationDescriptionItem[] getInterfacesToExpose() {
        return m_interfacesToExpose;
    }

    /**
     * Set the verbose logging level for class generation.
     *
     * @param trueFalse set the verbose level
     */
    public void verbose(boolean trueFalse) {
        m_verbose = trueFalse;
    }


    /**
     * Set the interfaces to expose.
     *
     * @param interfacesToExpose the interfaces.
     */
    public void setInterfacesToExpose(PublicationDescriptionItem[] interfacesToExpose) {
        m_interfacesToExpose = interfacesToExpose;
    }


    /**
     * Set the additional facades
     *
     * @param additionalFacades the facades.
     */
    public void setAdditionalFacades(PublicationDescriptionItem[] additionalFacades) {
        m_additionalFacades = additionalFacades;
    }

    public void setCallbackFacades(PublicationDescriptionItem[] callbackFacades) {
        this.m_callbackFacades = callbackFacades;
    }

    /**
     * Set the clas generation dorectory
     *
     * @param classGenDir the dir.
     */
    public void setClassGenDir(String classGenDir) {
        m_classGenDir = classGenDir;
    }

    /**
     * Set the generation name
     *
     * @param genName the name
     */
    public void setGenName(String genName) {
        this.m_genName = genName;
    }

    /**
     * Set the source generation directory.
     *
     * @param srcGenDir the dir name.
     */
    public void setSrcGenDir(String srcGenDir) {
        m_srcGenDir = srcGenDir;
    }

    /**
     * Set the m_classpath to generate with
     *
     * @param classpath the m_classpath.
     */
    public void setClasspath(String classpath) {
        m_classpath = classpath;
    }

    /**
     * Is the param one of the additional facades?
     *
     * @param clazz the class
     * @return true if the class is one of the designated facades
     */
    protected boolean isAdditionalFacade(Class clazz) {

        if (m_additionalFacades == null) {
            return false;
        }

        for (int p = 0; p < m_additionalFacades.length; p++) {
            if (clazz.getName().equals(m_additionalFacades[p].getFacadeClass().getName())) {
                return true;
            } else if (clazz.getName().equals("[L" + m_additionalFacades[p].getFacadeClass().getName() + ";")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates the Java-Source type of the string.
     *
     * @param rClass the class to get a type for
     * @return the class type
     */
    protected String getClassType(Class rClass) {

        String cn = rClass.getName();

        if (rClass.getName().startsWith("[L")) {
            return cn.substring(2, cn.length() - 1) + "[]";
        } else {
            return cn;
        }
    }

    /**
     * Gernerate the source for the proxy class.
     *
     * @param classLoader the classloader to use while making the source.
     * @throws ProxyGenerationException if an error during generation
     */
    public void generateSrc(ClassLoader classLoader) throws ProxyGenerationException {
        // default impl
    }

    /**
     * Generate the deferred classes.
     */
    public void generateDeferredClasses() {
        // default impl
    }

    /**
     * @param publicationDescriptionItemses
     * @return
     */
    protected boolean needsAsyncBehavior(PublicationDescriptionItem[] publicationDescriptionItemses) {
        for (int i = 0; i < publicationDescriptionItemses.length; i++) {
            PublicationDescriptionItem publicationDescriptionItem = publicationDescriptionItemses[i];
            if (publicationDescriptionItem.hasAsyncBehavior()) {
                return true;
            }
        }
        return false;
    }

    protected Method[] getGeneratableMethods(Class clazz) {

        Method[] methods = null;
        try {
            Method ts = Object.class.getMethod("toString", new Class[0]);
            Method hc = Object.class.getMethod("hashCode", new Class[0]);
            Method[] interfaceMethods = clazz.getMethods();
            methods = new Method[interfaceMethods.length + 2];
            System.arraycopy(interfaceMethods, 0, methods, 0, interfaceMethods.length);
            methods[interfaceMethods.length] = ts;
            methods[interfaceMethods.length + 1] = hc;
        } catch (NoSuchMethodException e) {
            // never!
        }
        return methods;

    }


}
