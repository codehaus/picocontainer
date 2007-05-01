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

        assertNull(pico.getComponentAdapter(Touchable.class));
        pico.component(SimpleTouchable.class);
        assertNotNull(pico.getComponentAdapter(SimpleTouchable.class));
        assertNotNull(pico.getComponentAdapter(Touchable.class));
    }


    //TODO move
    public void testMultipleImplementationsAccessedThroughKey()
            throws PicoInitializationException, PicoRegistrationException, PicoInvocationTargetInitializationException {
        SimpleTouchable Touchable1 = new SimpleTouchable();
        SimpleTouchable Touchable2 = new SimpleTouchable();
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.component("Touchable1", Touchable1);
        pico.component("Touchable2", Touchable2);
        pico.component("fred1", DependsOnTouchable.class, new Parameter[]{new ComponentParameter("Touchable1")});
        pico.component("fred2", DependsOnTouchable.class, new Parameter[]{new ComponentParameter("Touchable2")});

        DependsOnTouchable fred1 = (DependsOnTouchable) pico.getComponent("fred1");
        DependsOnTouchable fred2 = (DependsOnTouchable) pico.getComponent("fred2");

        assertFalse(fred1 == fred2);
        assertSame(Touchable1, fred1.getTouchable());
        assertSame(Touchable2, fred2.getTouchable());
    }

    //TODO - move
    public void testRegistrationByName() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        Webster one = new Webster(new ArrayList());
        Touchable two = new SimpleTouchable();

        pico.component("one", one);
        pico.component("two", two);

        assertEquals("Wrong number of comps in the internals", 2, pico.getComponents().size());

        assertEquals("Looking up one Touchable", one, pico.getComponent("one"));
        assertEquals("Looking up two Touchable", two, pico.getComponent("two"));

        assertTrue("Object one the same", one == pico.getComponent("one"));
        assertTrue("Object two the same", two == pico.getComponent("two"));

        assertEquals("Lookup of unknown key should return null", null, pico.getComponent("unknown"));
    }

    public void testRegistrationByNameAndClassWithResolving() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.component(List.class, new ArrayList());
        pico.component("one", Webster.class);
        pico.component("two", SimpleTouchable.class);

        assertEquals("Wrong number of comps in the internals", 3, pico.getComponents().size());

        assertNotNull("Object one the same", pico.getComponent("one"));
        assertNotNull("Object two the same", pico.getComponent("two"));

        assertNull("Lookup of unknown key should return null", pico.getComponent("unknown"));
    }

    public void testDuplicateRegistrationWithTypeAndObject() throws PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.component(SimpleTouchable.class);
        try {
            pico.component(SimpleTouchable.class, new SimpleTouchable());
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            // expected
            assertTrue(e.getDuplicateKey() == SimpleTouchable.class);
            assertTrue(e.getMessage().indexOf(SimpleTouchable.class.getName()) > 0);
        }
    }


    public void testComponentRegistrationMismatch() throws PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = new DefaultPicoContainer();

        try {
            pico.component(List.class, SimpleTouchable.class);
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
        pico.component(Animal.class,
                Dino.class,
                new Parameter[]{
                    new ConstantParameter("bones")
                });

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("bones", animal.getFood());
    }

    public void testParameterCanBePrimitive() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.component(Animal.class, Dino2.class, new Parameter[]{new ConstantParameter(new Integer(22))});

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("22", animal.getFood());
    }

    public void testMultipleParametersCanBePassed() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.component(Animal.class, Dino3.class, new Parameter[]{
            new ConstantParameter("a"),
            new ConstantParameter("b")
        });

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("ab", animal.getFood());

    }

    public void testParametersCanBeMixedWithComponentsCanBePassed() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.component(Touchable.class, SimpleTouchable.class);
        pico.component(Animal.class, Dino4.class, new Parameter[]{
            new ConstantParameter("a"),
            new ConstantParameter(new Integer(3)),
            new ConstantParameter("b"),
            ComponentParameter.DEFAULT
        });

        Animal animal = (Animal) pico.getComponent(Animal.class);
        assertNotNull("Component not null", animal);
        assertEquals("a3b org.picocontainer.testmodel.SimpleTouchable", animal.getFood());
    }

}
