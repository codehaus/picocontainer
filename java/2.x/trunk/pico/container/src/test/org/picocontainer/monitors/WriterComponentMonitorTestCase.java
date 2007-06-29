package org.picocontainer.monitors;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoLifecycleException;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.TransientPicoContainer;

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
        componentMonitor.instantiating(null, null, constructor);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATING, AbstractComponentMonitor.toString(constructor)) +NL,  out.toString());
    }

    public void testShouldTraceInstantiatedWithInjected() {
        Object[] injected = new Object[0];
        Object instantiated = new Object();
        componentMonitor.instantiated(null, null, constructor, instantiated, injected, 543);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATED2,
                                                   AbstractComponentMonitor.toString(constructor),
                                                   (long)543,
                                                   instantiated.getClass().getName(), WriterComponentMonitor.toString(injected)) +NL,  out.toString());
    }


    public void testShouldTraceInstantiationFailed() {
        componentMonitor.instantiationFailed(null, null, constructor, new RuntimeException("doh"));
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INSTANTIATION_FAILED,
                                                   AbstractComponentMonitor.toString(constructor), "doh") +NL,  out.toString());
    }

    public void testShouldTraceInvoking() {
        componentMonitor.invoking(null, null, method, this);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INVOKING,
                                                   AbstractComponentMonitor.toString(method), this) +NL,  out.toString());
    }

    public void testShouldTraceInvoked() {
        componentMonitor.invoked(null, null, method, this, 543);
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INVOKED,
                                                   AbstractComponentMonitor.toString(method), this,
                                                   (long)543) +NL,  out.toString());
    }

    public void testShouldTraceInvocatiationFailed() {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.INVOCATION_FAILED,
                                                   AbstractComponentMonitor.toString(method), this, "doh") +NL,  out.toString());
    }

        public void testShouldTraceLifecycleInvocationFailed() {
        try {
            componentMonitor.lifecycleInvocationFailed(new TransientPicoContainer(), new AbstractAdapter(Map.class, HashMap.class){
                public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
                    return "x";
                }
                public void verify(PicoContainer container) throws PicoCompositionException {
                }
            }, method, "fooooo", new RuntimeException("doh"));
            fail("should have barfed");
        } catch (PicoLifecycleException e) {
            //expected
        }
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.LIFECYCLE_INVOCATION_FAILED,
                                                   AbstractComponentMonitor.toString(method), "fooooo", "doh") +NL,  out.toString());
    }

    public void testNoComponent() {
        
        componentMonitor.noComponent(new TransientPicoContainer(), "foo");
        assertEquals(WriterComponentMonitor.format(WriterComponentMonitor.NO_COMPONENT,
                                                   "foo") +NL,  out.toString());
    }


}
