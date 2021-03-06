/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

/**
 * @author Michael Rimov
 */
public class TraversalCheckingVisitorTestCase extends TestCase {

    private MutablePicoContainer pico;

    private MutablePicoContainer child;

    private ComponentAdapter parentAdapter;

    private ComponentAdapter childAdapter;

    protected void setUp() throws Exception {
        super.setUp();

        pico = new DefaultPicoContainer();
        parentAdapter = pico.registerComponent(new SetterInjectionComponentAdapter(StringBuffer.class, StringBuffer.class, null));

        child = pico.makeChildContainer();

        childAdapter = child.registerComponent(new ConstructorInjectionComponentAdapter(ArrayList.class, ArrayList.class,
            new Parameter[] {
            new ConstantParameter(new Integer(3))
        }));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        child = null;
        pico = null;
        parentAdapter = null;
        childAdapter = null;
    }

    public void testVisitComponentAdapter() {
        final int numExpectedComponentAdapters = 2;
        final List allAdapters = new ArrayList();

        Set knownAdapters = new HashSet();
        knownAdapters.add(parentAdapter);
        knownAdapters.add(childAdapter);

        PicoVisitor containerCollector = new TraversalCheckingVisitor() {
            public void visitComponentAdapter(ComponentAdapter adapter) {
                super.visitComponentAdapter(adapter); //Calls checkTraversal for us.
                allAdapters.add(adapter);
            }
        };
        containerCollector.traverse(pico);

        assertEquals(numExpectedComponentAdapters, allAdapters.size());

        for (Iterator i = allAdapters.iterator(); i.hasNext(); ) {
            boolean knownAdapter = knownAdapters.remove(i.next());
            assertTrue("Encountered unknown adapter in collection: " + allAdapters.toString(), knownAdapter);
        }

        assertTrue("All adapters should match known adapters.", knownAdapters.size() == 0);
    }

    public void testVisitContainer() {
        final List allContainers = new ArrayList();
        final int expectedNumberOfContainers = 2;

        PicoVisitor containerCollector = new TraversalCheckingVisitor() {
            public void visitContainer(PicoContainer pico) {
                super.visitContainer(pico); //Calls checkTraversal for us.
                allContainers.add(pico);
            }
        };

        containerCollector.traverse(pico);

        assertTrue(allContainers.size() == expectedNumberOfContainers);

        Set knownContainers = new HashSet();
        knownContainers.add(pico);
        knownContainers.add(child);
        for (Iterator i = allContainers.iterator(); i.hasNext(); ) {
            PicoContainer oneContainer = (PicoContainer) i.next();
            boolean knownContainer = knownContainers.remove(oneContainer);
            assertTrue("Found a picocontainer that wasn't previously expected.", knownContainer);
        }

        assertTrue("All containers must match what is returned by traversal.",
            knownContainers.size() == 0);

    }


    public void testVisitParameter() {
        final List allParameters = new ArrayList();

        PicoVisitor containerCollector = new TraversalCheckingVisitor() {
            public void visitParameter(Parameter param) {
                super.visitParameter(param); //Calls checkTraversal for us.
                allParameters.add(param);
            }
        };

        containerCollector.traverse(pico);

        assertTrue(allParameters.size() == 1);
        assertTrue(allParameters.get(0) instanceof ConstantParameter);
        assertTrue( ( (ConstantParameter) allParameters.get(0)).resolveInstance(null, null, null) instanceof Integer);
        assertEquals(3, ( (Integer) ( (ConstantParameter) allParameters.get(0)).resolveInstance(null, null,
            null)).intValue());
    }

}    