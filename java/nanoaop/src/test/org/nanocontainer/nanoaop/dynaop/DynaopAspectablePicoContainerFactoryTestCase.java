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
import org.nanocontainer.nanoaop.IdGenerator;
import org.nanocontainer.nanoaop.IdGeneratorImpl;
import org.nanocontainer.nanoaop.Identifiable;
import org.nanocontainer.nanoaop.IdentifiableMixin;
import org.nanocontainer.nanoaop.LoggingInterceptor;
import org.nanocontainer.nanoaop.OrderEntity;
import org.nanocontainer.nanoaop.OrderEntityImpl;
import org.nanocontainer.nanoaop.PointcutsFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

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
        pico.registerComponentImplementation(IdGenerator.class, IdGeneratorImpl.class);
        pico.registerComponentImplementation("order1", OrderEntityImpl.class);
        pico.registerComponentImplementation("order2", OrderEntityImpl.class);
        pico.registerMixin(cuts.instancesOf(OrderEntity.class), new Class[] { Identifiable.class },
                IdentifiableMixin.class);

        Identifiable i1 = (Identifiable) pico.getComponentInstance("order1");
        Identifiable i2 = (Identifiable) pico.getComponentInstance("order2");

        assertEquals(new Integer(1), i1.getId());
        assertEquals(new Integer(2), i2.getId());

        i1.setId(new Integer(3));
        assertEquals(new Integer(3), i1.getId());
        assertEquals(new Integer(2), i2.getId());
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
        pico.registerComponentImplementation(IdGenerator.class, IdGeneratorImpl.class);
        pico.registerMixin(cuts.componentName("hasMixin*"), new Class[] { Identifiable.class }, IdentifiableMixin.class);
        pico.registerComponentImplementation("hasMixin1", OrderEntityImpl.class);
        pico.registerComponentImplementation("hasMixin2", OrderEntityImpl.class);
        pico.registerComponentImplementation("noMixin", OrderEntityImpl.class);

        OrderEntity hasMixin1 = (OrderEntity) pico.getComponentInstance("hasMixin1");
        OrderEntity hasMixin2 = (OrderEntity) pico.getComponentInstance("hasMixin2");
        OrderEntity noMixin = (OrderEntity) pico.getComponentInstance("noMixin");

        assertTrue(hasMixin1 instanceof Identifiable);
        assertTrue(hasMixin2 instanceof Identifiable);
        assertFalse(noMixin instanceof Identifiable);
        
        assertEquals(new Integer(1), ((Identifiable) hasMixin1).getId());
        assertEquals(new Integer(2), ((Identifiable) hasMixin2).getId());
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

    public void testCreateWithParentContainer() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.registerComponentInstance("key", "value");
        AspectablePicoContainerFactory containerFactory = new DynaopAspectablePicoContainerFactory();
        PicoContainer child = containerFactory.createContainer(parent);
        assertEquals("value", child.getComponentInstance("key"));
    }

}