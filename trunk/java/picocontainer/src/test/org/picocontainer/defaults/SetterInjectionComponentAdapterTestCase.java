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

import junit.framework.TestCase;

import org.picocontainer.Disposable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.Startable;

import java.util.ArrayList;
import java.util.List;

public class SetterInjectionComponentAdapterTestCase extends TestCase {

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

    public void testDependenciesAreResolved() {
        SetterInjectionComponentAdapter aAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("a", A.class, null));
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class, null));

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(aAdapter);
        pico.registerComponentInstance("YO");
        pico.registerComponentImplementation(ArrayList.class);

        A a = (A) aAdapter.getComponentInstance();
        assertNotNull(a.getB());
        assertNotNull(a.getString());
        assertNotNull(a.getList());
    }

    public void testAllUnsatisfiableDependenciesAreSignalled() {
        SetterInjectionComponentAdapter aAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("a", A.class, null));
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class, null));

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(aAdapter);

        try {
            aAdapter.getComponentInstance();
        } catch (UnsatisfiableDependenciesException e) {
            e.getUnsatisfiableDependencies().contains(List.class);
            e.getUnsatisfiableDependencies().contains(String.class);
        }
    }

    public void testShouldBeAbleToTakeParameters() {
        ArrayList list = new ArrayList();
        Parameter[] aParameters = new Parameter[]{
            new ComponentParameter(),
            new ConstantParameter("YO"),
            new ConstantParameter(list)
        };
        SetterInjectionComponentAdapter aAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("a", A.class), aParameters);
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class));

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(aAdapter);

        A a = (A) aAdapter.getComponentInstance();
        assertNotNull(a.getB());
        assertEquals("YO", a.getString());
        assertSame(list, a.getList());
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

    public void notSureWhatThisIsTestingOrWhyItIsNeeded___testHybrids() {
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class, new Parameter[]{}));
        SetterInjectionComponentAdapter cAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("c", C.class, new Parameter[]{}));
        SetterInjectionComponentAdapter cNullAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("c0", C.class, null));

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(cAdapter);
        pico.registerComponent(cNullAdapter);
        pico.registerComponentImplementation(ArrayList.class);

        C c = (C) cAdapter.getComponentInstance();
        assertTrue(c.instantiatedAsBean());
        C c0 = (C) cNullAdapter.getComponentInstance();
        assertTrue(c0.instantiatedAsBean());
    }

    // TODO: Factor out test classes and unit tests (currently copied from DefaultPicoContainerLifecycleTestCase) 
    public abstract static class RecordingLifecycle implements Startable, Disposable {
        private StringBuffer recording;

        protected RecordingLifecycle() {
        }
        
        public void setRecording(StringBuffer recording) {
            this.recording = recording;
        }

        public void start() {
            recording.append("<" + code());
        }

        public void stop() {
            recording.append(code() + ">");
        }

        public void dispose() {
            recording.append("!" + code());
        }

        private String code() {
            String name = getClass().getName();
            return name.substring(name.indexOf('$') + 1);
        }
    }

    public static class One extends RecordingLifecycle {
        public One() {
        }
    }

    public static class Two extends RecordingLifecycle {
        public Two() {
        }
        public void setOne(One one) {
        }
    }

    public static class Three extends RecordingLifecycle {
        public Three() {
        }
        public void setOne(One one) {
        }
        public void setTwo(Two two) {
        }
    }

    public static class Four extends RecordingLifecycle {
        public Four() {
        }
        public void setOne(One one) {
        }
        public void setTwo(Two two) {
        }
        public void setThree(Three three) {
        }
    }

    // TODO: fails with incorrect order
    public void XXXtestOrderOfInstantiationShouldBeDependencyOrder() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer(
                new CachingComponentAdapterFactory(
                        new SetterInjectionComponentAdapterFactory(
                                new ConstructorInjectionComponentAdapterFactory())));
        pico.registerComponent(
                new CachingComponentAdapter(
                        new ConstructorInjectionComponentAdapter("recording", StringBuffer.class)));
        pico.registerComponentImplementation(Four.class);
        pico.registerComponentImplementation(Two.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Three.class);
        final List componentInstances = pico.getComponentInstances();

        // instantiation - would be difficult to do these in the wrong order!!
        assertEquals("Incorrect Order of Instantiation", One.class, componentInstances.get(1).getClass());
        assertEquals("Incorrect Order of Instantiation", Two.class, componentInstances.get(2).getClass());
        assertEquals("Incorrect Order of Instantiation", Three.class, componentInstances.get(3).getClass());
        assertEquals("Incorrect Order of Instantiation", Four.class, componentInstances.get(4).getClass());
    }

    // TODO: fails with NPE
    public void XXXtestOrderOfStartShouldBeDependencyOrderAndStopAndDisposeTheOpposite() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(
                new CachingComponentAdapter(
                        new ConstructorInjectionComponentAdapter("recording", StringBuffer.class)));
        pico.registerComponentImplementation(Four.class);
        pico.registerComponentImplementation(Two.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Three.class);

        pico.start();
        pico.stop();
        pico.dispose();

        assertEquals("<One<Two<Three<FourFour>Three>Two>One>!Four!Three!Two!One", pico.getComponentInstance("recording").toString());
    }
}
