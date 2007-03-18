package org.picocontainer.monitors;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.picocontainer.ComponentMonitor;

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @version $Revision$
 */
public class WriterComponentMonitorTestCase extends TestCase {
    private Writer out;
    private ComponentMonitor componentMonitor;
    private static final String NL = System.getProperty("line.separator");
    private Constructor constructor;
    private Method method;

    protected void setUp() throws Exception {
        out = new StringWriter();
        constructor = getClass().getConstructor((Class[])null);
        method = getClass().getDeclaredMethod("setUp", (Class[])null);
        componentMonitor = new WriterComponentMonitor(out);
    }

    public void testShouldTraceInstantiating() {
        componentMonitor.instantiating(constructor);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATING, new Object[]{AbstractComponentMonitor.toString(constructor)}) +NL,  out.toString());
    }

    public void testShouldTraceInstantiated() {
        componentMonitor.instantiated(constructor, 543);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATED, new Object[]{AbstractComponentMonitor.toString(constructor), new Long(543)}) +NL,  out.toString());
    }

    public void testShouldTraceInstantiatedWithInjected() {
        Object[] injected = new Object[0];
        Object instantiated = new Object();
        componentMonitor.instantiated(constructor, instantiated, injected, 543);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATED2, new Object[]{AbstractComponentMonitor.toString(constructor), new Long(543), instantiated.getClass().getName(), WriterComponentMonitor.toString(injected)}) +NL,  out.toString());
    }


    public void testShouldTraceInstantiationFailed() {
        componentMonitor.instantiationFailed(constructor, new RuntimeException("doh"));
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATION_FAILED, new Object[]{AbstractComponentMonitor.toString(constructor), "doh"}) +NL,  out.toString());
    }

    public void testShouldTraceInvoking() {
        componentMonitor.invoking(method, this);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INVOKING, new Object[]{AbstractComponentMonitor.toString(method), this}) +NL,  out.toString());
    }

    public void testShouldTraceInvoked() {
        componentMonitor.invoked(method, this, 543);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INVOKED, new Object[]{AbstractComponentMonitor.toString(method), this, new Long(543)}) +NL,  out.toString());
    }

    public void testShouldTraceInvocatiationFailed() {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INVOCATION_FAILED, new Object[]{AbstractComponentMonitor.toString(method), this, "doh"}) +NL,  out.toString());
    }

}
