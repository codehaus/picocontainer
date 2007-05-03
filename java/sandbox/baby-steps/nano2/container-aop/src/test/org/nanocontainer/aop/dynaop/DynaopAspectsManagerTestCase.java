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
import org.nanocontainer.aop.AspectsManager;
import org.nanocontainer.aop.ClassPointcut;
import org.nanocontainer.aop.ComponentPointcut;
import org.nanocontainer.aop.LoggingInterceptor;
import org.nanocontainer.aop.MethodPointcut;
import org.nanocontainer.aop.PointcutsFactory;
import org.nanocontainer.aop.defaults.AspectsComponentAdapterFactory;
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
import org.picocontainer.componentadapters.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.lang.reflect.Method;

/**
 * @author Stephen Molitor
 */
public class DynaopAspectsManagerTestCase extends AbstractAopTestCase {

    private AspectsManager aspects = new DynaopAspectsManager();
    private ComponentAdapterFactory caFactory = new CachingComponentAdapterFactory(new AspectsComponentAdapterFactory(aspects));
    private MutablePicoContainer pico = new DefaultPicoContainer(caFactory);
    private PointcutsFactory cuts = aspects.getPointcutsFactory();

    public void testInterceptor() {
        StringBuffer log = new StringBuffer();
        aspects.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), new LoggingInterceptor(log));
        pico.component(Dao.class, DaoImpl.class);
        Dao dao = pico.getComponent(Dao.class);
        verifyIntercepted(dao, log);
    }

    public void testContainerSuppliedInterceptor() {
        aspects.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), LoggingInterceptor.class);

        pico.component("log", StringBuffer.class);
        pico.component(LoggingInterceptor.class);
        pico.component(Dao.class, DaoImpl.class);

        Dao dao = pico.getComponent(Dao.class);
        StringBuffer log = (StringBuffer) pico.getComponent("log");
        verifyIntercepted(dao, log);
    }

    public void testComponentInterceptor() {
        StringBuffer log = new StringBuffer();

        aspects.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(), new LoggingInterceptor(log));
        pico.component("intercepted", DaoImpl.class);
        pico.component("notIntercepted", DaoImpl.class);

        Dao intercepted = (Dao) pico.getComponent("intercepted");
        Dao notIntercepted = (Dao) pico.getComponent("notIntercepted");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testContainerSuppliedComponentInterceptor() {
        aspects.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(), LoggingInterceptor.class);

        pico.component("log", StringBuffer.class);
        pico.component(LoggingInterceptor.class);
        pico.component("intercepted", DaoImpl.class);
        pico.component("notIntercepted", DaoImpl.class);

        StringBuffer log = (StringBuffer) pico.getComponent("log");
        Dao intercepted = (Dao) pico.getComponent("intercepted");
        Dao notIntercepted = (Dao) pico.getComponent("notIntercepted");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testMixin() {
        aspects.registerMixin(cuts.instancesOf(Dao.class), IdentifiableMixin.class);
        pico.component(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponent(Dao.class);
        verifyMixin(dao);
        assertTrue(dao instanceof AnotherInterface);
    }

    public void testContainerSuppliedMixin() {
        aspects.registerMixin(cuts.instancesOf(OrderEntity.class), IdentifiableMixin.class);
        pico.component("order1", OrderEntityImpl.class);
        pico.component("order2", OrderEntityImpl.class);

        // register mixin dependency:
        pico.component(IdGenerator.class, IdGeneratorImpl.class);

        Identifiable order1 = (Identifiable) pico.getComponent("order1");
        Identifiable order2 = (Identifiable) pico.getComponent("order2");

        assertEquals(new Integer(1), order1.getId());
        assertEquals(new Integer(2), order2.getId());

        // order1 and order2 do NOT share the same mixin instance (usually a
        // good thing),
        // although their mixin instances do share the same IdGenerator
        order1.setId(new Integer(42));
        assertEquals(new Integer(42), order1.getId());
        assertEquals(new Integer(2), order2.getId());
    }

    public void testContainerSuppliedMixinWithMixinExplicitlyRegistered() {
        aspects.registerMixin(cuts.instancesOf(OrderEntity.class), IdentifiableMixin.class);
        pico.component(IdentifiableMixin.class);
        pico.component("order1", OrderEntityImpl.class);
        pico.component("order2", OrderEntityImpl.class);

        Identifiable order1 = (Identifiable) pico.getComponent("order1");
        Identifiable order2 = (Identifiable) pico.getComponent("order2");

        assertEquals(new Integer(1), order1.getId());
        assertEquals(new Integer(1), order2.getId());

        // order1 and order2 share the same IdentifiableMixin object (not
        // usually what you want!)
        order1.setId(new Integer(42));
        assertEquals(new Integer(42), order1.getId());
        assertEquals(new Integer(42), order2.getId());
    }

    public void testComponentMixin() {
        pico.component("hasMixin", DaoImpl.class);
        pico.component("noMixin", DaoImpl.class);

        aspects.registerMixin(cuts.component("hasMixin"), IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponent("hasMixin");
        Dao noMixin = (Dao) pico.getComponent("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);
        assertTrue(hasMixin instanceof AnotherInterface);
    }

    // weird. what is this suposed to do? does it rely on non-caching ?
    public void doNOT_testContainerSuppliedComponentMixin() {
        aspects.registerMixin(cuts.componentName("hasMixin*"), new Class[]{Identifiable.class},
                IdentifiableMixin.class);

        pico.component("hasMixin1", OrderEntityImpl.class);
        pico.component("hasMixin2", OrderEntityImpl.class);
        pico.component("noMixin", OrderEntityImpl.class);
        pico.component(IdGenerator.class, IdGeneratorImpl.class);

        Identifiable hasMixin1 = (Identifiable) pico.getComponent("hasMixin1");
        Identifiable hasMixin2 = (Identifiable) pico.getComponent("hasMixin1");
        OrderEntity noMixin = (OrderEntity) pico.getComponent("noMixin");

        assertFalse(noMixin instanceof Identifiable);
        assertEquals(new Integer(1), hasMixin1.getId());
        assertEquals(new Integer(2), hasMixin2.getId());

        hasMixin1.setId(new Integer(42));
        assertEquals(new Integer(42), hasMixin1.getId());
        assertEquals(new Integer(2), hasMixin2.getId());
    }

    public void testMixinExplicitInterfaces() {
        aspects.registerMixin(cuts.instancesOf(Dao.class), new Class[]{Identifiable.class}, IdentifiableMixin.class);
        pico.component(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponent(Dao.class);
        verifyMixin(dao);
        assertFalse(dao instanceof AnotherInterface);
    }

    public void testComponentMixinExplicitInterfaces() {
        pico.component("hasMixin", DaoImpl.class);
        pico.component("noMixin", DaoImpl.class);

        aspects.registerMixin(cuts.component("hasMixin"), new Class[]{Identifiable.class}, IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponent("hasMixin");
        Dao noMixin = (Dao) pico.getComponent("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);

        assertFalse(hasMixin instanceof AnotherInterface);
    }

    public void testCustomClassPointcut() {
        StringBuffer log = new StringBuffer();

        ClassPointcut customCut = new ClassPointcut() {
            public boolean picks(Class clazz) {
                return true;
            }
        };

        aspects.registerInterceptor(customCut, cuts.allMethods(), new LoggingInterceptor(log));
        pico.component(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponent(Dao.class);
        verifyIntercepted(dao, log);
    }

    public void testCustomMethodPointcut() {
        StringBuffer log = new StringBuffer();

        MethodPointcut customCut = new MethodPointcut() {
            public boolean picks(Method method) {
                return true;
            }
        };

        aspects.registerInterceptor(cuts.instancesOf(Dao.class), customCut, new LoggingInterceptor(log));
        pico.component(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponent(Dao.class);
        verifyIntercepted(dao, log);
    }

    public void testCustomComponentPointcut() {
        StringBuffer log = new StringBuffer();

        ComponentPointcut customCut = new ComponentPointcut() {
            public boolean picks(Object componentKey) {
                return true;
            }
        };

        aspects.registerInterceptor(customCut, cuts.allMethods(), new LoggingInterceptor(log));
        pico.component(Dao.class, DaoImpl.class);
        Dao dao = (Dao) pico.getComponent(Dao.class);
        verifyIntercepted(dao, log);
    }

}