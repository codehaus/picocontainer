/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import junit.framework.TestCase;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoInitializationException;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.WilmaImpl;
import picocontainer.defaults.WrongNumberOfConstructorsRegistrationException;

import java.util.ArrayList;
import java.util.List;

public class DefaultPicoContainerTestCase extends TestCase {

    public interface Peelable {
        void peel();
    }

    public interface Washable {
        void wash();
    }

    public interface Startable {
        void start();

        void stop();
    }

    public static class PeelableComponent implements Peelable {
        boolean wasPeeled;

        public PeelableComponent() {
        }

        public void peel() {
            wasPeeled = true;
        }
    }

    public static class CoincidentallyPeelableComponent {
        boolean wasPeeled;

        public void peel() {
            wasPeeled = true;
        }
    }

    public static class PeelableAndWashableComponent extends PeelableComponent implements Washable {
        boolean wasWashed;

        public void wash() {
            wasWashed = true;
        }
    }

    public static class Recorder {
        List thingsThatHappened = new ArrayList();

        public void record(String something) {
            thingsThatHappened.add(something);
        }

        public String getWhatHappened(int i) {
            return (String) thingsThatHappened.get(i);
        }

        public void clear() {
            thingsThatHappened.clear();
        }
    }

    public static abstract class RecordingAware {
        protected Recorder recorder;

        public RecordingAware(Recorder recorder) {
            this.recorder = recorder;
            String name = getClass().getName();
            String niceName = name.substring(name.lastIndexOf("$") + 1);
            recorder.record("instantiated " + niceName);
        }
    }

    public static class A extends RecordingAware implements Washable {
        public A(Recorder recorder) {
            super(recorder);
        }

        public void wash() {
            recorder.record("A.wash()");
        }
    }

    public static class B extends RecordingAware implements Washable {
        public B(Recorder recorder, A a) {
            super(recorder);
            a.toString();
        }

        public void wash() {
            recorder.record("B.wash()");
        }
    }

    public static class C extends RecordingAware implements Washable {
        public C(Recorder recorder, A a, B b) {
            super(recorder);
            a.toString();
            b.toString();
        }

        public void wash() {
            recorder.record("C.wash()");
        }
    }

    public void testApplyInterfaceMethodsToWholeContainer() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();
        pico.registerComponent(PeelableComponent.class);
        pico.registerComponent(CoincidentallyPeelableComponent.class);
        pico.instantiateComponents();

        assertEquals(2, pico.getComponents().length);

        Peelable myPeelableContainer = (Peelable) pico.getMultipleInheritanceProxy();

        myPeelableContainer.peel();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponent(PeelableComponent.class);
        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponent(CoincidentallyPeelableComponent.class);

