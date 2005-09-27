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
package org.nanocontainer.remoting.tools.generator.ant;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.nanocontainer.remoting.server.ProxyGenerationException;
import org.nanocontainer.remoting.server.ProxyGenerator;
import org.nanocontainer.remoting.server.PublicationDescriptionItem;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class ProxyGenerationTask
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class ProxyGenerationTask extends Task {

    protected String[] m_interfacesToExpose;
    protected String[] m_additionalFacades;
    protected String[] m_callbackFacades;
    protected File m_srcGenDir;
    protected File m_classGenDir;
    protected String m_genName;
    protected Path m_classpath;
    protected String m_verbose = "false";
    private String m_generatorClass = "org.nanocontainer.remoting.tools.generator.ProxyGeneratorImpl";

    /**
     * Constructor ProxyGenerationTask
     */
    public ProxyGenerationTask() {
    }

    /**
     * Method setInterfaces
     *
     * @param interfacesToExpose
     */
    public void setInterfaces(String interfacesToExpose) {

        StringTokenizer st = new StringTokenizer(interfacesToExpose, ",");
        Vector strings = new Vector();

        while (st.hasMoreTokens()) {
            strings.add(st.nextToken().trim());
        }

        m_interfacesToExpose = new String[strings.size()];

        strings.copyInto(m_interfacesToExpose);
    }

    /**
     * Method setAdditionalfacades
     *
     * @param additionalfacades
     */
    public void setAdditionalfacades(String additionalfacades) {

        StringTokenizer st = new StringTokenizer(additionalfacades, ",");
        Vector strings = new Vector();

        while (st.hasMoreTokens()) {
            strings.add(st.nextToken().trim());
        }

        m_additionalFacades = new String[strings.size()];

        strings.copyInto(m_additionalFacades);
    }

    /**
     * Method setAdditionalfacades
     *
     * @param callbackfacades
     */
    public void setCallbackfacades(String callbackfacades) {

        StringTokenizer st = new StringTokenizer(callbackfacades, ",");
        Vector strings = new Vector();

        while (st.hasMoreTokens()) {
            strings.add(st.nextToken().trim());
        }

        m_callbackFacades = new String[strings.size()];

        strings.copyInto(m_callbackFacades);
    }


    /**
     * Method setSrcgendir
     *
     * @param srcGenDir
     */
    public void setSrcgendir(File srcGenDir) {
        m_srcGenDir = srcGenDir;
    }

    /**
     * Method setClassgendir
     *
     * @param classGenDir
     */
    public void setClassgendir(File classGenDir) {
        m_classGenDir = classGenDir;
    }

    /**
     * Method setGenname
     *
     * @param genName
     */
    public void setGenname(String genName) {
        this.m_genName = genName;
    }

    /**
     * Method setM_classpath
     *
     * @param classpath
     */
    public void setClasspath(Path classpath) {

        if (m_classpath == null) {
            m_classpath = classpath;
        } else {
            m_classpath.append(classpath);
        }
    }

    /**
     * Method createClasspath
     *
     * @return
     */
    public Path createClasspath() {

        if (m_classpath == null) {
            m_classpath = new Path(project);
        }

        return m_classpath.createPath();
    }

    /**
     * Method setClasspathRef
     *
     * @param r
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    /**
     * Method setVerbose
     *
     * @param verbose
     */
    public void setVerbose(String verbose) {
        m_verbose = verbose;
    }

    /**
     * Sets the GeneratorClass
     *
     * @param generatorClass The Generator Class to set.
     */
    public void setGeneratorClass(String generatorClass) {
        this.m_generatorClass = generatorClass;
    }

    /**
     * Method execute
     *
     * @throws BuildException
     */
    public void execute() throws BuildException {

        if (m_interfacesToExpose == null) {
            throw new BuildException("Specify at least one interface to expose");
        }

        if (m_srcGenDir == null) {
            throw new BuildException("Specify the directory to generate Java source in");
        }

        if (m_classGenDir == null) {
            throw new BuildException("Specify the directory to generate Java classes in");
        }

        if (m_genName == null) {
            throw new BuildException("Specify the name to use for lookup");
        }

        ProxyGenerator proxyGenerator;

        try {
            proxyGenerator = (ProxyGenerator) Class.forName(m_generatorClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException("PrimaryGenerator Impl jar not in m_classpath");
        }

        try {
            proxyGenerator.setSrcGenDir(m_srcGenDir.getAbsolutePath());
            proxyGenerator.setClassGenDir(m_classGenDir.getAbsolutePath());
            proxyGenerator.setGenName(m_genName);
            proxyGenerator.verbose(Boolean.valueOf(m_verbose).booleanValue());
            proxyGenerator.setClasspath(m_classpath.concatSystemClasspath("ignore").toString());

            PublicationDescriptionItem[] interfacesToExpose = new PublicationDescriptionItem[m_interfacesToExpose.length];
            ClassLoader classLoader = new AntClassLoader(getProject(), m_classpath);

            for (int i = 0; i < m_interfacesToExpose.length; i++) {
                String cn = m_interfacesToExpose[i];
                interfacesToExpose[i] = new PublicationDescriptionItem(classLoader.loadClass(cn));
            }

            proxyGenerator.setInterfacesToExpose(interfacesToExpose);

            if (m_additionalFacades != null) {
                PublicationDescriptionItem[] additionalFacades = new PublicationDescriptionItem[m_additionalFacades.length];

                for (int i = 0; i < m_additionalFacades.length; i++) {
                    String cn = m_additionalFacades[i];

                    additionalFacades[i] = new PublicationDescriptionItem(classLoader.loadClass(cn));
                }

                proxyGenerator.setAdditionalFacades(additionalFacades);
            }

            if (m_callbackFacades != null) {
                PublicationDescriptionItem[] callbackFacades = new PublicationDescriptionItem[m_callbackFacades.length];

                for (int i = 0; i < m_callbackFacades.length; i++) {
                    String cn = m_callbackFacades[i];

                    callbackFacades[i] = new PublicationDescriptionItem(classLoader.loadClass(cn));
                }

                proxyGenerator.setCallbackFacades(callbackFacades);
            }


            ClassLoader classLoader2 = null;

            if (m_classpath != null) {
                classLoader2 = new AntClassLoader(project, m_classpath);
            } else {
                classLoader2 = this.getClass().getClassLoader();
            }

            proxyGenerator.generateSrc(classLoader2);
            proxyGenerator.generateClass(classLoader2);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();

            throw new BuildException("Class not found : " + cnfe.getMessage());
        } catch (ProxyGenerationException sge) {
            throw new BuildException("Proxy Gerneation error : " + sge.getMessage());
        }
    }


}
