/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.adapters;

import java.util.ArrayList;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.monitors.DelegatingComponentMonitor;
import static org.picocontainer.parameters.ComponentParameter.*;
import org.picocontainer.adapters.CachingBehaviorFactory;
import org.picocontainer.adapters.SetterInjectionAdapter;
import org.picocontainer.adapters.SetterInjectionFactory;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.PersonBean;
import org.picocontainer.testmodel.PurseBean;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;


public class SetterInjectionAdapterTestCase
        extends AbstractComponentAdapterTestCase {

    protected Class getComponentAdapterType() {
        return SetterInjectionAdapter.class;
    }

    protected ComponentFactory createDefaultComponentAdapterFactory() {
        return new CachingBehaviorFactory().forThis(new SetterInjectionFactory());
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return new SetterInjectionAdapter(PersonBean.class, PersonBean.class, new ConstantParameter(
                "Pico Container"));
    }

    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjectionAdapter(DeadBody.class, DeadBody.class, DEFAULT);
    }

    protected ComponentAdapter prepDEF_visitable() {
        return new SetterInjectionAdapter(PersonBean.class, PersonBean.class, new ConstantParameter(
                "Pico Container"));
    }

    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjectionAdapter(PersonBean.class, PersonBean.class, DEFAULT);
    }

    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return prepSER_isSerializable(picoContainer);
    }

    protected ComponentAdapter prepDEF_isAbleToTakeParameters(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        picoContainer.addComponent(PersonBean.class);
        SetterInjectionAdapter componentAdapter = new SetterInjectionAdapter(
                PurseBean.class, MoneyPurse.class, DEFAULT, new ConstantParameter(100.0));
        return picoContainer.addAdapter(componentAdapter).lastCA();
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
        SetterInjectionAdapter componentAdapter = new SetterInjectionAdapter(
                PurseBean.class, MoneyPurse.class, DEFAULT);
        return picoContainer.addAdapter(componentAdapter).lastCA();
    }

    protected ComponentAdapter prepINS_createsNewInstances(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjectionAdapter(PersonBean.class, PersonBean.class, DEFAULT);
    }

    public static class Ghost
            extends PersonBean {
        public Ghost() {
            throw new VerifyError("test");
        }
    }

    protected ComponentAdapter prepINS_errorIsRethrown(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjectionAdapter(Ghost.class, Ghost.class, DEFAULT);
    }

    public static class DeadBody
            extends PersonBean {
        public DeadBody() {
            throw new RuntimeException("test");
        }
    }

    protected ComponentAdapter prepINS_runtimeExceptionIsRethrown(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        return new SetterInjectionAdapter(DeadBody.class, DeadBody.class, DEFAULT);
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
        return new SetterInjectionAdapter(
                HidingPersion.class, HidingPersion.class, DEFAULT);
    }

    protected ComponentAdapter prepRES_dependenciesAreResolved(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        picoContainer.addComponent(PersonBean.class);
        return new SetterInjectionAdapter(PurseBean.class, PurseBean.class, DEFAULT);
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
        SetterInjectionAdapter componentAdapter = new SetterInjectionAdapter(
                PurseBean.class, PurseBean.class, DEFAULT);
        return picoContainer.addAdapter(componentAdapter).lastCA();
    }

    protected ComponentAdapter prepRES_failingInstantiationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        picoContainer.addComponent("Pico Container");
        picoContainer.addComponent(PersonBean.class, WealthyPerson.class);
        SetterInjectionAdapter componentAdapter = new SetterInjectionAdapter(
                PurseBean.class, PurseBean.class, DEFAULT);
        return picoContainer.addAdapter(componentAdapter).lastCA();
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

    public void testAllUnsatisfiableDependenciesAreSignalled() {
        SetterInjectionAdapter aAdapter = new SetterInjectionAdapter("a", A.class);
        SetterInjectionAdapter bAdapter = new SetterInjectionAdapter("b", B.class);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(bAdapter);
        pico.addAdapter(aAdapter);

        try {
            aAdapter.getComponentInstance(pico);
        } catch (UnsatisfiableDependenciesException e) {
            assertTrue(e.getUnsatisfiableDependencies().contains(List.class));
            assertTrue(e.getUnsatisfiableDependencies().contains(String.class));
        }
    }

    public void testAllUnsatisfiableDependenciesAreSignalled2() {
        SetterInjectionAdapter aAdapter = new SetterInjectionAdapter(A2.class, A2.class);
        SetterInjectionAdapter bAdapter = new SetterInjectionAdapter("b", B.class);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(List.class, ArrayList.class).addComponent(String.class, "foo");
        pico.addAdapter(bAdapter);
        pico.addAdapter(aAdapter);

        aAdapter.getComponentInstance(pico);

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

    public void testSetterMethodInjectionToContrastWithThatBelow() {

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjectionAdapter(SetterBurp.class, SetterBurp.class, Parameter.DEFAULT));
        pico.addComponent(Wind.class, new Wind());
        SetterBurp burp = pico.getComponent(SetterBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

    public void testNonSetterMethodInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjectionAdapter(InitBurp.class, InitBurp.class, Parameter.DEFAULT) {
            protected String getInjectorPrefix() {
                return "init";
            }
        });
        pico.addComponent(Wind.class, new Wind());
        InitBurp burp = pico.getComponent(InitBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

    public void testNonSetterMethodInjectionWithoutOverridingSetterPrefix() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjectionAdapter(InitBurp.class, InitBurp.class, Parameter.ZERO));
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

    public void testHybridBeans() {
        SetterInjectionAdapter bAdapter = new SetterInjectionAdapter("b", B.class, (Parameter[])null);
        SetterInjectionAdapter cAdapter = new SetterInjectionAdapter("c", C.class, (Parameter[])null);
        SetterInjectionAdapter cNullAdapter = new SetterInjectionAdapter("c0", C.class, (Parameter[])null);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(bAdapter);
        pico.addAdapter(cAdapter);
        pico.addAdapter(cNullAdapter);
        pico.addComponent(ArrayList.class);

        C c = (C) cAdapter.getComponentInstance(pico);
        assertTrue(c.instantiatedAsBean());
        C c0 = (C) cNullAdapter.getComponentInstance(pico);
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

    // TODO PICO-188
    // http://jira.codehaus.org/browse/PICO-188
    public void FIXME_testShouldBeAbleToHandleMutualDependenciesWithSetterInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer(new CachingBehaviorFactory().forThis(new SetterInjectionFactory()));

        pico.addComponent(Yin.class);
        pico.addComponent(Yang.class);

        Yin yin = pico.getComponent(Yin.class);
        Yang yang = pico.getComponent(Yang.class);

        assertSame(yin, yang.getYin());
        assertSame(yang, yin.getYang());
    }
    
    public void testCustomLifecycleCanBeInjected() throws NoSuchMethodException {
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        SetterInjectionAdapter sica = new SetterInjectionAdapter(
                NullLifecycle.class, NullLifecycle.class, new Parameter[0],
                new DelegatingComponentMonitor(), strategy);
        Touchable touchable = new SimpleTouchable();
        sica.start(touchable);
        sica.stop(touchable);
        sica.dispose(touchable);
        assertEquals("<start<stop<dispose", strategy.recording());
    }

}
