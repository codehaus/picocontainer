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

import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.Reader;
import java.io.StringReader;

import groovy.lang.Binding;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public class GroovyContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "builder = new org.nanocontainer.script.groovy.NanoContainerBuilder()\n" +
                "pico = builder.container(parent:parent) { \n" +
                "  component(StringBuffer)\n" +
                "}");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        assertEquals(StringBuffer.class, pico.getComponentInstance(StringBuffer.class).getClass());
    }

	public void testAdditionalBindingViaSubClassing() {
		// NOTE script does NOT define a "builder"
		Reader script = new StringReader("" +
                "pico = builder.container(parent:parent) { \n" +
                "  component(StringBuffer)\n" +
                "}");   		

        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new ChildGroovyContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");

		assertNotSame(parent, pico.getParent());
        assertEquals(StringBuffer.class, pico.getComponentInstance(StringBuffer.class).getClass());
	}

	/**
	 * Child GroovyContainerBuilder which adds additional bindings
	 */
	private class ChildGroovyContainerBuilder extends GroovyContainerBuilder {
		public ChildGroovyContainerBuilder(final Reader script, ClassLoader classLoader) {
			super(script, classLoader);
		}

		protected void handleBinding(Binding binding) {
			super.handleBinding(binding);

			binding.setVariable("builder", new NanoContainerBuilder());
		}

	}

}