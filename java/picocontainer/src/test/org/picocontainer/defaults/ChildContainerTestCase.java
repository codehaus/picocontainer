/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import junit.framework.TestCase;
import junit.framework.Assert;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ChildContainerTestCase extends TestCase {
    public static abstract class Xxx implements Startable, Disposable {

        public static String componentRecorder = "";

        public void start() {
            componentRecorder += "<" + code();
        }

        public void stop() {
            componentRecorder += code() + ">";
        }

        public void dispose() {
            componentRecorder += "!" + code();
        }

        private String code() {
            String name = getClass().getName();
            return name.substring(name.indexOf('$')+1,name.length());
        }

        public static class A extends Xxx {}
        public static class B extends Xxx {
            public B(A a) {
                Assert.assertNotNull(a);
            }
        }
        public static class C extends Xxx {}
    }


	public void testFOO() {}

    public void TODO_testComponentsAreInstantiatedBreadthFirst() {
//        <container>
//              <component class='org.picocontainer.tck.Xxx$A'/>
//              <container>
//                  <component class='org.picocontainer.tck.Xxx$B'/>
//              </container>
//              <component class='org.picocontainer.tck.Xxx$C'/>
//        </container>

        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.registerComponentImplementation(Xxx.A.class);
        parent.registerComponentImplementation(DefaultPicoContainer.class);
        parent.registerComponentImplementation(Xxx.C.class);
        MutablePicoContainer child = (MutablePicoContainer) parent.getComponentInstance(DefaultPicoContainer.class);
        assertNotSame(parent, child);
        assertSame(parent, child.getParent());
        child.registerComponentImplementation(Xxx.B.class);

        parent.start();
//        nano.stopComponentsDepthFirst();
//        nano.disposeComponentsDepthFirst();

        assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", Xxx.componentRecorder);
//        assertEquals("Should match the expression", "*A*B+A_started+B_started+B_stopped+A_stopped+B_disposed+A_disposed", MockMonitor.monitorRecorder);
    }
}
