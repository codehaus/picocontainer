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

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.Webster;

import java.util.ArrayList;
import java.util.List;

public class NoneOfTheseTestsAffectCoverageMeaningTheyCouldGoTestCase extends TestCase {

    //TODO - move to AbstractComponentRegistryTestCase
    public void testGetComponentSpecification() throws PicoRegistrationException, DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, AmbiguousComponentResolutionException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        assertNull(pico.getComponentAdapterOfType(Touchable.class));
        pico.registerComponentImplementation(SimpleTouchable.class);
        assertNotNull(pico.getComponentAdapterOfType(SimpleTouchable.class));
        assertNotNull(pico.getComponentAdapterOfType(Touchable.class));
    }


    //TODO move
    public void testMultipleImplementationsAccessedThroughKey()
            throws PicoInitializationException, PicoRegistrationException, PicoInvocationTargetInitializationException {
        SimpleTouchable Touchable1 = new SimpleTouchable();
        SimpleTouchable Touchable2 = new SimpleTouchable();
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentInstance("Touchable1", Touchable1);
        pico.registerComponentInstance("Touchable2", Touchable2);
        pico.registerComponentImplementation("fred1", DependsOnTouchable.class, new Parameter[]{new ComponentParameter("Touchable1")});
        pico.registerComponentImplementation("fred2", DependsOnTouchable.class, new Parameter[]{new ComponentParameter("Touchable2")});

        DependsOnTouchable fred1 = (DependsOnTouchable) pico.getComponentInstance("fred1");
        DependsOnTouchable fred2 = (DependsOnTouchable) pico.getComponentInstance("fred2");

        assertFalse(fred1 == fred2);
        assertSame(Touchable1, fred1.getTouchable());
        assertSame(Touchable2, fred2.getTouchable());
    }

    //TODO - move
    public void testRegistrationByName() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        Webster one = new Webster(new ArrayList());
        Touchable two = new SimpleTouchable();

        pico.registerComponentInstance("one", one);
        pico.registerComponentInstance("two", two);

        assertEquals("Wrong number of comps in the internals", 2, pico.getComponentInstances().size());

        assertEquals("Looking up one Touchable", one, pico.getComponentInstance("one"));
        assertEquals("Looking up two Touchable", two, pico.getComponentInstance("two"));

        assertTrue("Object one the same", one == pico.getComponentInstance("one"));
        assertTrue("Object two the same", two == pico.getComponentInstance("two"));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponentInstance("unknown"));
    }

    public void testRegistrationByNameAndClassWithResolving() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentInstance(List.class, new ArrayList());
        pico.registerComponentImplementation("one", Webster.class);
        pico.registerComponentImplementation("two", SimpleTouchable.class);

        assertEquals("Wrong number of comps in the internals", 3, pico.getComponentInstances().size());

        assertNotNull("Object one the same", pico.getComponentInstance("one"));
        assertNotNull("Object two the same", pico.getComponentInstance("two"));

        assertNull("Lookup of unknown key should return null", pico.getComponentInstance("unknown"));
    }

    public void testDuplicateRegistrationWithTypeAndObject() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.registerComponentImplementation(SimpleTouchable.class);
        try {
            pico.registerComponentInstance(SimpleTouchable.class, new SimpleTouchable());
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            // expected
            assertTrue(e.getDuplicateKey() == SimpleTouchable.class);
            assertTrue(e.getMessage().indexOf(SimpleTouchable.class.getName()) > 0);
        }
    }


    public void testComponentRegistrationMismatch() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = new DefaultPicoContainer();

        try {
            pico.registerComponentImplementation(List.class, SimpleTouchable.class);
        } catch (AssignabilityRegistrationException e) {
            // not worded in message
            assertTrue(e.getMessage().indexOf(List.class.getName()) > 0);
            assertTrue(e.getMessage().indexOf(SimpleTouchable.class.getName()) > 0);
        }

    }

    interface Animal {

        String getFood();
    }

    public static class Dino implements Animal {
        String food;

        public Dino(String food) {
            this.food = food;
        }

        public String getFood() {
            return food;
        }
    }

    public static class Dino2 extends Dino {
        public Dino2(int number) {
            super(String.valueOf(number));
        }
    }

    public static class Dino3 extends Dino {
        public Dino3(String a, String b) {
            super(a + b);
        }
    }

    public static class Dino4 extends Dino {
        public Dino4(String a, int n, String b, Touchable Touchable) {
            super(a + n + b + " " + Touchable.getClass().getName());
        }
    }

    public void testParameterCanBePassedToConstructor() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Animal.class,
                Dino.class,
                new Parameter[]{
                    new ConstantParameter("bones")
                });

        Animal animal = (Animal) pico.getComponentInstance(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("bones", animal.getFood());
    }

    public void testParameterCanBePrimitive() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Animal.class, Dino2.class, new Parameter[]{new ConstantParameter(new Integer(22))});

        Animal animal = (Animal) pico.getComponentInstance(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("22", animal.getFood());
    }

    public void testMultipleParametersCanBePassed() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Animal.class, Dino3.class, new Parameter[]{
            new ConstantParameter("a"),
            new ConstantParameter("b")
        });

        Animal animal = (Animal) pico.getComponentInstance(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("ab", animal.getFood());

    }

    public void testParametersCanBeMixedWithComponentsCanBePassed() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        pico.registerComponentImplementation(Animal.class, Dino4.class, new Parameter[]{
            new ConstantParameter("a"),
            new ConstantParameter(new Integer(3)),
            new ConstantParameter("b"),
            ComponentParameter.DEFAULT
        });

        Animal animal = (Animal) pico.getComponentInstance(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("a3b org.picocontainer.testmodel.SimpleTouchable", animal.getFood());
    }

}
