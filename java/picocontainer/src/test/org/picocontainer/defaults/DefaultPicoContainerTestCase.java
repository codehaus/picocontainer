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

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.testmodel.Touchable;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.awt.Button;
import java.awt.Panel;
import java.io.Serializable;

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

        assertTrue("Component should be instance of Touchable", Touchable.class.isAssignableFrom(
                pico.getComponentAdapterOfType(Touchable.class).getComponentImplementation() ));
    }

    public void testComponentInstancesFromParentsAreNotDirectlyAccessible() {
        MutablePicoContainer a = new DefaultPicoContainer();
        MutablePicoContainer b = new DefaultPicoContainer(a);
        MutablePicoContainer c = new DefaultPicoContainer(b);

        Object ao = new Object();
        Object bo = new Object();
        Object co = new Object();

        a.registerComponentInstance("a", ao);
        b.registerComponentInstance("b", bo);
        c.registerComponentInstance("c", co);

        assertEquals(1, a.getComponentInstances().size());
        assertEquals(1, b.getComponentInstances().size());
        assertEquals(1, c.getComponentInstances().size());
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
}
