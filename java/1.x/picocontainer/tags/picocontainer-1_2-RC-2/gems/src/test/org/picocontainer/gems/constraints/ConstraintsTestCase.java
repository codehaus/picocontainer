/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.DecoratedTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Test some <code>Constraint</code>s.
 *
 * @author Nick Sieger
 * @author J&ouml;rg Schaible
 * @version 1.1
 */
public class ConstraintsTestCase extends MockObjectTestCase {

    MutablePicoContainer container;

    protected void setUp() throws Exception {
        super.setUp();
        
        container = new DefaultPicoContainer();
        container.registerComponentImplementation(SimpleTouchable.class);
        container.registerComponentImplementation(DecoratedTouchable.class);
        container.registerComponentImplementation(AlternativeTouchable.class);
        container.registerComponentImplementation(DependsOnTouchable.class);
    }

    public void testIsKeyConstraint() {
        Constraint c = new IsKey(SimpleTouchable.class);

        Object object = c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class),
                Touchable.class);
        assertEquals(SimpleTouchable.class, object.getClass());
    }

    public void testIsTypeConstraint() {
        Constraint c = new IsType(AlternativeTouchable.class);

        Object object = c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class),
                Touchable.class);
        assertEquals(AlternativeTouchable.class, object.getClass());
    }

    public void testIsKeyTypeConstraint() {
        container.registerComponentImplementation("Simple", SimpleTouchable.class);
        container.registerComponentImplementation(new Integer(5), SimpleTouchable.class);
        container.registerComponentImplementation(Boolean.TRUE, SimpleTouchable.class);
        Touchable t = (Touchable) container.getComponentInstance(Boolean.TRUE);
        
        Constraint c = new IsKeyType(Boolean.class);

        assertSame(t, c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class),
                Touchable.class));
    }

    public void testConstraintTooBroadThrowsAmbiguityException() {
        Constraint c = new IsType(Touchable.class);

        try {
            c.resolveInstance(container, 
                    container.getComponentAdapter(DependsOnTouchable.class),
                    Touchable.class);
            fail("did not throw ambiguous resolution exception");
        } catch (AmbiguousComponentResolutionException acre) {
            // success
        }
    }

    public void testFindCandidateConstraintsExcludingOneImplementation() {
        Constraint c = 
            new CollectionConstraint(
                new And(new IsType(Touchable.class),
                new Not(new IsType(DecoratedTouchable.class))));
        Touchable[] touchables = (Touchable[]) c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class),
                Touchable[].class);
        assertEquals(2, touchables.length);
        for (int i = 0; i < touchables.length; i++) {
            assertFalse(touchables[i] instanceof DecoratedTouchable);
        }
    }
    
    public void testCollectionChildIdVisitedBreadthFirst() {
        Mock             mockVisior = mock(PicoVisitor.class);
        PicoVisitor     visitor = (PicoVisitor) mockVisior.proxy();

        Mock       mockC1 = mock(Constraint.class, "constraint 1");
        Constraint c1     = (Constraint) mockC1.proxy();

        Constraint c = new CollectionConstraint(c1);
        
        mockVisior.expects(once()).method("visitParameter")
            .with(same(c)).id("v");
        mockC1.expects(once()).method("accept")
            .with(same(visitor)).after(mockVisior, "v");
        
        c.accept(visitor);
    }
}
