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
package org.nanocontainer.remoting.tools.javacompiler;

import java.io.ByteArrayOutputStream;

/**
 * If you want to plugin your own Java compiler, you probably want to
 * write a class that implements this interface.
 *
 * @author Anil K. Vijendran
 * @author Sam Ruby
 * @author Costin Manolache
 */
public abstract class JavaCompiler {

    static String CPSEP = System.getProperty("path.separator");
    protected String m_classpath;
    protected String m_compilerPath = "jikes";
    protected String m_outdir;
    protected ByteArrayOutputStream m_out;
    protected boolean m_classDebugInfo = false;

    protected JavaCompiler() {
        reset();
    }

    /**
     * Specify where the compiler can be found
     */
    public void setCompilerPath(String m_compilerPath) {

        if (m_compilerPath != null) {
            this.m_compilerPath = m_compilerPath;
        }
    }

    /**
     * Method addClassPath
     *
     * @param path
     */
    public void addClassPath(String path) {

        // XXX use StringBuffer
        m_classpath = m_classpath + CPSEP + path;
    }

    /**
     * Method addDefaultClassPath
     */
    public void addDefaultClassPath() {
        addClassPath(System.getProperty("java.class.path"));
    }

    /**
     * Set the output directory
     */
    public void setOutputDir(String outdir) {
        this.m_outdir = outdir;
    }

    /**
     * Method getCompilerMessage
     *
     * @return
     */
    public String getCompilerMessage() {
        return m_out.toString();
    }

    /**
     * Reset all compilation state, but keep the settings.
     * The compiler can be used again.
     */
    public void reset() {
        m_out = new ByteArrayOutputStream(256);
    }


    /**
     * Execute the compiler
     *
     * @param source - file name of the source to be compiled
     */
    public abstract boolean doCompile(String source);

    /**
     * Method getDefaultCompiler
     *
     * @return
     */
    public static JavaCompiler getDefaultCompiler() {
        return new SunJavaCompiler();
    }

    //-------------------- Class path utils --------------------

}
