package org.nanocontainer.ant;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.internals.ComponentRegistry;
import org.nanocontainer.StringRegistrationNanoContainer;
import org.nanocontainer.StringRegistrationNanoContainerImpl;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.BuildException;

import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * An Ant task that makes the use of PicoContainer possible from Ant.
 * When the task is executed, it will invoke <code>execute()</code>
 * on all components that have a public no-arg, non-static execute method.
 * The components's execute() method (if it exists) will be invoked
 * in the order of instantiation.
 *
 * <taskdef name="pico" classname="org.nanocontainer.ant.PicoContainerTask"/>
 *
 * <pico>
 *    <component name="foo.Bar"/>
 *    <component name="ping.Pong"/>
 * </pico>
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoContainerTask extends Task implements DynamicConfigurator {
    private StringRegistrationNanoContainer picoContainer;
    private List antComponents = new ArrayList();
    private ComponentRegistry componentRegistry = new DefaultComponentRegistry();

    public PicoContainerTask() {
        picoContainer = new StringRegistrationNanoContainerImpl.WithClassLoaderAndComponentRegistry(
                getClass().getClassLoader(),
                componentRegistry
                );
    }

    public void setDynamicAttribute(String name, String value) throws BuildException {
    }

    public Object createDynamicElement(String elementName) {
        if("component".equals(elementName)) {
            return createAntComponent();
        }
        return null;
    }

    protected AntComponent createAntComponent() {
        AntComponent antComponent = new AntComponent();
        antComponents.add(antComponent);
        return antComponent;
    }

    public void execute() {
        registerComponents();

        try {
            getPicoContainer().instantiateComponents();
        } catch (PicoInitializationException e) {
            throw new BuildException(e);
        }

        prepareComponents();

        executeComponents();
    }

    /**
     * Override this method if you need to prepare some components
     * before they are all executed.
     */
    protected void prepareComponents() {
    }

    private void executeComponents() {
        Collection instantiationOrderedComponents = componentRegistry.getOrderedComponents();
        for (Iterator iterator = instantiationOrderedComponents.iterator(); iterator.hasNext();) {
            Object component = iterator.next();
            Class componentClass = component.getClass();
            try {
                Method execute = componentClass.getMethod("execute", null);
                execute.invoke(component, null);
            } catch (NoSuchMethodException e) {
                // ignore
            } catch (SecurityException e) {
                throw new BuildException(e);
            } catch (IllegalAccessException e) {
                // ignore
            } catch (IllegalArgumentException e) {
                // ignore
            } catch (InvocationTargetException e) {
                throw new BuildException(e.getTargetException());
            }
        }
    }

    private void registerComponents() {
        for (Iterator iterator = antComponents.iterator(); iterator.hasNext();) {
            AntComponent antComponent = (AntComponent) iterator.next();

            try {
                picoContainer.registerComponent(antComponent.getClassName());
            } catch (PicoRegistrationException e) {
                throw new BuildException(e);
            } catch (ClassNotFoundException e) {
                throw new BuildException(e);
            } catch (PicoIntrospectionException e) {
                throw new BuildException(e);
            }
        }
    }

    public PicoContainer getPicoContainer() {
        return picoContainer;
    }

}