        assertTrue(peelableComponent.wasPeeled);
        assertFalse(notReallyPeelableComponent.wasPeeled);
    }

    public void testWorksWithMultipleInterfaces() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(PeelableComponent.class);
        pico.registerComponent(CoincidentallyPeelableComponent.class);
        pico.registerComponent(PeelableAndWashableComponent.class);

        pico.instantiateComponents();

        Object myPeelableAndWashableContainer = pico.getMultipleInheritanceProxy();

        ((Washable) myPeelableAndWashableContainer).wash();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponent(PeelableComponent.class);
        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponent(CoincidentallyPeelableComponent.class);
        PeelableAndWashableComponent washAndPeel =
                (PeelableAndWashableComponent) pico.getComponent(PeelableAndWashableComponent.class);
        ;

        assertFalse(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);

        ((Peelable) myPeelableAndWashableContainer).peel();

        assertTrue(peelableComponent.wasPeeled);
        assertTrue(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);
        assertFalse(notReallyPeelableComponent.wasPeeled);

    }

    public void testAsCallsAllComponents() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        //an unmanaged component
        PeelableComponent peelableComponent = new PeelableComponent();
        pico.registerComponent(PeelableComponent.class, peelableComponent);

        //some managed ones
        pico.registerComponent(CoincidentallyPeelableComponent.class);
        pico.registerComponent(PeelableAndWashableComponent.class);

        pico.instantiateComponents();

        Object myPeelableAndWashableContainer = pico.getMultipleInheritanceProxy();

        ((Washable) myPeelableAndWashableContainer).wash();

        CoincidentallyPeelableComponent notReallyPeelableComponent =
                (CoincidentallyPeelableComponent) pico.getComponent(CoincidentallyPeelableComponent.class);
        PeelableAndWashableComponent washAndPeel =
                (PeelableAndWashableComponent) pico.getComponent(PeelableAndWashableComponent.class);

        assertFalse(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);

        ((Peelable) myPeelableAndWashableContainer).peel();

        assertTrue(peelableComponent.wasPeeled);
        assertTrue(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);
        assertFalse(notReallyPeelableComponent.wasPeeled);

    }

    public void testLifecycleCallsComponentsInReverseOrder() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        Recorder recorder = new Recorder();

        pico.registerComponent(Recorder.class, recorder);
        pico.registerComponent(A.class, A.class);
        pico.registerComponent(B.class, B.class);
        pico.registerComponent(C.class, C.class);

        pico.instantiateComponents();

        assertEquals("instantiated A", recorder.getWhatHappened(0));
        assertEquals("instantiated B", recorder.getWhatHappened(1));
        assertEquals("instantiated C", recorder.getWhatHappened(2));

        recorder.clear();

        Object washableContainer =
                pico.getAggregateComponentProxy(false,true);

        ((Washable) washableContainer).wash();

        assertEquals("C.wash()", recorder.getWhatHappened(0));
        assertEquals("B.wash()", recorder.getWhatHappened(1));
        assertEquals("A.wash()", recorder.getWhatHappened(2));

    }

    public void testAsLifecycleOnlyCallsManagedComponents() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        Recorder recorder = new Recorder();

        A unmanagedComponent = new A(recorder);

        recorder.clear();

        pico.registerComponent(Recorder.class, recorder);
        pico.registerComponent(A.class, unmanagedComponent);
        pico.registerComponent(B.class);
        pico.registerComponent(C.class);

        pico.instantiateComponents();

        assertEquals("instantiated B", recorder.getWhatHappened(0));
        assertEquals("instantiated C", recorder.getWhatHappened(1));

        recorder.clear();

        Object washableContainer =
                pico.getAggregateComponentProxy(false,false);

        ((Washable) washableContainer).wash();

        assertEquals("C.wash()", recorder.getWhatHappened(0));
        assertEquals("B.wash()", recorder.getWhatHappened(1));
        assertTrue(
                "Unmanaged components should not be called by an asLifecycle() proxy",
                !recorder.thingsThatHappened.contains("A.wash()"));
    }

    public void testPeelableAndWashable() throws WrongNumberOfConstructorsRegistrationException, PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(PeelableComponent.class);
        pico.registerComponent(PeelableAndWashableComponent.class);

        pico.instantiateComponents();

        Object proxy = pico.getMultipleInheritanceProxy();

        Peelable peelable = (Peelable) proxy;
        peelable.peel();

        Washable washable = (Washable) proxy;
        washable.wash();

        PeelableComponent pComp = (PeelableComponent) pico.getComponent(PeelableComponent.class);
        PeelableAndWashableComponent peelNWash = (PeelableAndWashableComponent) pico.getComponent(PeelableAndWashableComponent.class);

        assertTrue(pComp.wasPeeled);
        assertTrue(peelNWash.wasWashed);

    }

    public void testBasicComponentInteraction() throws PicoInitializationException, PicoRegistrationException
    {
        DefaultPicoContainer pico = new DefaultPicoContainer.Default();

        pico.registerComponent(FredImpl.class);
        pico.registerComponent(WilmaImpl.class);

        pico.instantiateComponents();

        WilmaImpl wilma = (WilmaImpl) pico.getComponent(WilmaImpl.class);

        assertTrue("hello should have been called in wilma", wilma.helloCalled());
    }

}
