package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.defaults.CustomLifecycleManagerFactory;
import org.picocontainer.MutablePicoContainer;

import java.lang.reflect.Method;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CustomLifecycleManagerTestCase extends TestCase {
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
    }

    public static class One extends RecordingLifecycle {
        public One(StringBuffer sb) {
            super(sb);
        }
    }

    public static class Two extends RecordingLifecycle {
        public Two(StringBuffer sb, One one) {
            super(sb);
            assertNotNull(one);
        }
    }

    public static class Three extends RecordingLifecycle {
        public Three(StringBuffer sb, One one, Two two) {
            super(sb);
            assertNotNull(one);
            assertNotNull(two);
        }
    }

    public static class Four extends RecordingLifecycle {
        public Four(StringBuffer sb, Two two, Three three, One one) {
            super(sb);
            assertNotNull(one);
            assertNotNull(two);
            assertNotNull(three);
        }
    }
    public void testShouldAllowCustomLifecycle() throws Exception {
        Method start = RecordingLifecycle.class.getMethod("demarrer", null);
        Method stop = RecordingLifecycle.class.getMethod("arreter", null);
        Method dispose = RecordingLifecycle.class.getMethod("ecraser", null);

        CustomLifecycleManagerFactory lmf = new CustomLifecycleManagerFactory(start, stop, dispose);
        MutablePicoContainer parent = new DefaultPicoContainer(lmf);
        MutablePicoContainer child = parent.makeChildContainer();
        parent.registerComponentImplementation("recording", StringBuffer.class);
        child.registerComponentImplementation(Four.class);
        parent.registerComponentImplementation(Two.class);
        parent.registerComponentImplementation(One.class);
        child.registerComponentImplementation(Three.class);

        parent.getLifecycleManager().start();
        parent.getLifecycleManager().stop();
        parent.getLifecycleManager().dispose();

        assertEquals("<One<Two<Three<FourFour>Three>Two>One>!Four!Three!Two!One", parent.getComponentInstance("recording").toString());
    }

}