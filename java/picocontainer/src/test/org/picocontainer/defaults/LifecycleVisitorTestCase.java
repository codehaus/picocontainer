package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.io.FileNotFoundException;


/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class LifecycleVisitorTestCase
        extends MockObjectTestCase {

    public abstract static class RecordingLifecycle {
        private final StringBuffer recording;

        protected RecordingLifecycle(StringBuffer recording) {
            this.recording = recording;
        }

        public void demarrer() {
            recording.append("<" + code());
        }

        public void arreter() {
            recording.append(code() + ">");
        }

        public void ecraser() {
            recording.append("!" + code());
        }

        private String code() {
            String name = getClass().getName();
            return name.substring(name.indexOf('$') + 1);
        }

        public void uncallableByVisitor(final String s) {
        }

        public void throwsAtVisit() throws FileNotFoundException {
            throw new FileNotFoundException();
        }

        private void callMe() {
        }
    }

    public static class One
            extends RecordingLifecycle {
        public One(StringBuffer sb) {
            super(sb);
        }
    }

    public static class Two
            extends RecordingLifecycle {
        public Two(StringBuffer sb, One one) {
            super(sb);
            assertNotNull(one);
        }
    }

    public static class Three
            extends RecordingLifecycle {
        public Three(StringBuffer sb, One one, Two two) {
            super(sb);
            assertNotNull(one);
            assertNotNull(two);
        }
    }

    public static class Four
            extends RecordingLifecycle {
        public Four(StringBuffer sb, Two two, Three three, One one) {
            super(sb);
            assertNotNull(one);
            assertNotNull(two);
            assertNotNull(three);
        }
    }

    public void testShouldAllowCustomLifecycle() throws NoSuchMethodException {
        LifecycleVisitor starter = new LifecycleVisitor(
                RecordingLifecycle.class.getMethod("demarrer", null), RecordingLifecycle.class, true);
        LifecycleVisitor stopper = new LifecycleVisitor(
                RecordingLifecycle.class.getMethod("arreter", null), RecordingLifecycle.class, false);
        LifecycleVisitor disposer = new LifecycleVisitor(
                RecordingLifecycle.class.getMethod("ecraser", null), RecordingLifecycle.class, false);

        MutablePicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = parent.makeChildContainer();
        parent.registerComponentImplementation("recording", StringBuffer.class);
        child.registerComponentImplementation(Four.class);
        parent.registerComponentImplementation(Two.class);
        parent.registerComponentImplementation(One.class);
        child.registerComponentImplementation(Three.class);

        parent.accept(starter);
        parent.accept(stopper);
        parent.accept(disposer);

        assertEquals("<One<Two<Three<FourFour>Three>Two>One>!Four!Three!Two!One", parent.getComponentInstance("recording")
                .toString());
    }

    // TODO: Aslak, this fails, since DPC's parent is *always* an ImmutablePC
    public void XXXtestDisposeRemovesMutablePicoContainerAsChild() {
        Mock mutableParentMock = mock(MutablePicoContainer.class);
        MutablePicoContainer pico = new DefaultPicoContainer();
        mutableParentMock.expects(once()).method("removeChildContainer").with(same(pico));
        pico.addChildContainer((PicoContainer) mutableParentMock.proxy());
        LifecycleVisitor.DISPOSER.visitContainer(pico);
    }

    public void testPicoIntrospectionExceptionForInvalidMethod() throws NoSuchMethodException {
        LifecycleVisitor visitor = new LifecycleVisitor(RecordingLifecycle.class.getMethod(
                "uncallableByVisitor", new Class[]{String.class}), RecordingLifecycle.class, true);
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(StringBuffer.class);
        pico.registerComponentImplementation(One.class);
        try {
            pico.accept(visitor);
            fail("PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    public void testPicoIntrospectionExceptionForThrownException() throws NoSuchMethodException {
        LifecycleVisitor visitor = new LifecycleVisitor(
                RecordingLifecycle.class.getMethod("throwsAtVisit", null), RecordingLifecycle.class, true);
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(StringBuffer.class);
        pico.registerComponentImplementation(One.class);
        try {
            pico.accept(visitor);
            fail("PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getCause() instanceof FileNotFoundException);
        }
    }

    public void testPicoIntrospectionExceptionForInaccessibleMethod() throws NoSuchMethodException {
        LifecycleVisitor visitor = new LifecycleVisitor(
                RecordingLifecycle.class.getDeclaredMethod("callMe", null), RecordingLifecycle.class, true);
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(StringBuffer.class);
        pico.registerComponentImplementation(One.class);
        try {
            pico.accept(visitor);
            fail("PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            assertTrue(e.getCause() instanceof IllegalAccessException);
        }
    }
}