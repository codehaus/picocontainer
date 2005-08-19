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
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATING, new Object[]{constructor}) +NL,  out.toString());
    }

    public void testShouldTraceInstantiated() {
        componentMonitor.instantiated(constructor, 543);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATED, new Object[]{constructor, new Long(543)}) +NL,  out.toString());
    }

    public void testShouldTraceInstantiationFailed() {
        componentMonitor.instantiationFailed(constructor, new RuntimeException("doh"));
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATION_FAILED, new Object[]{constructor, "doh"}) +NL,  out.toString());
    }

    public void testShouldTraceInvoking() {
        componentMonitor.invoking(method, this);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INVOKING, new Object[]{method, this}) +NL,  out.toString());
    }

    public void testShouldTraceInvoked() {
        componentMonitor.invoked(method, this, 543);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INVOKED, new Object[]{method, this, new Long(543)}) +NL,  out.toString());
    }

    public void testShouldTraceInvocatiationFailed() {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INVOCATION_FAILED, new Object[]{method, this, "doh"}) +NL,  out.toString());
    }

}
