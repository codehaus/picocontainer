/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleManager;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.monitors.NullComponentMonitor;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;


/**
 * @author J&ouml;rg Schaible
 * @author Aslak Helles&oslash;y
 */
public class CustomLifecycleManagerTest extends MockObjectTestCase {

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

        public void throwsAtCall() throws FileNotFoundException {
            throw new FileNotFoundException();
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

    public void testShouldApplyLifecylceInInstantiationOrder() throws NoSuchMethodException {
        CustomLifecycleManager lifecylceManager = new CustomLifecycleManager(RecordingLifecycle.class.getMethod(
                "demarrer", null), RecordingLifecycle.class.getMethod("arreter", null), RecordingLifecycle.class
                .getMethod("ecraser", null), new NullComponentMonitor());

        MutablePicoContainer parent = new DefaultPicoContainer(lifecylceManager);
        MutablePicoContainer child = parent.makeChildContainer();
        parent.registerComponentImplementation("recording", StringBuffer.class);
        child.registerComponentImplementation(Four.class);
        parent.registerComponentImplementation(Two.class);
        parent.registerComponentImplementation(One.class, One.class, new Parameter[]{ComponentParameter.DEFAULT});
        child.registerComponentImplementation(Three.class);

        parent.start();
        parent.stop();
        parent.dispose();

        assertEquals("<One<Two<Three<FourFour>Three>Two>One>!Four!Three!Two!One", parent.getComponentInstance(
                "recording").toString());
    }

    public void testNullPointerExceptionForNullAsParameter() throws NoSuchMethodException {
        Method uncallable = RecordingLifecycle.class.getMethod("uncallableByVisitor", new Class[]{String.class});
        try {
            new CustomLifecycleManager(uncallable, uncallable, null, new NullComponentMonitor());
            fail("Thrown " + NullPointerException.class.getName() + " expected");
        } catch (final NullPointerException e) {
            // ok
        }
        try {
            new CustomLifecycleManager(uncallable, uncallable, uncallable, null);
            fail("Thrown " + NullPointerException.class.getName() + " expected");
        } catch (final NullPointerException e) {
            // ok
        }
    }

    public void testIllegalArgumentExceptionForInvalidMethod() throws NoSuchMethodException {
        Method uncallable = RecordingLifecycle.class.getMethod("uncallableByVisitor", new Class[]{String.class});
        try {
            new CustomLifecycleManager(uncallable, uncallable, uncallable, new NullComponentMonitor());
            fail("Thrown " + IllegalArgumentException.class.getName() + " expected");
        } catch (final IllegalArgumentException e) {
            // ok
        }
    }

    public void testIsSerializable() throws NoSuchMethodException, IOException, ClassNotFoundException {
        LifecycleManager lifecylceManager = new CustomLifecycleManager(RecordingLifecycle.class.getMethod(
                "demarrer", null), RecordingLifecycle.class.getMethod("arreter", null), RecordingLifecycle.class
                .getMethod("ecraser", null), new NullComponentMonitor());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(buffer);
        out.writeObject(lifecylceManager);
        out.close();
        LifecycleManager serializedLifecylceManager = (LifecycleManager)new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray())).readObject();

        MutablePicoContainer parent = new DefaultPicoContainer(serializedLifecylceManager);
        parent.registerComponentImplementation("recording", StringBuffer.class);
        parent.registerComponentImplementation(One.class);

        parent.start();
        parent.stop();
        parent.dispose();

        assertEquals("<OneOne>!One", parent.getComponentInstance("recording").toString());
    }

    public void testSupportComponentMonitor() throws Exception {
        Mock mockComponentMonitor = mock(ComponentMonitor.class);

        Method demarrerMethod = RecordingLifecycle.class.getMethod("demarrer", null);
        Method throwsAtCallMethod = RecordingLifecycle.class.getMethod("throwsAtCall", null);
        LifecycleManager lifecylceManager = new CustomLifecycleManager(
                demarrerMethod, throwsAtCallMethod, RecordingLifecycle.class.getMethod("ecraser", null),
                (ComponentMonitor)mockComponentMonitor.proxy());

        MutablePicoContainer parent = new DefaultPicoContainer(lifecylceManager);
        parent.registerComponentImplementation("recording", StringBuffer.class);
        parent.registerComponentImplementation(One.class);

        mockComponentMonitor.expects(once()).method("invoking").with(same(demarrerMethod), isA(One.class));
        mockComponentMonitor.expects(once()).method("invoked").with(
                same(demarrerMethod), isA(One.class), isA(Long.class));
        mockComponentMonitor.expects(once()).method("invoking").with(same(throwsAtCallMethod), isA(One.class));
        mockComponentMonitor.expects(once()).method("invocationFailed").with(
                same(throwsAtCallMethod), isA(One.class), isA(Exception.class));

        try {
            parent.start();
            parent.stop();
            fail("Thrown " + PicoInitializationException.class.getName() + " expected");
        } catch (final PicoInitializationException e) {
            // ok
        }
    }
}
