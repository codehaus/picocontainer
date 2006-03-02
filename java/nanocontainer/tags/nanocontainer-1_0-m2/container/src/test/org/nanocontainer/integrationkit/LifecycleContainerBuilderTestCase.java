/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.integrationkit;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class LifecycleContainerBuilderTestCase extends MockObjectTestCase {
    public void testBuildContainerCreatesANewChildContainerAndStartsItButNotTheParent() {
        final Mock childStartable = mock(Startable.class);
        childStartable.expects(once()).method("start").withNoArguments();
        childStartable.expects(once()).method("stop").withNoArguments();

        ContainerComposer containerComposer = new ContainerComposer() {
            public void composeContainer(MutablePicoContainer container, Object assemblyScope) {
                container.registerComponentInstance(childStartable.proxy());
            }
        };
        LifecycleContainerBuilder builder = new DefaultLifecycleContainerBuilder(containerComposer);

        ObjectReference parentRef = new SimpleReference();
        MutablePicoContainer parent = new DefaultPicoContainer();

        Mock parentStartable = mock(Startable.class);
        parent.registerComponentInstance(parentStartable.proxy());
        parentRef.set(parent);

        ObjectReference childRef = new SimpleReference();

        builder.buildContainer(childRef, parentRef, null, true);
        PicoContainer childContainer = (PicoContainer) childRef.get();
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, childContainer.getParent());

        builder.killContainer(childRef);
    }

}
