package org.picoextras.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.picocontainer.extras.BeanPropertyComponentAdapterFactory;
import org.picoextras.integrationkit.ContainerComposer;
import org.picoextras.integrationkit.ContainerBuilder;
import org.picoextras.integrationkit.DefaultLifecycleContainerBuilder;
import org.picoextras.reflection.DefaultReflectionContainerAdapter;
import org.picoextras.reflection.ReflectionContainerAdapter;

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
 * &lt;taskdef name="pico" classname="org.picoextras.ant.PicoContainerTask"/&gt;
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
			protected MutablePicoContainer createContainer() {
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
