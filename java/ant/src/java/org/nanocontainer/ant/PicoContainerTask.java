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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.DefaultLifecycleContainerBuilder;
import org.nanocontainer.reflection.DefaultReflectionContainerAdapter;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.nanocontainer.reflection.ReflectionContainerAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.BeanPropertyComponentAdapter;
import org.picocontainer.defaults.BeanPropertyComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * An Ant task that makes the use of PicoContainer possible from Ant.
 * When the task is executed, it will invoke <code>start()</code>
 * on all antSpecifiedComponents that have a public no-arg, non-static start method.
 * The antSpecifiedComponents's start() method (if it exists) will be invoked
 * in the order of instantiation.
 * <p/>
 * &lt;taskdef name="pico" classname="org.nanocontainer.ant.PicoContainerTask"/&gt;
 * <p/>
 * &lt;pico&gt;
 * &lt;component classname="foo.Bar" someprop="somevalue"/&gt;
 * &lt;component classname="ping.Pong"/&gt;
 * &lt;/pico&gt;
 * <p/>
 * Also note that bean/ant style properties can be set too. The above
 * usage will call <code>setSomeprop("somevalue")</code> on the
 * <code>foo.Bar</code> instance.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoContainerTask extends Task {
    private final List antSpecifiedComponents = new ArrayList();

    // for subclasses
    protected ContainerComposer extraContainerComposer = null;

    private ContainerComposer containerComposer = new ContainerComposer() {
        public void composeContainer(SoftCompositionPicoContainer picoContainer, Object assemblyScope) {
            if (extraContainerComposer != null) {
                extraContainerComposer.composeContainer(picoContainer, assemblyScope);
            }

            // register components specified in Ant
            ReflectionContainerAdapter containerAdapter = new DefaultReflectionContainerAdapter(getClass().getClassLoader(), picoContainer);
            for (Iterator iterator = antSpecifiedComponents.iterator(); iterator.hasNext();) {
                Component component = (Component) iterator.next();
                try {
                    BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) containerAdapter.registerComponentImplementation(component.getKey(), component.getClassname());
                    adapter.setProperties(component.getProperties());
                } catch (ClassNotFoundException e) {
                    throw new BuildException("Class Not Found: " + e.getMessage(), e);
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
            BeanPropertyComponentAdapterFactory propertyFactory =
                    new BeanPropertyComponentAdapterFactory(new DefaultComponentAdapterFactory());

            protected SoftCompositionPicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope) {
                return new DefaultSoftCompositionPicoContainer(propertyFactory);
            }
        };
        try {
            containerBuilder.buildContainer(containerRef, null, null, true);
            containerBuilder.killContainer(containerRef);
        } catch (java.lang.reflect.UndeclaredThrowableException e) {
            Throwable ex = e.getUndeclaredThrowable();
            if (ex instanceof java.lang.reflect.InvocationTargetException) {
                ex = ((java.lang.reflect.InvocationTargetException) ex).getTargetException();
            }
            throw new BuildException(ex);
        } catch (RuntimeException e) {
            throw new BuildException(e);
        }
    }
}
