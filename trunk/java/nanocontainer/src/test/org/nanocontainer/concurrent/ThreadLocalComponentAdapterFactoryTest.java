/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.concurrent;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Test ThreadLocalComponentAdapterFactory.
 *
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalComponentAdapterFactoryTest
        extends TestCase {


    /**
     * Test method createComponentAdapter.
     *
     * @throws InterruptedException
     */
    public final void testCreateComponentAdapter() throws InterruptedException {
        final ComponentAdapterFactory componentAdapterFactory = new ThreadLocalComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory());
        final ComponentAdapter componentAdapter = componentAdapterFactory
                .createComponentAdapter(List.class, ArrayList.class, new Parameter[]{});
        final List list = (List) componentAdapter.getComponentInstance();
        list.add(this);
        final List list2 = new ArrayList();
        final Thread thread = new Thread(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                list2.addAll(list);
                list2.add(Thread.currentThread());
            }
        }, "junit");
        thread.start();
        thread.join();
        assertEquals(1, list2.size());
        assertSame(thread, list2.get(0));
    }
}

