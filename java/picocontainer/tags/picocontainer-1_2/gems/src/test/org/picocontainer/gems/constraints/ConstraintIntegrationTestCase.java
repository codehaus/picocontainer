/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.DecoratedTouchable;
import org.picocontainer.testmodel.DependsOnArray;
import org.picocontainer.testmodel.DependsOnList;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.DependsOnTwoComponents;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.ArrayList;
import java.util.List;

/**
 * Integration tests using Constraints.
 *
 * @author Nick Sieger
 * @version 1.1
 */
public class ConstraintIntegrationTestCase
    extends TestCase {

    MutablePicoContainer container;

    protected void setUp() throws Exception {
        super.setUp();

        container = new DefaultPicoContainer();
        container.registerComponentImplementation(SimpleTouchable.class);
        container.registerComponentImplementation(DependsOnTouchable.class);
        container.registerComponentImplementation(DependsOnTwoComponents.class);
        container.registerComponentInstance(ArrayList.class, new ArrayList());
        container.registerComponentInstance(Object[].class, new Object[0]);
    }


    public void testAmbiguouTouchableDependency() {
        container.registerComponentImplementation(AlternativeTouchable.class);
        container.registerComponentImplementation(DecoratedTouchable.class);

        try {
            container.getComponentInstance(DecoratedTouchable.class);
            fail("AmbiguousComponentResolutionException expected");
        } catch (AmbiguousComponentResolutionException acre) {
            // success
        }
    }

    public void testTouchableDependencyWithComponentKeyParameter() {
        container.registerComponentImplementation(AlternativeTouchable.class);
        container.registerComponentImplementation(DecoratedTouchable.class,
                                                  DecoratedTouchable.class,
                                                  new Parameter[] { new ComponentParameter(SimpleTouchable.class) });

        Touchable t = (Touchable) container.getComponentInstance(DecoratedTouchable.class);
        assertNotNull(t);
    }

    public void testTouchableDependencyInjectedViaConstraint() {
        container.registerComponentImplementation(AlternativeTouchable.class);
        container.registerComponentImplementation(DecoratedTouchable.class,
                                                  DecoratedTouchable.class,
                                                  new Parameter[] { new Not(new IsType(SimpleTouchable.class)) });
        Touchable t = (Touchable) container.getComponentInstance(DecoratedTouchable.class);
        assertNotNull(t);
    }

    public void testComponentDependsOnCollectionOfEverythingElse() {
        container.registerComponentImplementation(DependsOnList.class,
                                                  DependsOnList.class,
                                                  new Parameter[] { new CollectionConstraint(Anything.ANYTHING) });
        DependsOnList dol = (DependsOnList) container.getComponentInstance(DependsOnList.class);
        assertNotNull(dol);
        List dependencies = dol.getDependencies();
        assertEquals(5, dependencies.size());
    }

    public void testComponentDependsOnCollectionOfTouchables() {
        container.registerComponentImplementation(AlternativeTouchable.class);
        container.registerComponentImplementation(DecoratedTouchable.class,
                                                  DecoratedTouchable.class,
                                                  new Parameter[] { new Not(new IsType(SimpleTouchable.class)) });
        container.registerComponentImplementation(DependsOnList.class,
                                                  DependsOnList.class,
                                                  new Parameter[] { new CollectionConstraint(new IsType(Touchable.class)) });
        DependsOnList dol = (DependsOnList) container.getComponentInstance(DependsOnList.class);
        assertNotNull(dol);
        List dependencies = dol.getDependencies();
        assertEquals(3, dependencies.size());
    }

    public void testComponentDependsOnCollectionOfSpecificTouchables() {
        container.registerComponentImplementation(AlternativeTouchable.class);
        container.registerComponentImplementation(DecoratedTouchable.class,
                                                  DecoratedTouchable.class,
                                                  new Parameter[] { new Not(new IsType(SimpleTouchable.class)) });
        container.registerComponentImplementation(DependsOnList.class,
                                                  DependsOnList.class,
                                                  new Parameter[] {
            new CollectionConstraint(new Or(new IsType(AlternativeTouchable.class),
                                            new IsType(DecoratedTouchable.class)))
        });

        DependsOnList dol = (DependsOnList) container.getComponentInstance(DependsOnList.class);
        AlternativeTouchable at = (AlternativeTouchable) container.getComponentInstance(AlternativeTouchable.class);
        DecoratedTouchable dt = (DecoratedTouchable) container.getComponentInstance(DecoratedTouchable.class);
        assertNotNull(dol);
        List dependencies = dol.getDependencies();
        assertEquals(2, dependencies.size());
        assertTrue(dependencies.contains(at));
        assertTrue(dependencies.contains(dt));
    }

    public void testComponentDependsOnArrayOfEverythingElse() {
        container.registerComponentImplementation(DependsOnArray.class,
                                                  DependsOnArray.class,
                                                  new Parameter[] { new CollectionConstraint(Anything.ANYTHING) });
        DependsOnArray doa = (DependsOnArray) container.getComponentInstance(DependsOnArray.class);
        assertNotNull(doa);
        Object[] dependencies = doa.getDependencies();
        assertEquals(5, dependencies.length);
    }

}
