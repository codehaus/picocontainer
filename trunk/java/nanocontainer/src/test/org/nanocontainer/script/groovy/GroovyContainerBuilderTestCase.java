/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script.groovy;

import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;

import java.io.Reader;
import java.io.StringReader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class GroovyContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testContainerCanBeBuiltWithParent() {
        // * imports are not supported by groovy yet, so the GroovyContainerBuilder won't either.
        Reader script = new StringReader("" +
                "// No need to extend anything, but we must have a method with this signature\n" +
                "class MyContainerBuilder {\n" +
                "    buildContainer(parent, assemblyScope) {\n" +
                "        pico = new org.picocontainer.defaults.DefaultPicoContainer(parent)\n" +
                "        pico.registerComponentInstance(\"hello\", \"Groovy\")\n" +
                "        return pico\n" +
                "    }\n" +
                "}");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), parent);
        assertSame(parent, pico.getParent());
        assertEquals("Groovy", pico.getComponentInstance("hello"));
    }

}
