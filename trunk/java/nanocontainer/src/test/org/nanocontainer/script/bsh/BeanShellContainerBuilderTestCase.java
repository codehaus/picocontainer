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
package org.nanocontainer.script.bsh;

import java.io.Reader;
import java.io.StringReader;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanShellContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "pico = new org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer(parent);\n" +
                "pico.registerComponentInstance(\"hello\", \"BeanShell\");\n");
        PicoContainer parent = new DefaultPicoContainer();
        SoftCompositionPicoContainer pico = buildContainer(new BeanShellContainerBuilder(script, getClass().getClassLoader()), parent);
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        assertEquals("BeanShell", pico.getComponentInstance("hello"));
    }

}
