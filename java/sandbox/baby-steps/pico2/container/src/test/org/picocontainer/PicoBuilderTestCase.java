package org.picocontainer;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentFactory;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.alternatives.EmptyPicoContainer;
import org.picocontainer.adapters.ImplementationHidingBehaviorFactory;

import static org.picocontainer.PicoBuilder.SDI;
import static org.picocontainer.PicoBuilder.IMPL_HIDING;
import static org.picocontainer.PicoBuilder.CACHING;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class PicoBuilderTestCase extends TestCase {

    XStream xs;

    protected void setUp() throws Exception {
        xs = new XStream();
        xs.alias("PICO", DefaultPicoContainer.class);
        xs.registerConverter(new Converter() {
            public boolean canConvert(Class aClass) {
                return aClass.getName().equals("org.picocontainer.defaults.DefaultPicoContainer$OrderedComponentAdapterLifecycleManager") ||
                       aClass.getName().equals("org.picocontainer.defaults.DefaultPicoContainer$1") ||
                       aClass == Boolean.class ||
                       aClass == HashSet.class ||
                       aClass == ArrayList.class;
            }

            public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
            }

            public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
                return null;
            }
        });
        xs.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
    }

    public void testBasic() {
        MutablePicoContainer mpc = new PicoBuilder().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionFactory\n" +
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
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionFactory\n" +
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
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionFactory\n" +
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
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionFactory\n" +
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
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionFactory\n" +
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
                "  componentAdapterFactory=org.picocontainer.adapters.ImplementationHidingBehaviorFactory\n" +
                "    delegate=org.picocontainer.adapters.AnyInjectionFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithImplementationHidingInstance() {
        MutablePicoContainer mpc = new PicoBuilder().withComponentAdapterFactory(new ImplementationHidingBehaviorFactory()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.ImplementationHidingBehaviorFactory\n" +
                "    delegate=org.picocontainer.adapters.AnyInjectionFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCafsListChainThingy() {
        MutablePicoContainer mpc = new PicoBuilder(SDI()).withBehaviors(CACHING(), IMPL_HIDING()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.CachingBehaviorFactory\n" +
                "    delegate=org.picocontainer.adapters.ImplementationHidingBehaviorFactory\n" +
                "      delegate=org.picocontainer.adapters.SetterInjectionFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }


    public static class CustomParentcontainer extends EmptyPicoContainer {}

    public void testWithCustomParentContainer() {
        MutablePicoContainer mpc = new PicoBuilder(new CustomParentcontainer()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.PicoBuilderTestCase_CustomParentcontainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithBogusParentContainer() {
        try {
            new PicoBuilder((PicoContainer)null).build();
            fail("should have barfed");
        } catch (NullPointerException e) {
            //expected
        }
    }


    public void testWithSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.SetterInjectionFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithAnnotationDI() {
        MutablePicoContainer mpc = new PicoBuilder().withAnnotationInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnnotationInjectionFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCtorDI() {
        MutablePicoContainer mpc = new PicoBuilder().withConstructorInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.ConstructorInjectionFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithImplementationHidingAndSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.ImplementationHidingBehaviorFactory\n" +
                "    delegate=org.picocontainer.adapters.SetterInjectionFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCachingImplementationHidingAndSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withCaching().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.CachingBehaviorFactory\n" +
                "    delegate=org.picocontainer.adapters.ImplementationHidingBehaviorFactory\n" +
                "      delegate=org.picocontainer.adapters.SetterInjectionFactory\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithThreadSafety() {
        MutablePicoContainer mpc = new PicoBuilder().withThreadSafety().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.SynchronizedBehaviorFactory\n" +
                "    delegate=org.picocontainer.adapters.AnyInjectionFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCustomPicoContainer() {
        MutablePicoContainer mpc = new PicoBuilder().thisMutablePicoContainer(TestPicoContainer.class).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("org.picocontainer.PicoBuilderTestCase_-TestPicoContainer\n" +
                "  componentAdapterFactory=org.picocontainer.adapters.AnyInjectionFactory\n" +
                "    cdiDelegate\n" +
                "    sdiDelegate\n" +
                "  parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "org.picocontainer.PicoBuilderTestCase_-TestPicoContainer",foo);
    }


    public static class TestPicoContainer extends DefaultPicoContainer {
        public TestPicoContainer(ComponentFactory caf, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
            super(caf, monitor, lifecycleStrategy, parent);
        }
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
        foo = foo.replaceAll("\n  started","");
        foo = foo.replaceAll("\n  disposed","");
        foo = foo.replaceAll("\n  handler","");
        foo = foo.replaceAll("\n  children","");
        foo = foo.replaceAll("\n  delegate\n","\n");
        foo = foo.replaceAll("\n    delegate\n","\n");
        foo = foo.replaceAll("\n  componentCharacteristic class=\"org.picocontainer.defaults.DefaultPicoContainer$1\"","");
        foo = foo.replaceAll("\n  componentCharacteristic","");
        foo = foo.replaceAll("\n  componentKeyToAdapterCache","");
        foo = foo.replaceAll("\n    startedComponentAdapters","");
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
