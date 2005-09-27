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

import sun.tools.javac.Main;

/**
 * The default compiler. This is the javac present in JDK 1.1.x and
 * JDK 1.2.
 * <p/>
 * At some point we need to make sure there is a class like this for
 * JDK 1.3, and other javac-like animals that people want to use.
 *
 * @author Anil K. Vijendran
 */
public class SunJavaCompiler extends JavaCompiler {

    /**
     * Method doCompile
     *
     * @param source
     * @return
     */
    public boolean doCompile(String source) {
        Main compiler = new Main(m_out, "jsp->javac");
        String[] args;
        args = new String[]{"-classpath", m_classpath, "-d", m_outdir, source};
        return compiler.compile(args);
    }
}
