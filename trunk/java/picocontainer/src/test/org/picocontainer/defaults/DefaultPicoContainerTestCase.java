/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
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
import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.testmodel.Touchable;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class DefaultPicoContainerTestCase extends AbstractPicoContainerTestCase {

    protected MutablePicoContainer createPicoContainer() {
        return new DefaultPicoContainer();
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
        MutablePicoContainer parent = createPicoContainer();
        MutablePicoContainer child = createPicoContainer();
        child.setParent(parent);

        // ComponentF -> A -> ComponentB+ComponentC
        child.registerComponentImplementation(ComponentF.class);
        parent.registerComponentImplementation(ComponentA.class);
        child.registerComponentImplementation(ComponentB.class);
        child.registerComponentImplementation(ComponentC.class);

        try {
            child.getComponentInstance(ComponentF.class);
            fail();
        } catch (UnsatisfiableDependenciesException e) {
        }
    }
}
