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
 * @version $Revision$
 */
public class DefaultLifecycleManagerTestCase extends MockObjectTestCase {

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
        one = new TestComponent("One");
        two = new TestComponent("Two");
        three = new TestComponent("Three");

        pico = mock(PicoContainer.class);
        node = (PicoContainer) pico.proxy();

    }

    public void testStartingInInOrder() {

        pico.expects(once()).method("getComponentInstancesOfType").with(same(Startable.class)).will(returnValue(Arrays.asList(new Object[] {one,two,three})));

        DefaultLifecycleManager dlm = new DefaultLifecycleManager();
        dlm.start(node);
        assertEquals("<One<Two<Three", events.toString());
    }

    public void testStoppingInInOrder() {

        pico.expects(once()).method("getComponentInstancesOfType").with(same(Startable.class)).will(returnValue(Arrays.asList(new Object[] {one,two,three})));

        DefaultLifecycleManager dlm = new DefaultLifecycleManager();
        dlm.stop(node);
        assertEquals("Three>Two>One>", events.toString());
    }

    public void testDisposingInInOrder() {

        pico.expects(once()).method("getComponentInstancesOfType").with(same(Disposable.class)).will(returnValue(Arrays.asList(new Object[] {one,two,three})));

        DefaultLifecycleManager dlm = new DefaultLifecycleManager();
        dlm.dispose(node);
        assertEquals("!Three!Two!One", events.toString());
    }

}
