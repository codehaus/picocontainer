/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.NameBinding;
import org.picocontainer.PicoVisitor;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.DecoratedTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * Test some <code>Constraint</code>s.
 *
 * @author Nick Sieger
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class ConstraintsTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();

	private MutablePicoContainer container;

	@Before
    public void setUp() throws Exception {        
        container = new DefaultPicoContainer(new Caching());
        container.addComponent(SimpleTouchable.class);
        container.addComponent(DecoratedTouchable.class);
        container.addComponent(AlternativeTouchable.class);
        container.addComponent(DependsOnTouchable.class);
    }

    @Test public void testIsKeyConstraint() {
        Constraint c = new IsKey(SimpleTouchable.class);

        Object object = c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class, (NameBinding) null),
                Touchable.class, null, false, null);
        assertEquals(SimpleTouchable.class, object.getClass());
    }

    @Test public void testIsTypeConstraint() {
        Constraint c = new IsType(AlternativeTouchable.class);

        Object object = c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class, (NameBinding) null),
                Touchable.class, null, false, null);
        assertEquals(AlternativeTouchable.class, object.getClass());
    }

    @Test public void testIsKeyTypeConstraint() {
        container.addComponent("Simple", SimpleTouchable.class);
        container.addComponent(5, SimpleTouchable.class);
        container.addComponent(Boolean.TRUE, SimpleTouchable.class);
        Touchable t = (Touchable) container.getComponent(Boolean.TRUE);
        
        Constraint c = new IsKeyType(Boolean.class);

        assertSame(t, c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class, (NameBinding) null),
                Touchable.class, null, false, null));
    }

    @Test public void testConstraintTooBroadThrowsAmbiguityException() {
        Constraint c = new IsType(Touchable.class);

        try {
            c.resolveInstance(container, 
                    container.getComponentAdapter(DependsOnTouchable.class, (NameBinding) null),
                    Touchable.class, null, false, null);
            fail("did not throw ambiguous resolution exception");
        } catch (AbstractInjector.AmbiguousComponentResolutionException acre) {
            // success
        }
    }

    @Test public void testFindCandidateConstraintsExcludingOneImplementation() {
        Constraint c = 
            new CollectionConstraint(
                new And(new IsType(Touchable.class),
                new Not(new IsType(DecoratedTouchable.class))));
        Touchable[] touchables = c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class,(NameBinding) null),
                Touchable[].class, null, false, null);
        assertEquals(2, touchables.length);
        for (Touchable touchable : touchables) {
            assertFalse(touchable instanceof DecoratedTouchable);
        }
    }
    
    @Test public void testCollectionChildIdVisitedBreadthFirst() {
        final Constraint c1  = mockery.mock(Constraint.class, "constraint 1");
        final Constraint c = new CollectionConstraint(c1);
        final PicoVisitor visitor = mockery.mock(PicoVisitor.class);
        final Sequence sequence = mockery.sequence("contraints");
        mockery.checking(new Expectations(){{
        	one(visitor).visitParameter(with(same(c))); inSequence(sequence);
        	one(c1).accept(visitor);  inSequence(sequence);
        }});
        
        c.accept(visitor);
    }
}
