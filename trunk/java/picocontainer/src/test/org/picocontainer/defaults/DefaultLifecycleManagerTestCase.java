/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.picocontainer.defaults.ComponentMonitor;
import org.picocontainer.Disposable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;

import java.lang.reflect.Method;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class DefaultLifecycleManagerTestCase extends MockObjectTestCase {

    public void testStartingHappens() throws NoSuchMethodException {
        Mock monitor = mock(ComponentMonitor.class);
        DefaultLifecycleManager lifecycleManager = new DefaultLifecycleManager((ComponentMonitor) monitor.proxy());
        MutablePicoContainer pico = new DefaultPicoContainer(lifecycleManager);
        pico.registerComponentImplementation(LifecycleTestComponent.class);

        Constraint lifecycleTestComponentNotStarted = new Constraint() {
            public boolean eval(Object o) {
                LifecycleTestComponent ltc = (LifecycleTestComponent) o;
                return ltc.started == false;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Lifecycle Test Component Should Not Be Started");
            }
        };

        Constraint lifecycleTestComponentStarted = new Constraint() {
            public boolean eval(Object o) {
                LifecycleTestComponent ltc = (LifecycleTestComponent) o;
                return ltc.started == true;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Lifecycle Test Component Should Be Started");
            }
        };

        Constraint durationNonZero = new Constraint() {
            public boolean eval(Object o) {
                Long duration = (Long) o;
                return duration.longValue() >= 0;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Duration Should be Non Zero");
            }
        };

        Method startMethod = Startable.class.getMethod("start", new Class[0]);
        monitor.expects(once()).method("invoking").with(eq(startMethod), lifecycleTestComponentNotStarted);
        monitor.expects(once()).method("invoked").with(eq(startMethod), lifecycleTestComponentStarted, durationNonZero);

        pico.start();

        LifecycleTestComponent ltc = (LifecycleTestComponent) pico.getComponentInstance(LifecycleTestComponent.class);

        assertTrue("Test Component Not Started", ltc.started);
    }


    public static class LifecycleTestComponent implements Startable, Disposable {
        boolean started, stopped, disposed;

        public LifecycleTestComponent() {
        }

        public void start() {
            started = true;
        }

        public void stop() {
            stopped = true;
        }

        public void dispose() {
            disposed = true;
        }
    }

}
