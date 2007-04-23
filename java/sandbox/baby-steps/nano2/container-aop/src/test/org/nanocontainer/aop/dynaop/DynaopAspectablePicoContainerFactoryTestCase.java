/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.dynaop;

import org.nanocontainer.aop.AbstractAopTestCase;
import org.nanocontainer.aop.AspectablePicoContainer;
import org.nanocontainer.aop.AspectablePicoContainerFactory;
import org.nanocontainer.aop.LoggingInterceptor;
import org.nanocontainer.aop.PointcutsFactory;
import org.nanocontainer.testmodel.AnotherInterface;
import org.nanocontainer.testmodel.Dao;
import org.nanocontainer.testmodel.DaoImpl;
import org.nanocontainer.testmodel.IdGenerator;
import org.nanocontainer.testmodel.IdGeneratorImpl;
import org.nanocontainer.testmodel.Identifiable;
import org.nanocontainer.testmodel.IdentifiableMixin;
import org.nanocontainer.testmodel.OrderEntity;
import org.nanocontainer.testmodel.OrderEntityImpl;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.SimpleTouchable;

/**
 * @author Stephen Molitor
 */
public class DynaopAspectablePicoContainerFactoryTestCase extends AbstractAopTestCase {

    private AspectablePicoContainerFactory containerFactory = new DynaopAspectablePicoContainerFactory();
    private AspectablePicoContainer pico = containerFactory.createContainer();
    private PointcutsFactory cuts = pico.getPointcutsFactory();

