/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.script.groovy;

import org.nanocontainer.aop.Dao;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.picocontainer.PicoContainer;

import java.io.StringReader;

/**
 * @author Stephen Molitor
 * @author Paul n Aslak (pairing)
 */
public class NanoContainerBuilderAopTestCase extends AbstractScriptedContainerBuilderTestCase {


    public void testContainerScopedInterceptor() {
        String script = "" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.aop.*\n" +
                "" +
                "log = new StringBuffer()\n" +
                "logger = new LoggingInterceptor(log)\n" +
                "\n" +
                "cuts = new org.nanocontainer.aop.dynaop.DynaopPointcutsFactory()\n" +
                "aspectsManager = new org.nanocontainer.aop.dynaop.DynaopAspectsManager(cuts)\n" +
                "decorator = new org.nanocontainer.aop.defaults.AopDecorationDelegate(aspectsManager)\n" +
                "builder = new NanoContainerBuilder(decorator)\n" +
                "nano = builder.container() {\n" +
                "    aspect(classCut:cuts.instancesOf(Dao.class), methodCut:cuts.allMethods(), interceptor:logger)\n" +
                "    component(key:Dao, class:DaoImpl)\n" +
                "    component(key:StringBuffer, instance:log)\n" +
                "}\n";

        GroovyContainerBuilder builder = new GroovyContainerBuilder(new StringReader(script), getClass().getClassLoader());

        PicoContainer pico = buildContainer(builder, null, "SOME_SCOPE");

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);

        StringBuffer log = (StringBuffer) pico.getComponentInstance(StringBuffer.class);
        verifyIntercepted(dao, log);
    }

    public void testContainerScopedContainerSuppliedInterceptor() {

        String script = "" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.aop.*\n" +
                "cuts = new org.nanocontainer.aop.dynaop.DynaopPointcutsFactory()\n" +

                "aspectsManager = new org.nanocontainer.aop.dynaop.DynaopAspectsManager(cuts)\n" +
                "decorator = new org.nanocontainer.aop.defaults.AopDecorationDelegate(aspectsManager)\n" +
                "builder = new NanoContainerBuilder(decorator)\n" +
                "nano = builder.container() {\n" +
                "    aspect(classCut:cuts.instancesOf(Dao), methodCut:cuts.allMethods(), interceptorKey:LoggingInterceptor)\n" +
                "    component(key:'log', class:StringBuffer)\n" +
                "    component(LoggingInterceptor)\n" +
                "    component(key:Dao, class:DaoImpl)\n" +
                "}";

        GroovyContainerBuilder builder = new GroovyContainerBuilder(new StringReader(script), getClass().getClassLoader());

        PicoContainer pico = buildContainer(builder, null, "SOME_SCOPE");

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        StringBuffer log = (StringBuffer) pico.getComponentInstance("log");
        verifyIntercepted(dao, log);
    }



    private void verifyIntercepted(Dao dao, StringBuffer log) {
        String before = log.toString();
        String data = dao.loadData();
        assertEquals("data", data);
        assertEquals(before + "startend", log.toString());
    }

}