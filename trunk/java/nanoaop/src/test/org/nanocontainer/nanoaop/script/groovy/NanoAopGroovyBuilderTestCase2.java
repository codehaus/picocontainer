/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.script.groovy;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.nanoaop.AspectablePicoContainer;
import org.nanocontainer.nanoaop.Dao;
import org.nanocontainer.nanoaop.Identifiable;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * @author Stephen Molitor
 */
public class NanoAopGroovyBuilderTestCase2 extends TestCase {

    public void testContainerScopedAdvice() throws PicoCompositionException {
        Reader script = new StringReader(
                ""
                        + "import org.nanocontainer.nanoaop.*\n"
                        + ""
                        + "LoggingInterceptor logger = new LoggingInterceptor(log)\n"
                        + "builder = new org.nanocontainer.nanoaop.script.groovy.NanoAopGroovyBuilder()\n"
                        + "cuts = builder.pointcuts()\n"
                        + "pico = builder.container() {\n"
                        + "    component(key:Dao, class:DaoImpl)\n"
                        + "    aspect(classCut:cuts.instancesOf(Dao), methodCut:cuts.allMethods(), mixinClass:IdentifiableMixin)\n"
                        + "}");

        AspectablePicoContainer pico = (AspectablePicoContainer) buildContainer(new GroovyContainerBuilder(script,
                getClass().getClassLoader()), null);
        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        assertNotNull(dao);

        assertTrue(dao instanceof Identifiable);
    }

    public void testComponentScopedAdvice() throws PicoCompositionException {
        Reader script = new StringReader(
                ""
                        + "import org.nanocontainer.nanoaop.*\n"
                        + ""
                        + "LoggingInterceptor logger = new LoggingInterceptor(log)\n"
                        + "builder = new org.nanocontainer.nanoaop.script.groovy.NanoAopGroovyBuilder()\n"
                        + "cuts = builder.pointcuts()\n"
                        + "pico = builder.container() {\n"
                        + "    component(key:Dao, class:DaoImpl) {\n"
                        + "        aspect(methodCut:cuts.allMethods(), mixinClass:IdentifiableMixin)\n"
                        + "    }\n" 
                        + "}");

        AspectablePicoContainer pico = (AspectablePicoContainer) buildContainer(new GroovyContainerBuilder(script,
                getClass().getClassLoader()), null);
        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        assertNotNull(dao);

        assertTrue(dao instanceof Identifiable);
    }
    
    public void testComponentAdapterFactorySupplied() throws PicoCompositionException {
        Reader script = new StringReader(
                ""
                + "import org.nanocontainer.nanoaop.*\n"
                + "myFactory = new org.nanocontainer.nanoaop.script.groovy.DummyAdapterFactory()\n"
                + "builder = new org.nanocontainer.nanoaop.script.groovy.NanoAopGroovyBuilder()\n"
                + "pico = builder.container(adapterFactory:myFactory) {\n"
                + "    component(key:Dao, class:DaoImpl)\n"
                + "}\n");
        
        AspectablePicoContainer pico = (AspectablePicoContainer) buildContainer(new GroovyContainerBuilder(script,
                getClass().getClassLoader()), null);
        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        assertNotNull(dao);
    }

    protected PicoContainer buildContainer(ScriptedContainerBuilder builder, PicoContainer parentContainer) {
        ObjectReference containerRef = new SimpleReference();
        ObjectReference parentContainerRef = new SimpleReference();

        parentContainerRef.set(parentContainer);
        builder.buildContainer(containerRef, parentContainerRef, "SOME_SCOPE", true);
        return (PicoContainer) containerRef.get();
    }

}