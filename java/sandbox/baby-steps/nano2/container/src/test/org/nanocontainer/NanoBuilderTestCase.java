package org.nanocontainer;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.alternatives.EmptyPicoContainer;
import static org.picocontainer.PicoBuilder.CACHING;
import static org.picocontainer.PicoBuilder.IMPL_HIDING;
import static org.picocontainer.PicoBuilder.SDI;
import org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.monitors.ConsoleComponentMonitor;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;

public class NanoBuilderTestCase extends TestCase {

    XStream xs;

    protected void setUp() throws Exception {
        xs = new XStream();
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
        NanoContainer nc = new NanoBuilder().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "",foo);
    }

    public void testWithStartableLifecycle() {
        NanoContainer nc = new NanoBuilder().withLifecycle().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.StartableLifecycleStrategy\n" +
                "      componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "    lifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/org.nanocontainer.DefaultNanoContainer/delegate/lifecycleStrategy/componentMonitor\n",
                foo);
    }

    public void testWithReflectionLifecycle() {
        NanoContainer nc = new NanoBuilder().withReflectionLifecycle().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.ReflectionLifecycleStrategy\n" +
                "      methodNames\n" +
                "        stringstartstring\n" +
                "        stringstopstring\n" +
                "        stringdisposestring\n" +
                "      methodNames\n" +
                "      componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "    lifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/org.nanocontainer.DefaultNanoContainer/delegate/lifecycleStrategy/componentMonitor\n",
                foo);
    }
    
    public void testWithConsoleMonitor() {
        NanoContainer nc = new NanoBuilder().withConsoleMonitor().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "      delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "",foo);
    }

    public void testWithCustomMonitorByClass() {
        NanoContainer nc = new NanoBuilder().withMonitor(ConsoleComponentMonitor.class).build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "      delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "",foo);
    }

    public void testWithBogusCustomMonitorByClass() {
        try {
            new NanoBuilder().withMonitor(HashMap.class).build();
            fail("should have barfed");
        } catch (AssignabilityRegistrationException e) {
            // expected
        }
    }

    public void testWithImplementationHiding() {
        NanoContainer nc = new NanoBuilder().withHiddenImplementations().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory\n" +
                "      delegate=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "        cdiDelegate\n" +
                "        sdiDelegate\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",foo);
    }


    public void testWithImplementationHidingInstance() {
        NanoContainer nc = new NanoBuilder().withComponentAdapterFactory(new ImplementationHidingComponentAdapterFactory()).build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory\n" +
                "      delegate=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "        cdiDelegate\n" +
                "        sdiDelegate\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithCafsListChainThingy() {
        NanoContainer nc = new NanoBuilder().withComponentAdapterFactories(CACHING(), IMPL_HIDING(), SDI()).build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.CachingComponentAdapterFactory\n" +
                "      delegate=org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory\n" +
                "        delegate=org.picocontainer.adapters.SetterInjectionComponentAdapterFactory\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public static class CustomParentcontainer extends EmptyPicoContainer {
    }

    public void testWithCustomParentContainer() {
        NanoContainer nc = new NanoBuilder(new CustomParentcontainer()).build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "      cdiDelegate\n" +
                "      sdiDelegate\n" +
                "    parent=org.nanocontainer.NanoBuilderTestCase_CustomParentcontainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n", foo);
    }

    public void testWithBogusParentContainer() {
        try {
            NanoContainer nc = new NanoBuilder(null).build();
            fail("should have barfed");
        } catch (NullPointerException e) {
            //expected
        }
    }


    public void testWithSetterDI() {
        NanoContainer nc = new NanoBuilder().withSetterInjection().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.SetterInjectionComponentAdapterFactory\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithAnnotationDI() {
        NanoContainer nc = new NanoBuilder().withAnnotationInjection().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.AnnotationInjectionComponentAdapterFactory\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithCtorDI() {
        NanoContainer nc = new NanoBuilder().withConstructorInjection().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.ConstructorInjectionComponentAdapterFactory\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",foo);
    }

    public void testWithImplementationHidingAndSetterDI() {
        NanoContainer nc = new NanoBuilder().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory\n" +
                "      delegate=org.picocontainer.adapters.SetterInjectionComponentAdapterFactory\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithCachingImplementationHidingAndSetterDI() {
        NanoContainer nc = new NanoBuilder().withCaching().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.CachingComponentAdapterFactory\n" +
                "      delegate=org.picocontainer.adapters.ImplementationHidingComponentAdapterFactory\n" +
                "        delegate=org.picocontainer.adapters.SetterInjectionComponentAdapterFactory\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithThreadSafety() {
        NanoContainer nc = new NanoBuilder().withThreadSafety().build();
        String foo = simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.defaults.DefaultPicoContainer\n" +
                "    componentAdapterFactory=org.picocontainer.adapters.SynchronizedComponentAdapterFactory\n" +
                "      delegate=org.picocontainer.adapters.AnyInjectionComponentAdapterFactory\n" +
                "        cdiDelegate\n" +
                "        sdiDelegate\n" +
                "    parent=org.picocontainer.alternatives.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }




    private String simplifyRepresentation(MutablePicoContainer mpc) {
        String foo = xs.toXML(mpc);
        foo = foo.replace('$','_');
        foo = foo.replaceAll("/>","");
        foo = foo.replaceAll("</org.nanocontainer.DefaultNanoContainer","");
        foo = foo.replaceAll("</","");
        foo = foo.replaceAll("<","");
        foo = foo.replaceAll(">","");
        foo = foo.replaceAll("\n    childrenStarted","");
        foo = foo.replaceAll("\n    componentAdapters","");
        foo = foo.replaceAll("\n    orderedComponentAdapters","");
        foo = foo.replaceAll("\n    started","");
        foo = foo.replaceAll("\n    disposed","");
        foo = foo.replaceAll("\n    handler","");
        foo = foo.replaceAll("\n    children","");
        foo = foo.replaceAll("\n  namedChildContainers","");
        foo = foo.replaceAll("\n  delegate\n","\n");
        foo = foo.replaceAll("\n    delegate\n","\n");
        foo = foo.replaceAll("\n      delegate\n","\n");
        foo = foo.replaceAll("\n    componentCharacteristic class=\"org.picocontainer.defaults.DefaultPicoContainer$1\"","");
        foo = foo.replaceAll("\n    componentCharacteristic","");
        foo = foo.replaceAll("\n    componentKeyToAdapterCache","");
        foo = foo.replaceAll("\n    startedComponentAdapters","");
        foo = foo.replaceAll("\"class=","\"\nclass=");
        foo = foo.replaceAll("\n    componentAdapterFactory\n","\n");
        foo = foo.replaceAll("\n    componentMonitor\n","\n");
        foo = foo.replaceAll("\n    lifecycleManager","");
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
