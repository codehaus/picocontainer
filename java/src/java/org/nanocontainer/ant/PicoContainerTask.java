package org.nanocontainer.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.extras.InvokingComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.internals.ComponentAdapterFactory;
import org.picocontainer.internals.Parameter;

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
    private final Map keyToComponentMap = new HashMap();

    private RegistrationPicoContainer picoContainer;
    private Class delegateComponentAdapterFactoryClass = DefaultComponentAdapterFactory.class;

    public void setComponentAdapterFactoryClass(Class factoryClass) {
        this.delegateComponentAdapterFactoryClass = factoryClass;
    }

    private final ComponentAdapterFactory createComponentAdapterFactory() {
        // We're nesting several component factories:
        // - A default one that does instantiation
        // - A Bean property one that sets properties
        // - An invoking one that calls execute()

        try {
            ComponentAdapterFactory instantiator = (ComponentAdapterFactory) delegateComponentAdapterFactoryClass.newInstance();
            log("using ComponentAdapterFactory " + delegateComponentAdapterFactoryClass.getName());

            AntPropertyComponentAdapterFactory propertySetter =
                    new AntPropertyComponentAdapterFactory(instantiator, this);

            InvokingComponentAdapterFactory executor = new InvokingComponentAdapterFactory(
                    propertySetter,
                    "execute",
                    null,
                    null);

            return executor;
        } catch (Exception e) {
            throw new BuildException(
                    "Could not instantiate ComponentAdapterFactory " + delegateComponentAdapterFactoryClass, e);
        }
    }

    protected DefaultPicoContainer createPicoContainer(ComponentAdapterFactory componentAdapterFactory)
            throws PicoRegistrationException, PicoIntrospectionException {
        return new DefaultPicoContainer.WithComponentAdapterFactory(componentAdapterFactory);
    }

    /**
     * Convenience method for subclasses and tests that wish to register components
     * programatically.
     */
    protected Component registerComponent(Class componentClass) {
        Component component = new Component();
        component.setClassname(componentClass.getName());
        addConfiguredComponent(component);
        return component;
    }

    public void addConfiguredComponent(Component component) {
        keyToComponentMap.put(component.getKey(), component);
    }

    public final void execute() {
        registerComponentsSpecifiedInAnt();
        try {
            picoContainer.getComponents();
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    private void registerComponentsSpecifiedInAnt() {
        ComponentAdapterFactory componentAdapterFactory = createComponentAdapterFactory();
        picoContainer = new DefaultPicoContainer.WithComponentAdapterFactory(componentAdapterFactory);

        for (Iterator iterator = keyToComponentMap.values().iterator(); iterator.hasNext();) {
            Component componentdef = (Component) iterator.next();
            Parameter[] parameters = componentdef.getParameters();

            try {
                Class aClass = getClassLoader().loadClass(componentdef.getClassname());
                if (parameters != null) {
                    picoContainer.registerComponent(componentdef.getKey(), aClass, parameters);
                } else {
                    picoContainer.registerComponent(componentdef.getKey(), aClass);
                }
            } catch (PicoRegistrationException e) {
                throw new BuildException(e);
            } catch (ClassNotFoundException e) {
                throw new BuildException(e);
            } catch (PicoIntrospectionException e) {
                throw new BuildException(e);
            }
        }
    }

    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        return classLoader;
    }

    public PicoContainer getPicoContainer() {
        return picoContainer;
    }

    // Callback from the AntPropertyComponentAdaptoerFactory
    Component findComponent(String componentKey) {
        return (Component) keyToComponentMap.get(componentKey);
    }
}
