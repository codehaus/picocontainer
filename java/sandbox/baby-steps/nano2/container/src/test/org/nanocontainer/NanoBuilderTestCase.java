package org.nanocontainer;

import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implHiding;
import org.picocontainer.behaviors.ImplementationHidingBehaviorFactory;
import org.picocontainer.containers.EmptyPicoContainer;
import static org.picocontainer.injectors.Injectors.SDI;
import org.picocontainer.monitors.ConsoleComponentMonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import junit.framework.TestCase;

public class NanoBuilderTestCase extends TestCase {

    PrettyXmlRepresentation pxr;

    protected void setUp() throws Exception {
        pxr = new PrettyXmlRepresentation();
    }

    public void testBasic() {
        NanoContainer nc = new NanoBuilder().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "",foo);
    }

    public void testWithStartableLifecycle() {
        NanoContainer nc = new NanoBuilder().withLifecycle().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.StartableLifecycleStrategy\n" +
                "      componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "    lifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/org.nanocontainer.DefaultNanoContainer/delegate/lifecycleStrategy/componentMonitor\n",
                foo);
    }

    public void testWithReflectionLifecycle() {
        NanoContainer nc = new NanoBuilder().withReflectionLifecycle().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
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
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "      delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "",foo);
    }

    public void testWithCustomMonitorByClass() {
        NanoContainer nc = new NanoBuilder().withMonitor(ConsoleComponentMonitor.class).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "      delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "",foo);
    }

    @SuppressWarnings({ "unchecked" })
    public void testWithBogusCustomMonitorByClass() {
        try {
            Class aClass = HashMap.class;
            new NanoBuilder().withMonitor(aClass).build();
            fail("should have barfed");
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void testWithImplementationHiding() {
        NanoContainer nc = new NanoBuilder().withHiddenImplementations().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "      delegate=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",foo);
    }


    public void testWithImplementationHidingInstance() {
        NanoContainer nc = new NanoBuilder().withComponentFactory(new ImplementationHidingBehaviorFactory()).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "      delegate=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithComponentFactoriesListChainThingy() {
        NanoContainer nc = new NanoBuilder(SDI()).withComponentAdapterFactories(caching(), implHiding()).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.CachingBehaviorFactory\n" +
                "      delegate=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "        delegate=org.picocontainer.injectors.SetterInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public static class CustomParentcontainer extends EmptyPicoContainer {
    }

    public void testWithCustomParentContainer() {
        NanoContainer nc = new NanoBuilder(new CustomParentcontainer()).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.nanocontainer.NanoBuilderTestCase_CustomParentcontainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n", foo);
    }

    public void testWithBogusParentContainerBehavesAsIfNotSet() {
        NanoContainer nc = new NanoBuilder((PicoContainer)null).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                     "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                     "    componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                     "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                     "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                     "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                     "", foo);
    }


    public void testWithSetterDI() {
        NanoContainer nc = new NanoBuilder().withSetterInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.SetterInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithAnnotationDI() {
        NanoContainer nc = new NanoBuilder().withAnnotationInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.MethodAnnotationInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithCtorDI() {
        NanoContainer nc = new NanoBuilder().withConstructorInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.ConstructorInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",foo);
    }

    public void testWithImplementationHidingAndSetterDI() {
        NanoContainer nc = new NanoBuilder().withHiddenImplementations().withSetterInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "      delegate=org.picocontainer.injectors.SetterInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithCachingImplementationHidingAndSetterDI() {
        NanoContainer nc = new NanoBuilder().withCaching().withHiddenImplementations().withSetterInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.CachingBehaviorFactory\n" +
                "      delegate=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "        delegate=org.picocontainer.injectors.SetterInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithThreadSafety() {
        NanoContainer nc = new NanoBuilder().withThreadSafety().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.SynchronizedBehaviorFactory\n" +
                "      delegate=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithCustomNanoContainer() {
        NanoContainer nc = new NanoBuilder().implementedBy(TestNanoContainer.class).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.NanoBuilderTestCase_-TestNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "org.nanocontainer.NanoBuilderTestCase_-TestNanoContainer",
                foo);
    }


    public static class TestNanoContainer extends DefaultNanoContainer {
        public TestNanoContainer(ClassLoader classLoader, MutablePicoContainer delegate) {
            super(classLoader, delegate);
        }
    }

    public void testWithCustomNanoAndPicoContainer() {
        NanoContainer nc = new NanoBuilder().implementedBy(TestNanoContainer.class).picoImplementedBy(TestPicoContainer.class).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.NanoBuilderTestCase_-TestNanoContainer\n" +
                "  delegate=org.nanocontainer.NanoBuilderTestCase_TestPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "org.nanocontainer.NanoBuilderTestCase_-TestNanoContainer",
                foo);
    }

    public static class TestPicoContainer extends DefaultPicoContainer {
        public TestPicoContainer(ComponentFactory componentFactory, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
            super(componentFactory, lifecycleStrategy, parent, monitor);
        }
    }




}
