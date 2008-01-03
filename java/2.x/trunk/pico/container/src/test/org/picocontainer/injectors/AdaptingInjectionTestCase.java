/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Stacy Curl                        *
 *****************************************************************************/

package org.picocontainer.injectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.tck.AbstractComponentFactoryTest;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class AdaptingInjectionTestCase extends AbstractComponentFactoryTest {

    XStream xs;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        xs = new XStream();
        xs.alias("RLS", ReflectionLifecycleStrategy.class);
        xs.alias("CCM", ConsoleComponentMonitor.class);
        xs.alias("Method-Injection", AnnotatedMethodInjector.class);
        xs.alias("Field-Injection", AnnotatedFieldInjector.class);
        xs.alias("Constructor-Injection", ConstructorInjector.class);
        //xs.alias("CCM", ConsoleComponentMonitor.class);
        xs.registerConverter(new Converter() {
            public boolean canConvert(Class aClass) {
                return aClass.getName().equals("org.picocontainer.monitors.ConsoleComponentMonitor") ||
                       aClass.getName().equals("org.picocontainer.lifecycle.ReflectionLifecycleStrategy");

            }

            public void marshal(Object object,
                                HierarchicalStreamWriter hierarchicalStreamWriter,
                                MarshallingContext marshallingContext)
            {
            }

            public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader,
                                    UnmarshallingContext unmarshallingContext)
            {
                return null;
            }
        });

    }

    protected ComponentFactory createComponentFactory() {
        return new AdaptingInjection();
    }

    @Test public void testInstantiateComponentWithNoDependencies() throws PicoCompositionException {
        ComponentAdapter componentAdapter =
            createComponentFactory().createComponentAdapter(new NullComponentMonitor(),
                                                            new NullLifecycleStrategy(),
                                                            new Properties(Characteristics.CDI),
                                                            Touchable.class,
                                                            SimpleTouchable.class,
                                                            (Parameter[])null);

        Object comp = componentAdapter.getComponentInstance(new DefaultPicoContainer());
        assertNotNull(comp);
        assertTrue(comp instanceof SimpleTouchable);
    }

    @Test public void testSingleUsecanBeInstantiatedByDefaultComponentAdapter() {
        ComponentAdapter componentAdapter = createComponentFactory().createComponentAdapter(new NullComponentMonitor(),
                                                                                            new NullLifecycleStrategy(),
                                                                                            new Properties(
                                                                                                Characteristics.CDI),
                                                                                            "o",
                                                                                            Object.class,
                                                                                            (Parameter[])null);
        Object component = componentAdapter.getComponentInstance(new DefaultPicoContainer());
        assertNotNull(component);
    }


    @Test public void testFactoryMakesConstructorInjector() {

        ComponentFactory cf = createComponentFactory();

        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        ComponentAdapter ca = cf.createComponentAdapter(cm, new ReflectionLifecycleStrategy(cm), new Properties(),
                                                        Map.class, HashMap.class, Parameter.DEFAULT);

        String foo = xs.toXML(ca).replace("\"", "");

        assertEquals("<Constructor-Injection>\n" +
                     "  <lifecycleStrategy class=RLS/>\n" +
                     "  <useNames>false</useNames>\n" +
                     "  <componentKey class=java-class>java.util.Map</componentKey>\n" +
                     "  <componentImplementation>java.util.HashMap</componentImplementation>\n" +
                     "  <componentMonitor class=CCM/>\n" +
                     "</Constructor-Injection>", foo);


    }

    @Test public void testFactoryMakesFieldAnnotationInjector() {

        ComponentFactory cf = createComponentFactory();

        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        ComponentAdapter ca = cf.createComponentAdapter(cm,
                                                        new ReflectionLifecycleStrategy(cm),
                                                        new Properties(),
                                                        AnnotatedFieldInjectorTestCase.Helicopter.class,
                                                        AnnotatedFieldInjectorTestCase.Helicopter.class,
                                                        Parameter.DEFAULT);

        String foo = xs.toXML(ca).replace("\"", "");

        assertEquals("<Field-Injection>\n" +
                     "  <injectionAnnotation>org.picocontainer.annotations.Inject</injectionAnnotation>\n" +
                     "  <lifecycleStrategy class=RLS/>\n" +
                     "  <useNames>false</useNames>\n" +
                     "  <componentKey class=java-class>org.picocontainer.injectors.AnnotatedFieldInjectorTestCase$Helicopter</componentKey>\n" +
                     "  <componentImplementation>org.picocontainer.injectors.AnnotatedFieldInjectorTestCase$Helicopter</componentImplementation>\n" +
                     "  <componentMonitor class=CCM/>\n" +
                     "</Field-Injection>", foo);


    }

    @Test public void testFactoryMakesMethodAnnotationInjector() {

        ComponentFactory cf = createComponentFactory();

        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        ComponentAdapter ca = cf.createComponentAdapter(cm,
                                                        new ReflectionLifecycleStrategy(cm),
                                                        new Properties(),
                                                        AnnotatedMethodInjectorTestCase.AnnotatedBurp.class,
                                                        AnnotatedMethodInjectorTestCase.AnnotatedBurp.class,
                                                        Parameter.DEFAULT);

        String foo = xs.toXML(ca).replace("\"", "");

        assertEquals("<Method-Injection>\n" +
                     "  <injectionAnnotation>org.picocontainer.annotations.Inject</injectionAnnotation>\n" +
                     "  <setterMethodPrefix></setterMethodPrefix>\n" +
                     "  <lifecycleStrategy class=RLS/>\n" +
                     "  <useNames>false</useNames>\n" +                     
                     "  <componentKey class=java-class>org.picocontainer.injectors.AnnotatedMethodInjectorTestCase$AnnotatedBurp</componentKey>\n" +
                     "  <componentImplementation>org.picocontainer.injectors.AnnotatedMethodInjectorTestCase$AnnotatedBurp</componentImplementation>\n" +
                     "  <componentMonitor class=CCM/>\n" +
                     "</Method-Injection>", foo);


    }


}
