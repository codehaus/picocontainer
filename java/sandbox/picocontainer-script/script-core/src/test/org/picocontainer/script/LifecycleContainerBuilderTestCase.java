/*******************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.script;

import static org.junit.Assert.assertNotSame;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.script.AbstractContainerBuilder;
import org.picocontainer.script.ContainerComposer;
import org.picocontainer.script.DefaultContainerBuilder;
import org.picocontainer.tck.MockFactory;

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class LifecycleContainerBuilderTestCase {

	private Mockery mockery = MockFactory.mockeryWithCountingNamingScheme();
	
    @Test public void testBuildContainerCreatesANewChildContainerAndStartsItButNotTheParent() {
        final Startable childStartable = mockery.mock(Startable.class);
        mockery.checking(new Expectations(){{
        	one(childStartable).start();
        	one(childStartable).stop();
        }});
        
        ContainerComposer containerComposer = new ContainerComposer() {
            public void composeContainer(MutablePicoContainer container, Object assemblyScope) {
                container.addComponent(childStartable);
            }
        };
        AbstractContainerBuilder builder = new DefaultContainerBuilder(containerComposer);

        MutablePicoContainer parent = new DefaultPicoContainer();

        final Startable parentStartable = mockery.mock(Startable.class);
        parent.addComponent(parentStartable);

        PicoContainer childContainer = builder.buildContainer(parent, null, true);
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, childContainer.getParent());

        builder.killContainer(childContainer);
    }

}
