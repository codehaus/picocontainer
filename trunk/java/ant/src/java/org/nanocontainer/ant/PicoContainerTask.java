/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.picocontainer.defaults.BeanPropertyComponentAdapterFactory;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.DefaultLifecycleContainerBuilder;
import org.nanocontainer.reflection.DefaultReflectionContainerAdapter;
import org.nanocontainer.reflection.ReflectionContainerAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An Ant task that makes the use of PicoContainer possible from Ant.
 * When the task is executed, it will invoke <code>start()</code>
 * on all antSpecifiedComponents that have a public no-arg, non-static start method.
 * The antSpecifiedComponents's start() method (if it exists) will be invoked
 * in the order of instantiation.
 *
 * &lt;taskdef name="pico" classname="org.nanocontainer.ant.PicoContainerTask"/&gt;
 *
 * &lt;pico&gt;
 *    &lt;component classname="foo.Bar" someprop="somevalue"/&gt;
 *    &lt;component classname="ping.Pong"/&gt;
 * &lt;/pico&gt;
 *
 * Also note that bean/ant style properties can be set too. The above
 * usage will call <code>setSomeprop("somevalue")</code> on the
 * <code>foo.Bar</code> instance.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoContainerTask extends Task {
    private final List antSpecifiedComponents = new ArrayList();

    private final BeanPropertyComponentAdapterFactory propertyFactory =
            new BeanPropertyComponentAdapterFactory(new DefaultComponentAdapterFactory());

    // for subclasses
    protected ContainerComposer extraContainerComposer = null;

    private ContainerComposer containerComposer = new ContainerComposer() {
        public void composeContainer(MutablePicoContainer picoContainer, Object assemblyScope) {
            if(extraContainerComposer != null) {
                extraContainerComposer.composeContainer(picoContainer, assemblyScope);
            }

            // register components specified in Ant
            ReflectionContainerAdapter containerAdapter = new DefaultReflectionContainerAdapter(getClass().getClassLoader(), picoContainer);
            for (Iterator iterator = antSpecifiedComponents.iterator(); iterator.hasNext();) {
                Component component = (Component) iterator.next();

                // set the properties on the adapter factory
                // they will be set upon instantiation
                Object key = component.getKey();
                Map properties = component.getProperties();
                propertyFactory.setProperties(key, properties);

                try {
                    containerAdapter.registerComponentImplementation(component.getKey(), component.getClassname());
                } catch (Exception e) {
                    throw new BuildException(e);
                }
            }
        }
    };

    private ObjectReference containerRef = new SimpleReference();

    public void addComponent(Component component) {
        antSpecifiedComponents.add(component);
    }

    public void execute() {
		ContainerBuilder containerBuilder = new DefaultLifecycleContainerBuilder(containerComposer) {
			protected MutablePicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope) {
				return new DefaultPicoContainer(propertyFactory);
			}
		};
        try {
            containerBuilder.buildContainer(containerRef, null, null);
			containerBuilder.killContainer(containerRef);
        } catch (java.lang.reflect.UndeclaredThrowableException e) {
			Throwable ex = e.getUndeclaredThrowable();
			if(ex instanceof java.lang.reflect.InvocationTargetException) {
				ex = ((java.lang.reflect.InvocationTargetException) ex).getTargetException();
			}
            throw new BuildException(ex);
        } catch (Exception e) {
            throw new BuildException(e);
		}
    }
}
