/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.Disposable;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

import java.util.Arrays;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @author Mauro Talevi
 * @version $Revision$
 */
public class DelegatingLifecycleManagerTestCase extends MockObjectTestCase {

    StringBuffer events = new StringBuffer();
    Object one;
    Object two;
    Object three;

    Mock pico;
    PicoContainer node;

    class TestComponent implements Startable, Disposable{
        String code;

        public TestComponent(String code) {
            this.code = code;
        }

        public void start() {
            events.append("<" + code);
        }
        public void stop() {
            events.append(code + ">");
        }

        public void dispose() {
            events.append("!" + code);
        }

    }
    protected void setUp() throws Exception {
        one = new InstanceComponentAdapter(TestComponent.class, new TestComponent("One"));
        two = new InstanceComponentAdapter(TestComponent.class, new TestComponent("Two"));
        three = new InstanceComponentAdapter(TestComponent.class, new TestComponent("Three"));

        pico = mock(PicoContainer.class);
        node = (PicoContainer) pico.proxy();

    }

    public void testStartingInInOrder() {

        pico.expects(once()).method("getComponentAdapters").will(returnValue(Arrays.asList(new Object[] { one,two,three})));

        DelegatingLifecycleManager dlm = new DelegatingLifecycleManager();
        dlm.start(node);
        assertEquals("<One<Two<Three", events.toString());
    }

    public void testStoppingInInOrder() {

        pico.expects(once()).method("getComponentAdapters").will(returnValue(Arrays.asList(new Object[] { one,two,three})));

        DelegatingLifecycleManager dlm = new DelegatingLifecycleManager();
        dlm.stop(node);
        assertEquals("Three>Two>One>", events.toString());
    }

    public void testDisposingInInOrder() {

        pico.expects(once()).method("getComponentAdapters").will(returnValue(Arrays.asList(new Object[] { one,two,three})));

        DelegatingLifecycleManager dlm = new DelegatingLifecycleManager();
        dlm.dispose(node);
        assertEquals("!Three!Two!One", events.toString());
    }

}
