package org.nanocontainer.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.picocontainer.extras.InvokingComponentAdapterFactory;
import org.picocontainer.extras.BeanPropertyComponentAdapterFactory;
import org.picocontainer.defaults.*;
import org.picocontainer.Parameter;
import org.picocontainer.MutablePicoContainer;

import java.util.*;

/**
 * An Ant task that makes the use of PicoContainer possible from Ant.
 * When the task is executed, it will invoke <code>execute()</code>
 * on all components that have a public no-arg, non-static execute method.
 * The components's execute() method (if it exists) will be invoked
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
    private final List components = new ArrayList();

    private final BeanPropertyComponentAdapterFactory propertyFactory;
    private final MutablePicoContainer pico;

    public PicoContainerTask() {
        DefaultComponentAdapterFactory defaultFactory = new DefaultComponentAdapterFactory();
        propertyFactory = new BeanPropertyComponentAdapterFactory(defaultFactory);
        InvokingComponentAdapterFactory invokingFactory = new InvokingComponentAdapterFactory(
                    propertyFactory,
                    "execute",
                    null,
                    null
            );
        pico = new DefaultPicoContainer(invokingFactory);
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void execute() {
        registerComponentsSpecifiedInAnt();
        try {
            getPicoContainer().getComponentInstances();
        } catch (PicoInvocationTargetInitializationException e) {
            throw new BuildException(e.getCause());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    private void registerComponentsSpecifiedInAnt() {

        for (Iterator iterator = components.iterator(); iterator.hasNext();) {
            Component component = (Component) iterator.next();

            // set the properties on the adapter factory
            // they will be set upon instantiation
            Object key = component.getKey();
            Map properties = component.getProperties();
            propertyFactory.setProperties(key, properties);

            Parameter[] parameters = component.getParameters();

            try {
                Class aClass = getClassLoader().loadClass(component.getClassname());
                MutablePicoContainer picoContainer = (MutablePicoContainer) getPicoContainer();
                if (parameters != null) {
                    picoContainer.registerComponentImplementation(component.getKey(), aClass, parameters);
                } else {
                    picoContainer.registerComponentImplementation(component.getKey(), aClass);
                }
            } catch (Exception e) {
                throw new BuildException(e);
            }
        }
    }

    private ClassLoader getClassLoader() {
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        return classLoader;
    }

    public MutablePicoContainer getPicoContainer() {
        return pico;
    }
}
