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

import junit.framework.TestCase;
import org.jmock.Mock;
import org.jmock.C;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.picocontainer.Startable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class LifecycleContainerBuilderTestCase extends TestCase {
    public void testBuildContainerCreatesANewChildContainerAndStartsItButNotTheParent() {
        final Mock childStartable = new Mock(Startable.class);
        childStartable.expect("start", C.args());
        childStartable.expect("stop", C.args());

        ContainerComposer containerAssembler = new ContainerComposer() {
            public void composeContainer(MutablePicoContainer container, Object assemblyScope) {
                container.registerComponentInstance(childStartable.proxy());
            }
        };
        LifecycleContainerBuilder builder = new DefaultLifecycleContainerBuilder(containerAssembler);

        ObjectReference parentRef = new SimpleReference();
        MutablePicoContainer parentC = new DefaultPicoContainer();

        // Expect no calls on this one!
        Mock parentStartable = new Mock(Startable.class);
        parentStartable.expectAndReturn("equals", C.ANY_ARGS, Boolean.FALSE);
        parentC.registerComponentInstance(parentStartable.proxy());
        parentRef.set(parentC);

        ObjectReference childRef = new SimpleReference();

        builder.buildContainer(childRef, parentRef, null);
        PicoContainer childContainer = (PicoContainer) childRef.get();
        assertSame(parentC, childContainer.getParent());

        builder.killContainer(childRef);

        parentStartable.verify();
        childStartable.verify();
    }

}
