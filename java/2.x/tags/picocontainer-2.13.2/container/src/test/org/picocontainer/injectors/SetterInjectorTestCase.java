/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.picocontainer.parameters.ComponentParameter.DEFAULT;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.NameBinding;
import org.picocontainer.Parameter;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.tck.AbstractComponentAdapterTest;
import org.picocontainer.testmodel.PersonBean;
import org.picocontainer.testmodel.PurseBean;


@SuppressWarnings("serial")
public class SetterInjectorTestCase
    extends AbstractComponentAdapterTest {

    protected Class getComponentAdapterType() {
        return SetterInjector.class;
    }

    protected ComponentFactory createDefaultComponentFactory() {
        return new Caching().wrap(new SetterInjection());
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return new SetterInjector(PersonBean.class, PersonBean.class, new Parameter[] {new ConstantParameter(
                "Pico Container")}, new NullComponentMonitor(), "set", "", false, false);
    }

    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjector(DeadBody.class, DeadBody.class, new Parameter[] {DEFAULT}, new NullComponentMonitor(),
                                  "set", "", false, false);
    }

    protected ComponentAdapter prepDEF_visitable() {
        return new SetterInjector(PersonBean.class, PersonBean.class, new Parameter[] {new ConstantParameter(
                "Pico Container")}, new NullComponentMonitor(), "set", "", false, false);

    }

    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjector(PersonBean.class, PersonBean.class, new Parameter[] {DEFAULT}, new NullComponentMonitor(),
                                  "set", "", false, false);
    }

    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return prepSER_isSerializable(picoContainer);
    }

    protected ComponentAdapter prepDEF_isAbleToTakeParameters(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        picoContainer.addComponent(PersonBean.class);
        SetterInjector componentAdapter = new SetterInjector(
                PurseBean.class, MoneyPurse.class, new Parameter[] {DEFAULT, new ConstantParameter(100.0)}, new NullComponentMonitor(),
                "set", "", false, false);
        return picoContainer.as(Characteristics.NO_CACHE).addAdapter(componentAdapter).getComponentAdapter(PurseBean.class, (NameBinding) null);
    }

    public static class MoneyPurse
            extends PurseBean {
        double money;

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }
    }

    protected ComponentAdapter prepVER_verificationFails(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        picoContainer.addComponent(PersonBean.class);
        SetterInjector componentAdapter = new SetterInjector(
                PurseBean.class, MoneyPurse.class, new Parameter[] {DEFAULT},new NullComponentMonitor(),
                "set", "", false, false);
        return picoContainer.addAdapter(componentAdapter).getComponentAdapter(PurseBean.class, (NameBinding) null);
    }

    protected ComponentAdapter prepINS_createsNewInstances(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjector(PersonBean.class, PersonBean.class, new Parameter[] {DEFAULT}, new NullComponentMonitor(),
                                  "set", "", false, false);
    }

    public static class Ghost
            extends PersonBean {
        public Ghost() {
            throw new VerifyError("test");
        }
    }

    protected ComponentAdapter prepINS_errorIsRethrown(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjector(Ghost.class, Ghost.class, new Parameter[] {DEFAULT}, new NullComponentMonitor(),
                                  "set", "", false, false);
    }

    public static class DeadBody
            extends PersonBean {
        public DeadBody() {
            throw new RuntimeException("test");
        }
    }

    protected ComponentAdapter prepINS_runtimeExceptionIsRethrown(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjector(DeadBody.class, DeadBody.class, new Parameter[] {DEFAULT}, new NullComponentMonitor(),
                                  "set", "", false, false);
    }

    public static class HidingPersion
            extends PersonBean {
        public HidingPersion() throws Exception {
            throw new Exception("test");
        }
    }

    protected ComponentAdapter prepINS_normalExceptionIsRethrownInsidePicoInitializationException(
            MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjector(
                HidingPersion.class, HidingPersion.class, new Parameter[] {DEFAULT}, new NullComponentMonitor(),
                "set", "", false, false);
    }

    protected ComponentAdapter prepRES_dependenciesAreResolved(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        picoContainer.addComponent(PersonBean.class);
        return new SetterInjector(PurseBean.class, PurseBean.class, new Parameter[] {DEFAULT}, new NullComponentMonitor(),
                                  "set", "", false, false);
    }

    public static class WealthyPerson
            extends PersonBean {
        PurseBean purse;

        public PurseBean getPurse() {
            return purse;
        }

        public void setPurse(PurseBean purse) {
            this.purse = purse;
        }
    }

    protected ComponentAdapter prepRES_failingVerificationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        picoContainer.addComponent(PersonBean.class, WealthyPerson.class);
        SetterInjector componentAdapter = new SetterInjector(
                PurseBean.class, PurseBean.class, new Parameter[] {DEFAULT}, new NullComponentMonitor(),
                "set", "", false, false);
        return picoContainer.as(Characteristics.NO_CACHE).addAdapter(componentAdapter).getComponentAdapter(PurseBean.class, (NameBinding) null);
    }

    protected ComponentAdapter prepRES_failingInstantiationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        picoContainer.addComponent(PersonBean.class, WealthyPerson.class);
        SetterInjector componentAdapter = new SetterInjector(
                PurseBean.class, PurseBean.class, new Parameter[] {DEFAULT}, new NullComponentMonitor(),
                "set", "", false, false);
        return picoContainer.as(Characteristics.NO_CACHE).addAdapter(componentAdapter).getComponentAdapter(PurseBean.class, (NameBinding) null);
    }

    public static class A {
        private B b;
        private String string;
        private List list;

        public void setB(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public List getList() {
            return list;
        }

        public void setList(List list) {
            this.list = list;
        }
    }

    public static class A2 {
        private B b;
        private String string;
        private List list;

        public void injectB(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public String getString() {
            return string;
        }

        public void injectString(String string) {
            this.string = string;
        }

        public List getList() {
            return list;
        }

        public void injectList(List list) {
            this.list = list;
        }
    }


    public static class B {
    }

    @Test public void testAllUnsatisfiableDependenciesAreSignalled() {
        SetterInjector aAdapter = new SetterInjector("a", A.class, Parameter.DEFAULT, new NullComponentMonitor(),
                                                     "set", "", false, false);
        SetterInjector bAdapter = new SetterInjector("b", B.class, Parameter.DEFAULT, new NullComponentMonitor(),
                                                     "set", "", false, false);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(bAdapter);
        pico.addAdapter(aAdapter);

        try {
            aAdapter.getComponentInstance(pico, ComponentAdapter.NOTHING.class);
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            assertTrue(e.getUnsatisfiableDependencies().contains(List.class));
            assertTrue(e.getUnsatisfiableDependencies().contains(String.class));
        }
    }

    @Test public void testAllUnsatisfiableDependenciesAreSignalled2() {
        SetterInjector aAdapter = new SetterInjector(A2.class, A2.class, null, new NullComponentMonitor(),
                                                     "set", "", false, false);
        SetterInjector bAdapter = new SetterInjector("b", B.class, null, new NullComponentMonitor(),
                                                     "set", "", false, false);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(List.class, ArrayList.class)
            .addComponent(String.class, "foo")
            .addAdapter(bAdapter)
            .addAdapter(aAdapter);

        aAdapter.getComponentInstance(pico, ComponentAdapter.NOTHING.class);

        assertNotNull(aAdapter);

        A2 a = pico.getComponent(A2.class);
        assertTrue(a.getList() == null);
        assertTrue(a.getString() == null);
    }

    public static class InitBurp {

        private Wind wind;

        public void initWind(Wind wind) {
            this.wind = wind;
        }
    }

    public static class SetterBurp {

        private Wind wind;

        public void setWind(Wind wind) {
            this.wind = wind;
        }
    }

    public static class Wind {

    }

    @Test public void testSetterMethodInjectionToContrastWithThatBelow() {

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(SetterBurp.class, SetterBurp.class, Parameter.DEFAULT, new NullComponentMonitor(),
                                           "set", "", false, false));
        pico.addComponent(Wind.class, new Wind());
        SetterBurp burp = pico.getComponent(SetterBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

    @Test public void testNonSetterMethodInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(InitBurp.class, InitBurp.class, Parameter.DEFAULT, new NullComponentMonitor(),
                                           "set", "", false, false) {
            protected String getInjectorPrefix() {
                return "init";
            }
        });
        pico.addComponent(Wind.class, new Wind());
        InitBurp burp = pico.getComponent(InitBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

    @Test public void testNonSetterMethodInjectionWithoutOverridingSetterPrefix() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(InitBurp.class, InitBurp.class, new Parameter[0], new NullComponentMonitor(),
                                           "set", "", false, false));
        pico.addComponent(Wind.class, new Wind());
        InitBurp burp = pico.getComponent(InitBurp.class);
        assertNotNull(burp);
        assertTrue(burp.wind == null);
    }


    public static class C {
        private B b;
        private List l;
        private final boolean asBean;

        public C() {
            asBean = true;
        }

        public C(B b) {
            this.l = new ArrayList();
            this.b = b;
            asBean = false;
        }

        public void setB(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public void setList(List l) {
            this.l = l;
        }

        public List getList() {
            return l;
        }

        public boolean instantiatedAsBean() {
            return asBean;
        }
    }

    @Test public void testHybridBeans() {
        SetterInjector bAdapter = new SetterInjector("b", B.class, null, new NullComponentMonitor(),
                                                     "set", "", false, false);
        SetterInjector cAdapter = new SetterInjector("c", C.class, null, new NullComponentMonitor(),
                                                     "set", "", false, false);
        SetterInjector cNullAdapter = new SetterInjector("c0", C.class, null, new NullComponentMonitor(),
                                                         "set", "", false, false);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(bAdapter);
        pico.addAdapter(cAdapter);
        pico.addAdapter(cNullAdapter);
        pico.addComponent(ArrayList.class);

        C c = (C) cAdapter.getComponentInstance(pico, ComponentAdapter.NOTHING.class);
        assertTrue(c.instantiatedAsBean());
        C c0 = (C) cNullAdapter.getComponentInstance(pico, ComponentAdapter.NOTHING.class);
        assertTrue(c0.instantiatedAsBean());
    }

    public static class Yin {
        private Yang yang;

        public void setYin(Yang yang) {
            this.yang = yang;
        }

        public Yang getYang() {
            return yang;
        }
    }

    public static class Yang {
        private Yin yin;

        public void setYang(Yin yin) {
            this.yin = yin;
        }

        public Yin getYin() {
            return yin;
        }
    }

    //@Test  http://jira.codehaus.org/browse/PICO-188
    public void shouldBeAbleToHandleMutualDependenciesWithSetterInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer(new Caching().wrap(new SetterInjection()));

        pico.addComponent(Yin.class);
        pico.addComponent(Yang.class);

        Yin yin = pico.getComponent(Yin.class);
        Yang yang = pico.getComponent(Yang.class);

        assertSame(yin, yang.getYin());
        assertSame(yang, yin.getYang());
    }

    @Test
    public void shouldProvideEmptyArgumentListForDefaultConstructor() throws Exception {
        final Mockery mockery = new Mockery();
        final ComponentMonitor componentMonitor = mockery.mock(ComponentMonitor.class);
        final MutablePicoContainer pico = new DefaultPicoContainer(
            new SetterInjection(), new NullLifecycleStrategy(), new EmptyPicoContainer(), componentMonitor
        );

        mockery.checking(new Expectations() {{
            oneOf(componentMonitor).newInjector(
                    with(any(org.picocontainer.Injector.class))
            ); will(returnSameInjector());
        }});

        pico.addComponent(B.class);

        mockery.checking(new Expectations() {{
            oneOf(componentMonitor).instantiating(
                    with(same(pico)), with(any(ComponentAdapter.class)), with(equal(B.class.getConstructor()))
            ); will(new CustomAction("return same constructor") {
                public Object invoke(Invocation invocation) {
                    return invocation.getParameter(2);
                }
            });
            oneOf(componentMonitor).instantiated(
                    with(same(pico)), with(any(ComponentAdapter.class)), with(equal(B.class.getConstructor())),
                    with(any(Object.class)), with(equal(new Object[0])), with(any(long.class))
            );
        }});
        pico.getComponent(B.class);

        mockery.assertIsSatisfied();
    }

    private CustomAction returnSameInjector() {
        return new CustomAction("return same injector") {
            public Object invoke(Invocation invocation) {
                return invocation.getParameter(0);
            }
        };
    }
}
