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

import org.jmock.Mock;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.testmodel.Touchable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
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
        child.registerComponentImplementation(c.class);

        try {
            child.getComponentInstance(ComponentF.class);
            fail();
        } catch (UnsatisfiableDependenciesException e) {
        }
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

    /*
    When pico tries to instantiate ArrayList, it will attempt to use the constructor that takes a
    java.util.Collection. Since both the ArrayList and LinkedList are Collection, it will fail with
    AmbiguousComponentResolutionException.

    Pico should be smart enough to figure out that it shouldn't consider a component as a candidate parameter for
    its own instantiation. This may be fixed by adding an additional parameter to ComponentParameter.resolveAdapter -
    namely the ComponentAdapter instance that should be excluded.

    AH
     */
    public void TODOtestComponentsWithCommonSupertypeWhichIsAConstructorArgumentCanBeLookedUpByConcreteType() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.registerComponentImplementation(LinkedList.class);
        pico.registerComponentImplementation(ArrayList.class);
        assertEquals(ArrayList.class, pico.getComponentInstanceOfType(ArrayList.class).getClass());
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

    public void testAcceptShouldIterateOverChildContainersAndAppropriateComponents() {
        final MutablePicoContainer parent = createPicoContainer(null);
        final MutablePicoContainer child = parent.makeChildContainer();
        Map map = new HashMap();
        ComponentAdapter mapAdapter = new InstanceComponentAdapter("map", map);
        parent.registerComponent(mapAdapter);
        child.registerComponentImplementation(ArrayList.class);

        Mock noneVisitor = new Mock(PicoVisitor.class);
        noneVisitor.expects(once()).method("visitContainer").with(same(parent));
        noneVisitor.expects(once()).method("visitContainer").with(same(child));
        parent.accept((PicoVisitor) noneVisitor.proxy(), null, true);

        Mock mapVisitor = new Mock(PicoVisitor.class);
        mapVisitor.expects(once()).method("visitContainer").with(same(parent));
        mapVisitor.expects(once()).method("visitContainer").with(same(child));
        mapVisitor.expects(once()).method("visitComponentInstance").with(same(map));
        mapVisitor.expects(once()).method("visitComponentAdapter").with(same(mapAdapter));
        parent.accept((PicoVisitor) mapVisitor.proxy(), Map.class, true);

        noneVisitor.verify();
        mapVisitor.verify();
    }

}
