package org.picocontainer;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.MarshallingStrategy;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.alternatives.EmptyPicoContainer;
import org.picocontainer.gems.*;

import java.util.HashMap;

public class PicoBuilderTestCase extends TestCase {

    XStream xs;

    protected void setUp() throws Exception {
        xs = new XStream();
        xs.alias("PICO", DefaultPicoContainer.class);
        xs.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
    }

    public void testBasic() {
        MutablePicoContainer mpc = new PicoBuilder().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithStartableLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withLifecycle().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.StartableLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "  lifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/PICO/lifecycleStrategy/componentMonitor\n" +
                "PICO",foo);
    }

    public void testWithReflectionLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withReflectionLifecycle().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.ReflectionLifecycleStrategy\n" +
                "    methodNames\n" +
                "      stringstartstring\n" +
                "      stringstopstring\n" +
                "      stringdisposestring\n" +
                "    methodNames\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "  lifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/PICO/lifecycleStrategy/componentMonitor\n" +
                "PICO",foo);
    }


    public void testWithConsoleMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withConsoleMonitor().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "  componentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCustomMonitorByClass() {
        MutablePicoContainer mpc = new PicoBuilder().withMonitor(ConsoleComponentMonitor.class).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "  componentMonitor\n" +
                "PICO",foo);
    }
    
    public void testWithBogusCustomMonitorByClass() {
        try {
            new PicoBuilder().withMonitor(HashMap.class).build();
            fail("should have barfed");
        } catch (AssignabilityRegistrationException e) {
            // expected
        }
    }

    public void testWithImplementationHiding() {
        MutablePicoContainer mpc = new PicoBuilder().withHiddenImplementations().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory\n" +
                "    delegate=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);    }

    public static class CustomParentcontainer extends EmptyPicoContainer {}

    public void testWithCustomParentContainer() {
        MutablePicoContainer mpc = new PicoBuilder(new CustomParentcontainer()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.PicoBuilderTestCase_CustomParentcontainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);    }

    public void testWithBogusParentContainer() {
        try {
            new PicoBuilder(null).build();
            fail("should have barfed");
        } catch (NullPointerException e) {
            //expected
        }
    }


    public void testWithSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.SetterInjectionComponentAdapterFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);    }

    public void testWithAnnotationDI() {
        MutablePicoContainer mpc = new PicoBuilder().withAnnotationInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnnotationInjectionComponentAdapterFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCtorDI() {
        MutablePicoContainer mpc = new PicoBuilder().withConstructorInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.ConstructorInjectionComponentAdapterFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithImplementationHidingAndSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory\n" +
                "    delegate=org.picocontainer.adapters.SetterInjectionComponentAdapterFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCachingImplementationHidingAndSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withCaching().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.CachingComponentAdapterFactory\n" +
                "    delegate=org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory\n" +
                "      delegate=org.picocontainer.adapters.SetterInjectionComponentAdapterFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithThreadSafety() {
        MutablePicoContainer mpc = new PicoBuilder().withThreadSafety().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.SynchronizedComponentAdapterFactory\n" +
                "    delegate=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }





    private String simplifyRepresentation(MutablePicoContainer mpc) {
        String foo = xs.toXML(mpc);
        foo = foo.replace('$','_');
        foo = foo.replaceAll("/>","");
        foo = foo.replaceAll("</","");
        foo = foo.replaceAll("<","");
        foo = foo.replaceAll(">","");
        foo = foo.replaceAll("\n  childrenStarted","");
        foo = foo.replaceAll("\n  componentAdapters","");
        foo = foo.replaceAll("\n  orderedComponentAdapters","");
        foo = foo.replaceAll("\n  startedfalsestarted","");
        foo = foo.replaceAll("\n  disposedfalsedisposed","");
        foo = foo.replaceAll("\n  handler","");
        foo = foo.replaceAll("\n  children","");
        foo = foo.replaceAll("\n  delegate\n","\n");
        foo = foo.replaceAll("\n    delegate\n","\n");
        foo = foo.replaceAll("\n    outer-class reference=\"/PICO\"","");
        foo = foo.replaceAll("\n  componentCharacteristic class=\"org.picocontainer.defaults.DefaultPicoContainer$1\"","");
        foo = foo.replaceAll("\n  componentCharacteristic","");
        foo = foo.replaceAll("\n  componentKeyToAdapterCache","");
        foo = foo.replaceAll("\n    startedComponentAdapters","");
        foo = foo.replaceAll("\n    props","");
        foo = foo.replaceAll("\"class=","\"\nclass=");
        foo = foo.replaceAll("\n  componentAdapterFactory\n","\n");
        foo = foo.replaceAll("\n  lifecycleManager","");
        foo = foo.replaceAll("class=\"org.picocontainer.defaults.DefaultPicoContainer_1\"","");
        foo = foo.replaceAll("class=\"org.picocontainer.defaults.DefaultPicoContainer_OrderedComponentAdapterLifecycleManager\"","");
        foo = foo.replaceAll("class=","=");
        foo = foo.replaceAll("\"","");
        foo = foo.replaceAll(" \n","\n");
        foo = foo.replaceAll(" =","=");
        foo = foo.replaceAll("\n\n","\n");

        return foo;
    }


}
