/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoVisitor;
import org.picocontainer.testmodel.Touchable;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


/**
 * @author J&ouml;rg Schaible
 */
public class MethodCallingVisitorTest extends MockObjectTestCase {

    private Method add;
    private Method touch;

    protected void setUp() throws Exception {
        super.setUp();
        add = List.class.getMethod("add", new Class[]{Object.class});
        touch = Touchable.class.getMethod("touch", (Class[])null);
    }

    public void testVisitorWillTraverseAndCall() throws Exception {
        MutablePicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = new DefaultPicoContainer();
        parent.addChildContainer(child);
        parent.addComponent(List.class, LinkedList.class, new Parameter[0]);
        child.addComponent(List.class, LinkedList.class, new Parameter[0]);
        List parentList = (List)parent.getComponent(List.class);
        List childList = (List)child.getComponent(List.class);

        assertEquals(0, parentList.size());
        assertEquals(0, childList.size());

        PicoVisitor visitor = new MethodCallingVisitor(add, List.class, new Object[]{Boolean.TRUE});
        visitor.traverse(parent);

        assertEquals(1, parentList.size());
        assertEquals(1, childList.size());
    }

    public void testVisitsInInstantiationOrder() throws Exception {
        Mock mockTouchable1 = mock(Touchable.class);
        Mock mockTouchable2 = mock(Touchable.class);

        MutablePicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = new DefaultPicoContainer();
        parent.addChildContainer(child);
        parent.addComponent(mockTouchable1.proxy());
        child.addComponent(mockTouchable2.proxy());

        mockTouchable1.expects(once()).method("touch").id("1");
        mockTouchable2.expects(once()).method("touch").after(mockTouchable1, "1");

        PicoVisitor visitor = new MethodCallingVisitor(touch, Touchable.class, null);
        visitor.traverse(parent);
    }

    public void testVisitsInReverseInstantiationOrder() throws Exception {
        Mock mockTouchable1 = mock(Touchable.class);
        Mock mockTouchable2 = mock(Touchable.class);

        MutablePicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = new DefaultPicoContainer();
        parent.addChildContainer(child);
        parent.addComponent(mockTouchable1.proxy());
        child.addComponent(mockTouchable2.proxy());

        mockTouchable2.expects(once()).method("touch").id("1");
        mockTouchable1.expects(once()).method("touch").after(mockTouchable2, "1");

        PicoVisitor visitor = new MethodCallingVisitor(touch, Touchable.class, null, false);
        visitor.traverse(parent);
    }
}
