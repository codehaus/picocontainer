/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.hierarchical;

import junit.framework.TestCase;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.defaults.NullContainer;
import picocontainer.defaults.NullLifecycleManager;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoStartException;

import java.util.ArrayList;
import java.util.List;

public class MorphingHierarchicalPicoContainerTestCase extends TestCase {

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

        public PeelableComponent() {}

        public void peel() {
            wasPeeled = true;
        }
    }

    public static class NotReallyPeelableComponent {
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
            String niceName = name.substring(name.lastIndexOf("$")+1);
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
        }
        public void wash() {
            recorder.record("B.wash()");
        }
    }

    public static class C extends RecordingAware implements Washable {
        public C(Recorder recorder, A a, B b) {
            super(recorder);
        }
        public void wash() {
            recorder.record("C.wash()");
        }
    }

    public void setUp() throws PicoRegistrationException, PicoStartException {


    }

    public void testApplyInterfaceMethodsToWholeContainer() throws PicoRegistrationException, PicoStartException {

        MorphingHierarchicalPicoContainer pico = new MorphingHierarchicalPicoContainer(new NullContainer(), new NullLifecycleManager(), new DefaultComponentFactory());
        pico.registerComponent(PeelableComponent.class);
        pico.registerComponent(NotReallyPeelableComponent.class);
        pico.start();

        assertEquals(2, pico.getComponents().length);

        Peelable myPeelableContainer = (Peelable) pico.as(Peelable.class);

        myPeelableContainer.peel();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponent(PeelableComponent.class);
        NotReallyPeelableComponent notReallyPeelableComponent =
                (NotReallyPeelableComponent) pico.getComponent(NotReallyPeelableComponent.class);

        assertTrue(peelableComponent.wasPeeled);
        assertFalse(notReallyPeelableComponent.wasPeeled);
    }

    public void testWorksWithMultipleInterfaces() throws PicoRegistrationException, PicoStartException {

        MorphingHierarchicalPicoContainer pico =
                new MorphingHierarchicalPicoContainer(new NullContainer(), new NullLifecycleManager(), new DefaultComponentFactory());

        pico.registerComponent(PeelableComponent.class);
        pico.registerComponent(NotReallyPeelableComponent.class);
        pico.registerComponent(PeelableAndWashableComponent.class);

        pico.start();

        Object myPeelableAndWashableContainer = pico.as(new Class[] { Peelable.class, Washable.class });

        ((Washable)myPeelableAndWashableContainer).wash();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponent(PeelableComponent.class);
        NotReallyPeelableComponent notReallyPeelableComponent =
                (NotReallyPeelableComponent) pico.getComponent(NotReallyPeelableComponent.class);
        PeelableAndWashableComponent washAndPeel =
                (PeelableAndWashableComponent) pico.getComponent(PeelableAndWashableComponent.class);;

        assertFalse(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);

        ((Peelable)myPeelableAndWashableContainer).peel();

        assertTrue(peelableComponent.wasPeeled);
        assertTrue(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);
        assertFalse(notReallyPeelableComponent.wasPeeled);

     }

    public void testAsCallsAllComponents() throws PicoRegistrationException, PicoStartException {

        MorphingHierarchicalPicoContainer pico =
                new MorphingHierarchicalPicoContainer(new NullContainer(), new NullLifecycleManager(), new DefaultComponentFactory());

        //an unmanaged component
        pico.registerComponent(PeelableComponent.class, new PeelableComponent());

        //some managed ones
        pico.registerComponent(NotReallyPeelableComponent.class);
        pico.registerComponent(PeelableAndWashableComponent.class);

        pico.start();

        Object myPeelableAndWashableContainer = pico.as(new Class[] { Peelable.class, Washable.class });

        ((Washable)myPeelableAndWashableContainer).wash();

        PeelableComponent peelableComponent =
                (PeelableComponent) pico.getComponent(PeelableComponent.class);
        NotReallyPeelableComponent notReallyPeelableComponent =
                (NotReallyPeelableComponent) pico.getComponent(NotReallyPeelableComponent.class);
        PeelableAndWashableComponent washAndPeel =
                (PeelableAndWashableComponent) pico.getComponent(PeelableAndWashableComponent.class);;

        assertFalse(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);

        ((Peelable)myPeelableAndWashableContainer).peel();

        assertTrue(peelableComponent.wasPeeled);
        assertTrue(washAndPeel.wasPeeled);
        assertTrue(washAndPeel.wasWashed);
        assertFalse(notReallyPeelableComponent.wasPeeled);

     }

    public void testLifecycleCallsComponentsInReverseOrder() throws PicoRegistrationException, PicoStartException {

        MorphingHierarchicalPicoContainer pico =
                new MorphingHierarchicalPicoContainer(new NullContainer(), new NullLifecycleManager(), new DefaultComponentFactory());

        Recorder recorder = new Recorder();

        pico.registerComponent(Recorder.class, recorder);
        pico.registerComponent(A.class, A.class);
        pico.registerComponent(B.class, B.class);
        pico.registerComponent(C.class, C.class);

        pico.start();

        assertEquals("instantiated A", recorder.getWhatHappened(0));
        assertEquals("instantiated B", recorder.getWhatHappened(1));
        assertEquals("instantiated C", recorder.getWhatHappened(2));

        recorder.clear();

        Object washableContainer =
                pico.asLifecycle(Washable.class, MorphingHierarchicalPicoContainer.REVERSE_INSTANTIATION_ORDER);

        ((Washable)washableContainer).wash();

        assertEquals("C.wash()", recorder.getWhatHappened(0));
        assertEquals("B.wash()", recorder.getWhatHappened(1));
        assertEquals("A.wash()", recorder.getWhatHappened(2));

     }

    public void testAsLifecycleOnlyCallsManagedComponents() throws PicoRegistrationException, PicoStartException {

        MorphingHierarchicalPicoContainer pico =
                new MorphingHierarchicalPicoContainer(new NullContainer(), new NullLifecycleManager(), new DefaultComponentFactory());

        Recorder recorder = new Recorder();

        A unmanagedComponent = new A(recorder);

        recorder.clear();

        pico.registerComponent(Recorder.class, recorder);
        pico.registerComponent(A.class, unmanagedComponent);
        pico.registerComponent(B.class);
        pico.registerComponent(C.class);

        pico.start();

        assertEquals("instantiated B", recorder.getWhatHappened(0));
        assertEquals("instantiated C", recorder.getWhatHappened(1));

        recorder.clear();

        Object washableContainer =
                pico.asLifecycle(Washable.class, MorphingHierarchicalPicoContainer.REVERSE_INSTANTIATION_ORDER);

        ((Washable)washableContainer).wash();

        assertEquals("C.wash()", recorder.getWhatHappened(0));
        assertEquals("B.wash()", recorder.getWhatHappened(1));
        assertTrue(
                "Unmanaged components should not be called by an asLifecycle() proxy",
                !recorder.thingsThatHappened.contains("A.wash()"));
     }


}
