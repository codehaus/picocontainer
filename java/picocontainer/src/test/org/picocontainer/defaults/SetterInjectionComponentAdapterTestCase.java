/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.PersonBean;
import org.picocontainer.testmodel.PurseBean;

import java.util.ArrayList;
import java.util.List;

public class SetterInjectionComponentAdapterTestCase extends AbstractComponentAdapterTestCase {

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#getComponentAdapterType()
     */
    protected Class getComponentAdapterType() {
        return SetterInjectionComponentAdapter.class;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#createDefaultComponentAdapterFactory()
     */
    protected ComponentAdapterFactory createDefaultComponentAdapterFactory() {
        return new CachingComponentAdapterFactory(new SetterInjectionComponentAdapterFactory());
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestVerifyWithoutDependencyWorks(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestVerifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return new SetterInjectionComponentAdapter(PersonBean.class, PersonBean.class, new Parameter[] { new ConstantParameter("Pico Container") });
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestVerifyDoesNotInstantiate(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestVerifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        return new SetterInjectionComponentAdapter(DeadBody.class, DeadBody.class, new Parameter[] { new ComponentParameter() });
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestVisitable()
     */
    protected ComponentAdapter prepareTestVisitable() {
        return new SetterInjectionComponentAdapter(PersonBean.class, PersonBean.class, new Parameter[] { new ConstantParameter("Pico Container") });
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestSerializable(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestSerializable(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        return new SetterInjectionComponentAdapter(PersonBean.class, PersonBean.class, new Parameter[] { new ComponentParameter() });
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestShouldBeAbleToTakeParameters(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestShouldBeAbleToTakeParameters(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        picoContainer.registerComponentImplementation(PersonBean.class);
        return picoContainer.registerComponent(new SetterInjectionComponentAdapter(PurseBean.class, MoneyPurse.class, new Parameter[] { 
                new ComponentParameter(),
                new ConstantParameter(new Double(100.0))
        }));
    }
    
    public static class MoneyPurse extends PurseBean {
        double money;
        public double getMoney() {
            return money;
        }
        public void setMoney(double money) {
            this.money = money;
        }
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestFailingVerificationWithUnsatisfiedDependency(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestFailingVerificationWithUnsatisfiedDependency(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        picoContainer.registerComponentImplementation(PersonBean.class);
        return picoContainer.registerComponent(new SetterInjectionComponentAdapter(PurseBean.class, MoneyPurse.class, new Parameter[] { new ComponentParameter() }));
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestComponentAdapterCreatesNewInstances(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestComponentAdapterCreatesNewInstances(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        return new SetterInjectionComponentAdapter(PersonBean.class, PersonBean.class, new Parameter[] { new ComponentParameter() });
    }

    public static class Ghost extends PersonBean {
        public Ghost() {
            throw new VerifyError("test");
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestErrorIsRethrown(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestErrorIsRethrown(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        return new SetterInjectionComponentAdapter(Ghost.class, Ghost.class, new Parameter[] { new ComponentParameter() });
    }

    public static class DeadBody extends PersonBean {
        public DeadBody() throws Exception {
            throw new RuntimeException("test");
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestRuntimeExceptionIsRethrown(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestRuntimeExceptionIsRethrown(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        return new SetterInjectionComponentAdapter(DeadBody.class, DeadBody.class, new Parameter[] { new ComponentParameter() });
    }

    public static class HidingPersion extends PersonBean {
        public HidingPersion() throws Exception {
            throw new Exception("test");
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestNormalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestNormalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(
            MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        return new SetterInjectionComponentAdapter(HidingPersion.class, HidingPersion.class, new Parameter[] { new ComponentParameter() });
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestDependenciesAreResolved(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestDependenciesAreResolved(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        picoContainer.registerComponentImplementation(PersonBean.class);
        return new SetterInjectionComponentAdapter(PurseBean.class, PurseBean.class, new Parameter[] { new ComponentParameter() });
    }

    public static class WealthyPerson extends PersonBean {
        PurseBean purse;
        public PurseBean getPurse() {
            return purse;
        }
        public void setPurse(PurseBean purse) {
            this.purse = purse;
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestFailingVerificationWithCyclicDependencyException(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestFailingVerificationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        picoContainer.registerComponentImplementation(PersonBean.class, WealthyPerson.class);
        return picoContainer.registerComponent(new SetterInjectionComponentAdapter(PurseBean.class, PurseBean.class, new Parameter[] { new ComponentParameter() }));
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestFailingInstantiationWithCyclicDependencyException(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestFailingInstantiationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        picoContainer.registerComponentInstance("Pico Container");
        picoContainer.registerComponentImplementation(PersonBean.class, WealthyPerson.class);
        return picoContainer.registerComponent(new SetterInjectionComponentAdapter(PurseBean.class, PurseBean.class, new Parameter[] { new ComponentParameter() }));
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

    public static class B {
    }

    public void testAllUnsatisfiableDependenciesAreSignalled() {
        SetterInjectionComponentAdapter aAdapter = new SetterInjectionComponentAdapter("a", A.class, null);
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter("b", B.class, null);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(aAdapter);

        try {
            aAdapter.getComponentInstance(pico);
        } catch (UnsatisfiableDependenciesException e) {
            assertTrue(e.getUnsatisfiableDependencies().contains(List.class));
            assertTrue(e.getUnsatisfiableDependencies().contains(String.class));
        }
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
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter("b", B.class, null);
        SetterInjectionComponentAdapter cAdapter = new SetterInjectionComponentAdapter("c", C.class, null);
        SetterInjectionComponentAdapter cNullAdapter = new SetterInjectionComponentAdapter("c0", C.class, null);

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(cAdapter);
        pico.registerComponent(cNullAdapter);
        pico.registerComponentImplementation(ArrayList.class);

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
        MutablePicoContainer pico = new DefaultPicoContainer(new CachingComponentAdapterFactory(new SetterInjectionComponentAdapterFactory()));

        pico.registerComponentImplementation(Yin.class);
        pico.registerComponentImplementation(Yang.class);

        Yin yin = (Yin) pico.getComponentInstance(Yin.class);
        Yang yang = (Yang) pico.getComponentInstance(Yang.class);

        assertSame(yin, yang.getYin());
        assertSame(yang, yin.getYang());
    }
}
