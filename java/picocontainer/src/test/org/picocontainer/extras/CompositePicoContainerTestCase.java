/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import junit.framework.TestCase;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.extras.CompositePicoContainer;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;

import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

public class CompositePicoContainerTestCase extends TestCase {
    private RegistrationPicoContainer pico;
    private CompositePicoContainer.WithContainerArray composite;

    public void setUp() throws PicoRegistrationException, PicoInitializationException {
        pico = new DefaultPicoContainer.Default();
        pico.registerComponentByClass(SimpleTouchable.class);
        pico.instantiateComponents();
        composite = new CompositePicoContainer.WithContainerArray(new PicoContainer[]{pico});
    }

    public void testGetComponents() {
        assertEquals("Collections of Component Keys should be the same", pico.getComponentKeys(), composite.getComponentKeys());
    }

    public void testGetComponentKeys() {
        assertEquals("Collections of Component Keys should be the same", pico.getComponents(), composite.getComponents());
    }

    public void testGetComponent() {
        assertSame("Touchable should be the same", pico.getComponent(SimpleTouchable.class), composite.getComponent(SimpleTouchable.class));
    }

    public void testHasComponent() {
        assertEquals("Containers should contain the same", pico.hasComponent(SimpleTouchable.class), composite.hasComponent(SimpleTouchable.class));
    }

    public void testNullContainer() {
        try {
            new CompositePicoContainer.WithContainerArray(null);
            fail("Should have failed with an NPE");
        } catch (NullPointerException e) {
            // fine
        }
    }

    public void todo_testEmptyArrayOfContainers() {
        try {
            new CompositePicoContainer.WithContainerArray(new PicoContainer[0]);
            fail("Should have failed with an NPE");
        } catch (NullPointerException e) {
            // fine
        }
    }

    public void testNullInArrayOfContainers() {
        try {
            new CompositePicoContainer.WithContainerArray(new PicoContainer[1]);
            fail("Should have failed with an NPE");
        } catch (NullPointerException e) {
            // fine
        }
    }

    public void testUnsupportedOperations() throws PicoInitializationException {
        try {
            composite.getComponentMulticaster();
            fail("should have barfed");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            composite.getComponentMulticaster(true, true);
            fail("should have barfed");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            composite.instantiateComponents();
            fail("should have barfed");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }


    public void testBasic() {

        final String acomp = "hello";
        final Integer bcomp = new Integer(123);

        PicoContainer a = new PicoContainer() {
            public boolean hasComponent(Object compKey) {
                return compKey == String.class;
            }

            public Object getComponent(Object compKey) {
                return compKey == String.class ? acomp : null;
            }

            public Collection getComponents() {
                Set result = new HashSet();
                result.add(acomp);
                return result;
            }

            public Collection getComponentKeys() {
                Set result = new HashSet();
                result.add(String.class);
                return result;
            }

            public void instantiateComponents() throws PicoInstantiationException {
            }

            public Object getComponentMulticaster() {
                return null;
            }

            public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
                return null;
            }
        };

        PicoContainer b = new PicoContainer() {
            public boolean hasComponent(Object compKey) {
                return compKey == Integer.class;
            }

            public Object getComponent(Object compKey) {
                return compKey == Integer.class ? bcomp : null;
            }

            public Collection getComponents() {
                Set result = new HashSet();
                result.add(bcomp);
                return result;
            }

            public Collection getComponentKeys() {
                Set result = new HashSet();
                result.add(Integer.class);
                return result;

            }

            public void instantiateComponents() throws PicoInstantiationException {
            }

            public Object getComponentMulticaster() {
                return null;
            }

            public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
                return null;
            }
        };

        CompositePicoContainer acc = new CompositePicoContainer.WithContainerArray(new PicoContainer[]{a, b});

        assertTrue(acc.hasComponent(String.class));
        assertTrue(acc.hasComponent(Integer.class));
        assertTrue(acc.getComponent(String.class) == acomp);
        assertTrue(acc.getComponent(Integer.class) == bcomp);
        assertTrue(acc.getComponents().size() == 2);
        assertTrue(acc.getComponentKeys().size() == 2);

    }

    public void testEmpty() {

        CompositePicoContainer acc = new CompositePicoContainer.WithContainerArray(new PicoContainer[0]);
        assertTrue(acc.hasComponent(String.class) == false);
        assertTrue(acc.getComponent(String.class) == null);
        assertTrue(acc.getComponents().size() == 0);
    }

    public void testParentComponentRegistryDominance() {
        ComponentRegistry cr = new DefaultComponentRegistry();
        cr.putComponent(Touchable.class, new SimpleTouchable());
        CompositePicoContainer acc = new CompositePicoContainer(cr, new PicoContainer[0]);
        assertTrue(acc.hasComponent(Touchable.class));
        assertTrue(acc.getComponent(Touchable.class) instanceof SimpleTouchable);
    }

    public void testAdditiveFeatures() {

        CompositePicoContainer addContainer = new CompositePicoContainer.Default();
        addContainer.addContainer(pico);
        assertTrue("Should have a Touchable", addContainer.hasComponent(SimpleTouchable.class));
        addContainer.removeContainer(pico);
        assertFalse("Should not have a Touchable", addContainer.hasComponent(SimpleTouchable.class));

    }

}
