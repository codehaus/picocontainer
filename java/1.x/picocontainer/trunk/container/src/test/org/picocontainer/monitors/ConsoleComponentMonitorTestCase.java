package org.picocontainer.monitors;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.picocontainer.ComponentMonitor;

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @version $Revision: 2024 $
 */
public class ConsoleComponentMonitorTestCase extends TestCase {
    private PrintStream out;
    private ComponentMonitor componentMonitor;
    private Constructor constructor;
    private Method method;

    protected void setUp() throws Exception {
        out = System.out;
        constructor = getClass().getConstructor((Class[])null);
        method = getClass().getDeclaredMethod("setUp", (Class[])null);
        componentMonitor = new ConsoleComponentMonitor(out);
    }

    public void testShouldTraceInstantiating() {
        componentMonitor.instantiating(constructor);
    }

    public void testShouldTraceInstantiated() {
        componentMonitor.instantiated(constructor, 543);
    }

    public void testShouldTraceInstantiatedWithInjected() {
        componentMonitor.instantiated(constructor, new Object(), new Object[0], 543);
    }

    public void testShouldTraceInstantiationFailed() {
        componentMonitor.instantiationFailed(constructor, new RuntimeException("doh"));
    }

    public void testShouldTraceInvoking() {
        componentMonitor.invoking(method, this);
    }

    public void testShouldTraceInvoked() {
        componentMonitor.invoked(method, this, 543);
    }

    public void testShouldTraceInvocatiationFailed() {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
    }

}
