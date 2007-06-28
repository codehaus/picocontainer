package org.picocontainer.monitors;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.picocontainer.defaults.ComponentMonitor;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ConsoleComponentMonitorTestCase extends TestCase {
    private Writer out;
    private ComponentMonitor componentMonitor;
    private static final String NL = System.getProperty("line.separator");
    private Constructor constructor;
    private Method method;

    protected void setUp() throws Exception {
        out = new StringWriter();
        constructor = getClass().getConstructor(null);
        method = getClass().getDeclaredMethod("setUp", null);
        componentMonitor = new ConsoleComponentMonitor(out);
    }

    public void testShouldTraceInstantiating() throws NoSuchMethodException {
        componentMonitor.instantiating(constructor);
        assertEquals("PicoContainer: instantiating public org.picocontainer.monitors.ConsoleComponentMonitorTestCase()" + NL, out.toString());
    }

    public void testShouldTraceInstantiated() throws NoSuchMethodException {
        componentMonitor.instantiated(constructor, 1234, 543);
        assertEquals("PicoContainer: instantiated public org.picocontainer.monitors.ConsoleComponentMonitorTestCase() [543ms]" + NL, out.toString());
    }

    public void testShouldTraceInstantiationFailed() throws NoSuchMethodException {
        componentMonitor.instantiationFailed(constructor, new RuntimeException("doh"));
        assertEquals("PicoContainer: instantiation failed: public org.picocontainer.monitors.ConsoleComponentMonitorTestCase(), reason: 'doh'" + NL, out.toString());
    }

    public void testShouldTraceInvoking() throws NoSuchMethodException {
        componentMonitor.invoking(method, this);
        assertEquals("PicoContainer: invoking protected void org.picocontainer.monitors.ConsoleComponentMonitorTestCase.setUp() throws java.lang.Exception on Blah" + NL, out.toString());
    }

    public void testShouldTraceInvoked() throws NoSuchMethodException {
        componentMonitor.invoked(method, this, 543);
        assertEquals("PicoContainer: invoked protected void org.picocontainer.monitors.ConsoleComponentMonitorTestCase.setUp() throws java.lang.Exception on Blah [543ms]" + NL, out.toString());
    }

    public void testShouldTraceInvocatiationFailed() throws NoSuchMethodException {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertEquals("PicoContainer: invocation failed: protected void org.picocontainer.monitors.ConsoleComponentMonitorTestCase.setUp() throws java.lang.Exception on Blah, reason: 'doh'" + NL, out.toString());
    }

    public String toString() {
        return "Blah";
    }
}
