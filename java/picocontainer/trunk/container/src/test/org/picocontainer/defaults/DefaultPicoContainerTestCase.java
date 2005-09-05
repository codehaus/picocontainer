/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.monitors.WriterComponentMonitor;
import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.testmodel.DecoratedTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @author Mauro Talevi
 * @version $Revision$
 */
public class DefaultPicoContainerTestCase extends AbstractPicoContainerTestCase {
    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new DefaultPicoContainer(parent);
    }

    // to go to parent testcase ?
    public void testBasicInstantiationAndContainment() throws PicoException, PicoRegistrationException {
        DefaultPicoContainer pico = (DefaultPicoContainer) createPicoContainerWithTouchableAndDependsOnTouchable();

        assertTrue("Component should be instance of Touchable", Touchable.class.isAssignableFrom(pico.getComponentAdapterOfType(Touchable.class).getComponentImplementation()));
    }

    public void testUpDownDependenciesCannotBeFollowed() {
        MutablePicoContainer parent = createPicoContainer(null);
        MutablePicoContainer child = createPicoContainer(parent);

        // ComponentF -> ComponentA -> ComponentB+c
        child.registerComponentImplementation(ComponentF.class);
        parent.registerComponentImplementation(ComponentA.class);
        child.registerComponentImplementation(ComponentB.class);
        child.registerComponentImplementation(ComponentC.class);

        Object o = child.getComponentInstance(ComponentF.class);
        assertNull(o);
    }

    public void testComponentsCanBeRemovedByInstance() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation(HashMap.class);
        pico.registerComponentImplementation(ArrayList.class);
        List list = (List) pico.getComponentInstanceOfType(List.class);
        pico.unregisterComponentByInstance(list);
        assertEquals(1, pico.getComponentAdapters().size());
        assertEquals(1, pico.getComponentInstances().size());
        assertEquals(HashMap.class, pico.getComponentInstanceOfType(Serializable.class).getClass());
    }

    public void testComponentInstancesListIsReturnedForNullType(){
        MutablePicoContainer pico = createPicoContainer(null);
        List componentInstances = pico.getComponentInstancesOfType(null);
        assertNotNull(componentInstances);
        assertEquals(0, componentInstances.size());
    }
    
    /*
    When pico tries to instantiate ArrayList, it will attempt to use the constructor that takes a
    java.util.Collection. Since both the ArrayList and LinkedList are Collection, it will fail with
    AmbiguousComponentResolutionException.

    Pico should be smart enough to figure out that it shouldn't consider a component as a candidate parameter for
    its own instantiation. This may be fixed by adding an additional parameter to ComponentParameter.resolveAdapter -
    namely the ComponentAdapter instance that should be excluded.
    AH
    Pico-222 is fixed, but activating this will lead to cyclic dependency...
    KP
     */
    public void TODOtestComponentsWithCommonSupertypeWhichIsAConstructorArgumentCanBeLookedUpByConcreteType() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation(LinkedList.class);
        pico.registerComponentImplementation(ArrayList.class);
        assertEquals(ArrayList.class, pico.getComponentInstanceOfType(ArrayList.class).getClass());
    }

    /*
     When pico tries to resolve DecoratedTouchable it find as dependency itself and SimpleTouchable.
     Problem is basically the same as above. Pico should not consider self as solution.
     
     JS
     fixed it ( PICO-222 )
     KP
     */
    public void testUnambiguouSelfDependency() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation(SimpleTouchable.class);
        pico.registerComponentImplementation(DecoratedTouchable.class);
        Touchable t = (Touchable) pico.getComponentInstance(DecoratedTouchable.class);
        assertNotNull(t);
    }

    public static class Thingie {
        public Thingie(List c) {
            assertNotNull(c);
        }
    }

    public void testThangCanBeInstantiatedWithArrayList() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Thingie.class);
        pico.registerComponentImplementation(ArrayList.class);
        assertNotNull(pico.getComponentInstance(Thingie.class));
    }

    public void getComponentAdaptersOfTypeReturnsUnmodifiableList() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Thingie.class);
        try {
            pico.getComponentAdaptersOfType(Thingie.class).add("blah");
            fail("Expected an exception!");
        } catch (UnsupportedOperationException th) {
        }
    }


    public static class Service {
    }

    public static class TransientComponent {
        private Service service;

        public TransientComponent(Service service) {
            this.service = service;
        }
    }

    public void testDefaultPicoContainerReturnsNewInstanceForEachCallWhenUsingTransientComponentAdapter() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.registerComponentImplementation(Service.class);
        picoContainer.registerComponent(new ConstructorInjectionComponentAdapter(TransientComponent.class, TransientComponent.class));
        TransientComponent c1 = (TransientComponent) picoContainer.getComponentInstance(TransientComponent.class);
        TransientComponent c2 = (TransientComponent) picoContainer.getComponentInstance(TransientComponent.class);
        assertNotSame(c1, c2);
        assertSame(c1.service, c2.service);
    }

    public static class DependsOnCollection {
        public DependsOnCollection(Collection c) {
        }
    }

    public void testShouldProvideInfoAboutDependingWhenAmbiguityHappens() {
        MutablePicoContainer pico = this.createPicoContainer(null);
        pico.registerComponentInstance(new ArrayList());
        pico.registerComponentInstance(new LinkedList());
        pico.registerComponentImplementation(DependsOnCollection.class);
        try {
            pico.getComponentInstanceOfType(DependsOnCollection.class);
            fail();
        } catch (AmbiguousComponentResolutionException expected) {
            String doc = DependsOnCollection.class.getName();
            assertEquals("class " + doc + " has ambiguous dependency on interface java.util.Collection, resolves to multiple classes: [class java.util.ArrayList, class java.util.LinkedList]", expected.getMessage());
        }
    }

    public void testCanChangeMonitor() {
        StringWriter writer1 = new StringWriter();
        ComponentMonitor monitor1 = new WriterComponentMonitor(writer1);
        DefaultPicoContainer pico = new DefaultPicoContainer(monitor1);
        pico.registerComponentImplementation("t1", SimpleTouchable.class);
        pico.registerComponentImplementation("t3", SimpleTouchable.class);
        Touchable t1 = (Touchable) pico.getComponentInstance("t1");
        assertNotNull(t1);
        assertTrue("writer not empty", writer1.toString().length() > 0);
        StringWriter writer2 = new StringWriter();
        ComponentMonitor monitor2 = new WriterComponentMonitor(writer2);
        pico.changeMonitor(monitor2);
        pico.registerComponentImplementation("t2", SimpleTouchable.class);
        Touchable t2 = (Touchable) pico.getComponentInstance("t2");
        assertNotNull(t2);
        assertTrue("writer not empty", writer2.toString().length() > 0);
        assertTrue("writers of same length", writer1.toString().length() == writer2.toString().length());
        Touchable t3 = (Touchable) pico.getComponentInstance("t3");
        assertNotNull(t3);
        assertTrue("old writer was used", writer1.toString().length() < writer2.toString().length());
    }

    public void testCanChangeMonitorOfChildContainers() {
        StringWriter writer1 = new StringWriter();
        ComponentMonitor monitor1 = new WriterComponentMonitor(writer1);
        DefaultPicoContainer parent = new DefaultPicoContainer();
        DefaultPicoContainer child = new DefaultPicoContainer(monitor1);
        parent.addChildContainer(child);
        child.registerComponentImplementation("t1", SimpleTouchable.class);
        child.registerComponentImplementation("t3", SimpleTouchable.class);
        Touchable t1 = (Touchable) child.getComponentInstance("t1");
        assertNotNull(t1);
        assertTrue("writer not empty", writer1.toString().length() > 0);
        StringWriter writer2 = new StringWriter();
        ComponentMonitor monitor2 = new WriterComponentMonitor(writer2);
        parent.changeMonitor(monitor2);
        child.registerComponentImplementation("t2", SimpleTouchable.class);
        Touchable t2 = (Touchable) child.getComponentInstance("t2");
        assertNotNull(t2);
        assertTrue("writer not empty", writer2.toString().length() > 0);
        assertTrue("writers of same length", writer1.toString().length() == writer2.toString().length());
        Touchable t3 = (Touchable) child.getComponentInstance("t3");
        assertNotNull(t3);
        assertTrue("old writer was used", writer1.toString().length() < writer2.toString().length());
    }
    
    public void testCanReturnCurrentMonitor() {
        StringWriter writer1 = new StringWriter();
        ComponentMonitor monitor1 = new WriterComponentMonitor(writer1);
        DefaultPicoContainer pico = new DefaultPicoContainer(monitor1);
        assertEquals(monitor1, pico.currentMonitor());
        StringWriter writer2 = new StringWriter();
        ComponentMonitor monitor2 = new WriterComponentMonitor(writer2);
        pico.changeMonitor(monitor2);
        assertEquals(monitor2, pico.currentMonitor());
    }

    public void testCannotReturnCurrentMonitor() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new ComponentAdapterFactoryWithNoMonitor());
        try {
            pico.currentMonitor();
            fail("PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            assertEquals("No component monitor found in container or its children", e.getMessage());
        }
    }

    private static class ComponentAdapterFactoryWithNoMonitor implements ComponentAdapterFactory {
        public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            return null;
        }
        
    }
    public void testMakeChildContainer() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.registerComponentImplementation("t1", SimpleTouchable.class);
        MutablePicoContainer child = parent.makeChildContainer();
        Object t1 = child.getParent().getComponentInstance("t1");
        assertNotNull(t1);
        assertTrue(t1 instanceof SimpleTouchable);
    }

}