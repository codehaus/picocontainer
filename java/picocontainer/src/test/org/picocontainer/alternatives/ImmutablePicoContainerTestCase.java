/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.alternatives;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.Disposable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.VerifyingVisitor;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImmutablePicoContainerTestCase extends MockObjectTestCase {

    public void testImmutingofNullBarfs() {
        try {
            new ImmutablePicoContainer(null);
            fail("Should have barfed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    public void testDelegationOfGettingComponentInstance() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        Map map = (Map) ipc.getComponentInstance(Map.class);
        assertNotNull(map);
    }

    public void testDelegationOfGettingComponentInstanceOfType() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        Map map = (Map) ipc.getComponentInstanceOfType(Map.class);
        assertNotNull(map);
    }

    public void testDelegationOfGettingComponentInstancesOfType() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        mpc.registerComponentImplementation(Collection.class, Vector.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        List list = ipc.getComponentInstancesOfType(Map.class);
        assertNotNull(list);
        assertEquals(1,list.size());
    }

    public void testDelegationOfGetComponentInstances() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        List comps = ipc.getComponentInstances();
        assertNotNull(comps);
        assertEquals(1, comps.size());
    }

    public void testDelegationOfGetComponentAdapter() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        ComponentAdapter ca = ipc.getComponentAdapter(Map.class);
        assertNotNull(ca);

    }

    public void testDelegationOfGetComponentAdapterOfType() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        ComponentAdapter ca = ipc.getComponentAdapterOfType(Map.class);
        assertNotNull(ca);
    }

    public void testDelegationOfGetComponentAdapters() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        Collection comps = ipc.getComponentAdapters();
        assertNotNull(comps);
        assertEquals(1, comps.size());
    }

    public void testDelegationOfGetComponentAdaptersOfType() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        List comps = ipc.getComponentAdaptersOfType(Map.class);
        assertNotNull(comps);
        assertEquals(1, comps.size());
    }

    public static class UnsatisfiableIterator implements Iterator {

        public UnsatisfiableIterator(Map map) {
        }

        public void remove() {
        }

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            return null;
        }
    }

    public void testDelegationOfVerify() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Iterator.class, UnsatisfiableIterator.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        try {
            ipc.accept(new VerifyingVisitor());
            fail("wrong!");
        } catch (PicoVerificationException e) {
            // expected
        }
    }

    public void testGetParentForMutable() {
        DefaultPicoContainer par = new DefaultPicoContainer();
        DefaultPicoContainer mpc = new DefaultPicoContainer(par);
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        PicoContainer parent = ipc.getParent();
        assertNotNull(parent);
        assertNotSame(par, parent);
        PicoContainer parent2 = ipc.getParent();
        assertNotNull(parent2);
        assertEquals(parent, parent2);
    }

    public void testGetParentForNonMutable() {
        DefaultPicoContainer par = new DefaultPicoContainer();
        ImmutablePicoContainer par2 = new ImmutablePicoContainer(par);
        DefaultPicoContainer mpc = new DefaultPicoContainer(par2);
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        PicoContainer parent = ipc.getParent();
        assertNotNull(parent);
        assertNotSame(par, parent);
        PicoContainer parent2 = ipc.getParent();
        assertNotNull(parent2);
        assertEquals(parent, parent2);
    }

    public void testStartBarfs() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        try {
            ipc.start();
            fail("should have barfed");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testStopBarfs() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        try {
            ipc.stop();
            fail("stop have barfed");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testDisposeBarfs() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(Map.class, HashMap.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        try {
            ipc.dispose();
            fail("should have barfed");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public static class MyDisposable implements Disposable {
        public boolean disposed;

        public void dispose() {
            disposed = true;
        }
    }

    public void testLifecycleGuardIsEasyToCircumventSoItMightAsWellBeDeleted() {
        DefaultPicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponentImplementation(MyDisposable.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(mpc);
        List componentInstances = ipc.getComponentInstances();
        for (Iterator iterator = componentInstances.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            if(o instanceof Disposable) {
                ((Disposable) o).dispose();
            }
        }
        MyDisposable disposable = (MyDisposable ) ipc.getComponentInstance(MyDisposable.class);
        assertTrue(disposable.disposed);
    }

    public void testFacetiouslyThatLifeCycleGuardPreventsCyclingOfChildContainersAsComponentsAreNotTheOnlyThingsThatAreLifecycleable() {
        DefaultPicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = parent.makeChildContainer();
        parent.registerComponentImplementation("foo", MyDisposable.class);
        child.registerComponentImplementation("bar", MyDisposable.class);
        ImmutablePicoContainer ipc = new ImmutablePicoContainer(parent);
        try {
            ipc.dispose();
            fail("Should have barfed");
        } catch (UnsupportedOperationException e) {
            // expected
        }

        MyDisposable parentDisposable = (MyDisposable) parent.getComponentInstance("foo");
        assertFalse(parentDisposable.disposed);

        MyDisposable childDisposable = (MyDisposable) child.getComponentInstance("bar");
        assertFalse(childDisposable.disposed);

        // If this were on parent, it would cascade to child.
        ((Disposable) ipc.getComponentInstances().get(0)).dispose();

        // I can live with this below....
        assertTrue(parentDisposable.disposed);

        // However, I'd be in favor of changing
        //   interface PicoContainer extends Startable, Disposable {}
        // to
        //   interface PicoContainer {}
        // AND
        //   interface MutablePicoContainer extends PicoContainer {}
        // to
        //   interface MutablePicoContainer extends PicoContainer implements Startable, Disposable {}
        // That despite breaking (marginally) backwards compatability.
        // - Paul
    }

    public void testVisitingOfImmutableContainerWorks() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        Object foo = new Object();
        ComponentAdapter componentAdapter = pico.registerComponentInstance(foo);

        Mock fooVisitor = new Mock(PicoVisitor.class);
        fooVisitor.expects(atLeastOnce()).method("isReverseTraversal").withNoArguments().will(returnValue(false));
        fooVisitor.expects(once()).method("visitContainer").with(eq(pico));
        fooVisitor.expects(once()).method("visitComponentAdapter").with(eq(componentAdapter));

        ImmutablePicoContainer ipc = new ImmutablePicoContainer(pico);

        ipc.accept((PicoVisitor) fooVisitor.proxy());


    }
}
