/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.dynaop;

import junit.framework.TestCase;

import org.nanocontainer.nanoaop.AnotherInterface;
import org.nanocontainer.nanoaop.AspectsManager;
import org.nanocontainer.nanoaop.Dao;
import org.nanocontainer.nanoaop.DaoImpl;
import org.nanocontainer.nanoaop.Identifiable;
import org.nanocontainer.nanoaop.IdentifiableMixin;
import org.nanocontainer.nanoaop.LoggingInterceptor;
import org.nanocontainer.nanoaop.PointcutsFactory;
import org.nanocontainer.nanoaop.defaults.AspectsComponentAdapterFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Stephen Molitor
 */
public class DynaopAspectsManagerTestCase extends TestCase {

    private AspectsManager aspects = new DynaopAspectsManager();
    private ComponentAdapterFactory caFactory = new AspectsComponentAdapterFactory(aspects);
    private MutablePicoContainer pico = new DefaultPicoContainer(caFactory);
    private PointcutsFactory cuts = aspects.getPointcutsFactory();

    public void testInterceptor() {
        StringBuffer log = new StringBuffer();
        aspects.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), new LoggingInterceptor(log));
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyIntercepted(dao, log);
    }

    public void testContainerSuppliedInterceptor() {
        aspects.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(),
                cuts.component(LoggingInterceptor.class));

        pico.registerComponentImplementation("log", StringBuffer.class);
        pico.registerComponentImplementation(LoggingInterceptor.class);
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        StringBuffer log = (StringBuffer) pico.getComponentInstance("log");
        verifyIntercepted(dao, log);
    }

    public void testComponentInterceptor() {
        StringBuffer log = new StringBuffer();

        aspects.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(), new LoggingInterceptor(log));
        pico.registerComponentImplementation("intercepted", DaoImpl.class);
        pico.registerComponentImplementation("notIntercepted", DaoImpl.class);

        Dao intercepted = (Dao) pico.getComponentInstance("intercepted");
        Dao notIntercepted = (Dao) pico.getComponentInstance("notIntercepted");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testContainerSuppliedComponentInterceptor() {
        aspects.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(),
                cuts.component(LoggingInterceptor.class));

        pico.registerComponentImplementation("log", StringBuffer.class);
        pico.registerComponentImplementation(LoggingInterceptor.class);
        pico.registerComponentImplementation("intercepted", DaoImpl.class);
        pico.registerComponentImplementation("notIntercepted", DaoImpl.class);

        StringBuffer log = (StringBuffer) pico.getComponentInstance("log");
        Dao intercepted = (Dao) pico.getComponentInstance("intercepted");
        Dao notIntercepted = (Dao) pico.getComponentInstance("notIntercepted");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testMixin() {
        aspects.registerMixin(cuts.instancesOf(Dao.class), IdentifiableMixin.class);
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyMixin(dao);
        assertTrue(dao instanceof AnotherInterface);
    }

    public void testContainerSuppliedMixin() {
        aspects.registerMixin(cuts.instancesOf(Dao.class), new Class[] { Identifiable.class },
                cuts.component(IdentifiableMixin.class));

        pico.registerComponentImplementation(IdentifiableMixin.class);
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyMixin(dao);
    }

    public void testComponentMixin() {
        pico.registerComponentImplementation("hasMixin", DaoImpl.class);
        pico.registerComponentImplementation("noMixin", DaoImpl.class);

        aspects.registerMixin(cuts.component("hasMixin"), IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponentInstance("hasMixin");
        Dao noMixin = (Dao) pico.getComponentInstance("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);
        assertTrue(hasMixin instanceof AnotherInterface);
    }

    public void testContainerSuppliedComponentMixin() {
        aspects.registerMixin(cuts.component("hasMixin"), new Class[] { Identifiable.class },
                cuts.component(IdentifiableMixin.class));

        pico.registerComponentImplementation(IdentifiableMixin.class);
        pico.registerComponentImplementation("hasMixin", DaoImpl.class);
        pico.registerComponentImplementation("noMixin", DaoImpl.class);

        Dao hasMixin = (Dao) pico.getComponentInstance("hasMixin");
        Dao noMixin = (Dao) pico.getComponentInstance("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);
    }

    public void testMixinExplicitInterfaces() {
        aspects.registerMixin(cuts.instancesOf(Dao.class), new Class[] { Identifiable.class }, IdentifiableMixin.class);
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyMixin(dao);
        assertFalse(dao instanceof AnotherInterface);
    }

    public void testComponentMixinExplicitInterfaces() {
        pico.registerComponentImplementation("hasMixin", DaoImpl.class);
        pico.registerComponentImplementation("noMixin", DaoImpl.class);

        aspects.registerMixin(cuts.component("hasMixin"), new Class[] { Identifiable.class }, IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponentInstance("hasMixin");
        Dao noMixin = (Dao) pico.getComponentInstance("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);

        assertFalse(hasMixin instanceof AnotherInterface);
    }

    private void verifyIntercepted(Dao dao, StringBuffer log) {
        String before = log.toString();
        String data = dao.loadData();
        assertEquals("data", data);
        assertEquals(before + "startend", log.toString());
    }

    private void verifyNotIntercepted(Dao dao, StringBuffer log) {
        String before = log.toString();
        String data = dao.loadData();
        assertEquals("data", data);
        assertEquals(before, log.toString());
    }

    private void verifyMixin(Object component) {
        assertTrue(component instanceof Identifiable);
        Identifiable identifiable = (Identifiable) component;
        identifiable.setId("id");
        assertEquals("id", identifiable.getId());
    }

    private void verifyNoMixin(Object component) {
        assertFalse(component instanceof Identifiable);
    }

}