    public void testInterceptor() {
        StringBuffer log = new StringBuffer();
        pico.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), new LoggingInterceptor(log));
        pico.registerComponent(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponent(Dao.class);
        verifyIntercepted(dao, log);
    }

    public void testContainerSuppliedInterceptor() {
        pico.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), LoggingInterceptor.class);

        pico.registerComponent("log", StringBuffer.class);
        pico.registerComponent(LoggingInterceptor.class);
        pico.registerComponent(Dao.class, DaoImpl.class);

        Dao dao = (Dao) pico.getComponent(Dao.class);
        StringBuffer log = (StringBuffer) pico.getComponent("log");
        verifyIntercepted(dao, log);
    }

    public void testComponentInterceptor() {
        StringBuffer log = new StringBuffer();

        pico.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(), new LoggingInterceptor(log));
        pico.registerComponent("intercepted", DaoImpl.class);
        pico.registerComponent("notIntercepted", DaoImpl.class);

        Dao intercepted = (Dao) pico.getComponent("intercepted");
        Dao notIntercepted = (Dao) pico.getComponent("notIntercepted");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testContainerSuppliedComponentInterceptor() {
        pico.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(), LoggingInterceptor.class);

        pico.registerComponent("log", StringBuffer.class);
        pico.registerComponent(LoggingInterceptor.class);
        pico.registerComponent("intercepted", DaoImpl.class);
        pico.registerComponent("notIntercepted", DaoImpl.class);

        StringBuffer log = (StringBuffer) pico.getComponent("log");
        Dao intercepted = (Dao) pico.getComponent("intercepted");
        Dao notIntercepted = (Dao) pico.getComponent("notIntercepted");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testMixin() {
        pico.registerMixin(cuts.instancesOf(Dao.class), IdentifiableMixin.class);
        pico.registerComponent(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponent(Dao.class);
        verifyMixin(dao);
        assertTrue(dao instanceof AnotherInterface);
    }

    public void testContainerSuppliedMixin() {
        pico.registerComponent(IdGenerator.class, IdGeneratorImpl.class);
        pico.registerComponent("order1", OrderEntityImpl.class);
        pico.registerComponent("order2", OrderEntityImpl.class);
        pico.registerMixin(cuts.instancesOf(OrderEntity.class), new Class[]{Identifiable.class},
                IdentifiableMixin.class);

        Identifiable i1 = (Identifiable) pico.getComponent("order1");
        Identifiable i2 = (Identifiable) pico.getComponent("order2");

        assertEquals(new Integer(1), i1.getId());
        assertEquals(new Integer(2), i2.getId());

        i1.setId(new Integer(3));
        assertEquals(new Integer(3), i1.getId());
        assertEquals(new Integer(2), i2.getId());
    }

    public void testComponentMixin() {
        pico.registerComponent("hasMixin", DaoImpl.class);
        pico.registerComponent("noMixin", DaoImpl.class);

        pico.registerMixin(cuts.component("hasMixin"), IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponent("hasMixin");
        Dao noMixin = (Dao) pico.getComponent("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);
        assertTrue(hasMixin instanceof AnotherInterface);
    }

    public void testContainerSuppliedComponentMixin() {
        pico.registerComponent(IdGenerator.class, IdGeneratorImpl.class);
        pico.registerMixin(cuts.componentName("hasMixin*"), new Class[]{Identifiable.class}, IdentifiableMixin.class);
        pico.registerComponent("hasMixin1", OrderEntityImpl.class);
        pico.registerComponent("hasMixin2", OrderEntityImpl.class);
        pico.registerComponent("noMixin", OrderEntityImpl.class);

        OrderEntity hasMixin1 = (OrderEntity) pico.getComponent("hasMixin1");
        OrderEntity hasMixin2 = (OrderEntity) pico.getComponent("hasMixin2");
        OrderEntity noMixin = (OrderEntity) pico.getComponent("noMixin");

        assertTrue(hasMixin1 instanceof Identifiable);
        assertTrue(hasMixin2 instanceof Identifiable);
        assertFalse(noMixin instanceof Identifiable);

        assertEquals(new Integer(1), ((Identifiable) hasMixin1).getId());
        assertEquals(new Integer(2), ((Identifiable) hasMixin2).getId());
    }

    public void testMixinExplicitInterfaces() {
        pico.registerMixin(cuts.instancesOf(Dao.class), new Class[]{Identifiable.class}, IdentifiableMixin.class);
        pico.registerComponent(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponent(Dao.class);
        verifyMixin(dao);
        assertFalse(dao instanceof AnotherInterface);
    }

    public void testComponentMixinExplicitInterfaces() {
        pico.registerComponent("hasMixin", DaoImpl.class);
        pico.registerComponent("noMixin", DaoImpl.class);

        pico.registerMixin(cuts.component("hasMixin"), new Class[]{Identifiable.class}, IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponent("hasMixin");
        Dao noMixin = (Dao) pico.getComponent("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);

        assertFalse(hasMixin instanceof AnotherInterface);
    }

    public void testCreateWithParentContainer() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.registerComponent("key", "value");
        AspectablePicoContainerFactory containerFactory = new DynaopAspectablePicoContainerFactory();
        PicoContainer child = containerFactory.createContainer(parent);
        assertEquals("value", child.getComponent("key"));
    }
    
    public void testMakeChildContainer(){
        AspectablePicoContainerFactory aspectableContainerFactory = new DynaopAspectablePicoContainerFactory();
        AspectablePicoContainer parent = aspectableContainerFactory.createContainer();
        parent.registerComponent("t1", SimpleTouchable.class);
        AspectablePicoContainer child = aspectableContainerFactory.makeChildContainer(parent);
        Object t1 = child.getParent().getComponent("t1");
        assertNotNull(t1);
        assertTrue(t1 instanceof SimpleTouchable);        
    }

    public void testInterfacesWithClassPointcut() {
        pico.registerComponent(Dao.class, DaoImpl.class);
        pico.registerMixin(cuts.instancesOf(Dao.class), IdentifiableMixin.class);
        pico.registerInterfaces(cuts.instancesOf(Dao.class), new Class[]{AnotherInterface.class});
        Dao dao = (Dao) pico.getComponent(Dao.class);
        assertTrue(dao instanceof Identifiable);
        assertTrue(dao instanceof AnotherInterface);
    }

    public void testInterfacesWithClassPointcutNoAdvice() {
        pico.registerComponent(Dao.class, DaoImpl.class);
        pico.registerInterfaces(cuts.instancesOf(Dao.class), new Class[]{AnotherInterface.class});
        Dao dao = (Dao) pico.getComponent(Dao.class);

        // dynaop doesn't add any interfaces if there's no advice applied to the
        // object:
        assertFalse(dao instanceof AnotherInterface);
    }

    public void testInterfacesWithComponentPointcut() {
        pico.registerComponent(Dao.class, DaoImpl.class);
        pico.registerMixin(cuts.component(Dao.class), IdentifiableMixin.class);
        pico.registerInterfaces(cuts.component(Dao.class), new Class[]{AnotherInterface.class});
        Dao dao = (Dao) pico.getComponent(Dao.class);
        assertTrue(dao instanceof Identifiable);
        assertTrue(dao instanceof AnotherInterface);
    }

}