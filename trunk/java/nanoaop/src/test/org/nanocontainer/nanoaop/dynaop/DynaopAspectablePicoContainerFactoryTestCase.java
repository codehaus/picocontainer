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

import org.nanocontainer.nanoaop.AbstractNanoaopTestCase;
import org.nanocontainer.nanoaop.AnotherInterface;
import org.nanocontainer.nanoaop.AspectablePicoContainer;
import org.nanocontainer.nanoaop.AspectablePicoContainerFactory;
import org.nanocontainer.nanoaop.Dao;
import org.nanocontainer.nanoaop.DaoImpl;
import org.nanocontainer.nanoaop.Identifiable;
import org.nanocontainer.nanoaop.IdentifiableMixin;
import org.nanocontainer.nanoaop.LoggingInterceptor;
import org.nanocontainer.nanoaop.PointcutsFactory;

/**
 * @author Stephen Molitor
 */
public class DynaopAspectablePicoContainerFactoryTestCase extends AbstractNanoaopTestCase {

    private AspectablePicoContainerFactory containerFactory = new DynaopAspectablePicoContainerFactory();
    private AspectablePicoContainer pico = containerFactory.createContainer();
    private PointcutsFactory cuts = pico.getPointcutsFactory();
    
    public void testInterceptor() {
        StringBuffer log = new StringBuffer();
        pico.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), new LoggingInterceptor(log));
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyIntercepted(dao, log);
    }

    public void testContainerSuppliedInterceptor() {
        pico.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), LoggingInterceptor.class);

        pico.registerComponentImplementation("log", StringBuffer.class);
        pico.registerComponentImplementation(LoggingInterceptor.class);
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        StringBuffer log = (StringBuffer) pico.getComponentInstance("log");
        verifyIntercepted(dao, log);
    }

    public void testComponentInterceptor() {
        StringBuffer log = new StringBuffer();

        pico.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(), new LoggingInterceptor(log));
        pico.registerComponentImplementation("intercepted", DaoImpl.class);
        pico.registerComponentImplementation("notIntercepted", DaoImpl.class);

        Dao intercepted = (Dao) pico.getComponentInstance("intercepted");
        Dao notIntercepted = (Dao) pico.getComponentInstance("notIntercepted");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testContainerSuppliedComponentInterceptor() {
        pico.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(), LoggingInterceptor.class);

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
        pico.registerMixin(cuts.instancesOf(Dao.class), IdentifiableMixin.class);
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyMixin(dao);
        assertTrue(dao instanceof AnotherInterface);
    }

    public void testContainerSuppliedMixin() {
        pico.registerMixin(cuts.instancesOf(Dao.class), new Class[] { Identifiable.class }, IdentifiableMixin.class);

        pico.registerComponentImplementation(IdentifiableMixin.class);
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyMixin(dao);
    }

    public void testComponentMixin() {
        pico.registerComponentImplementation("hasMixin", DaoImpl.class);
        pico.registerComponentImplementation("noMixin", DaoImpl.class);

        pico.registerMixin(cuts.component("hasMixin"), IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponentInstance("hasMixin");
        Dao noMixin = (Dao) pico.getComponentInstance("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);
        assertTrue(hasMixin instanceof AnotherInterface);
    }

    public void testContainerSuppliedComponentMixin() {
        pico.registerMixin(cuts.component("hasMixin"), new Class[] { Identifiable.class }, IdentifiableMixin.class);

        pico.registerComponentImplementation(IdentifiableMixin.class);
        pico.registerComponentImplementation("hasMixin", DaoImpl.class);
        pico.registerComponentImplementation("noMixin", DaoImpl.class);

        Dao hasMixin = (Dao) pico.getComponentInstance("hasMixin");
        Dao noMixin = (Dao) pico.getComponentInstance("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);
    }

    public void testMixinExplicitInterfaces() {
        pico.registerMixin(cuts.instancesOf(Dao.class), new Class[] { Identifiable.class }, IdentifiableMixin.class);
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyMixin(dao);
        assertFalse(dao instanceof AnotherInterface);
    }

    public void testComponentMixinExplicitInterfaces() {
        pico.registerComponentImplementation("hasMixin", DaoImpl.class);
        pico.registerComponentImplementation("noMixin", DaoImpl.class);

        pico.registerMixin(cuts.component("hasMixin"), new Class[] { Identifiable.class }, IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponentInstance("hasMixin");
        Dao noMixin = (Dao) pico.getComponentInstance("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);

        assertFalse(hasMixin instanceof AnotherInterface);
    }

}