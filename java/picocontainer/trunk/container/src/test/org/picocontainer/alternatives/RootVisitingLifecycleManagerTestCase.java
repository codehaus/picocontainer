/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.alternatives;

import org.picocontainer.LifecycleManager;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class RootVisitingLifecycleManagerTestCase extends MockObjectTestCase {

    public void testShouldVisitEveryComponentOnlyOnce() {
        Mock mockStartable1 = mock(Startable.class, "Startable1");
        Mock mockStartable2 = mock(Startable.class, "Startable2");

        LifecycleManager lifecycleManager = new RootVisitingLifecycleManager();
        MutablePicoContainer parent = new DefaultPicoContainer(lifecycleManager);
        MutablePicoContainer child = new DefaultPicoContainer(
                new DefaultComponentAdapterFactory(), parent, lifecycleManager);
        parent.addChildContainer(child);
        parent.registerComponentInstance(mockStartable1.proxy());
        child.registerComponentInstance(mockStartable2.proxy());

        mockStartable1.expects(once()).method("start").id("1");
        mockStartable2.expects(once()).method("start").after(mockStartable1, "1");

        parent.start();
    }
}
