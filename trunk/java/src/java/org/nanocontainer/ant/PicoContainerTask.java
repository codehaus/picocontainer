package org.nanocontainer.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.extras.DecoratingComponentAdapter;
import org.picocontainer.extras.DecoratingComponentAdapterFactory;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.ComponentAdapterFactory;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.Parameter;
import org.nanocontainer.MethodInvoker;

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
    private DefaultPicoContainer picoContainer;
    private final Map classNameToComponentMap = new HashMap();
    private SetPropertiesComponentAdapterFactory componentAdapterFactory;
    private Class delegateComponentAdapterFactoryClass = DefaultComponentAdapterFactory.class;
    private List registeredComponents = new ArrayList();

    public final class SetPropertiesComponentAdapterFactory extends DecoratingComponentAdapterFactory {

        public SetPropertiesComponentAdapterFactory(ComponentAdapterFactory delegate) {
            super(delegate);
        }

        public ComponentAdapter createComponentAdapter(Object componentKey,
                                                       Class componentImplementation,
                                                       Parameter[] parameters) throws PicoIntrospectionException {
            ComponentAdapter componentAdapter = super.createComponentAdapter(componentKey, componentImplementation, parameters);

            return new DecoratingComponentAdapter(componentAdapter) {
                public Object instantiateComponent(ComponentRegistry componentRegistry)
                        throws PicoInitializationException {
                    Object comp = super.instantiateComponent(componentRegistry);
                    Component component = findComponent(comp);
                    if (component != null) {
                        component.setPropertiesOn(comp, getProject());
                    }

                    return comp;
                }
            };
        }

        private Component findComponent(Object instance) {
            return (Component) classNameToComponentMap.get(instance.getClass().getName());
        }
    }


    public void setComponentAdapterFactoryClass(Class factoryClass) {
        this.delegateComponentAdapterFactoryClass = factoryClass;
    }

    private final ComponentAdapterFactory getComponentAdapterFactory() {
        if (componentAdapterFactory == null) {
            ComponentAdapterFactory delegate;
            try {
                delegate = (ComponentAdapterFactory) delegateComponentAdapterFactoryClass.newInstance();
                log("using ComponentAdapterFactory " + delegate);
                componentAdapterFactory = new SetPropertiesComponentAdapterFactory(delegate);
            } catch (Exception e) {
                throw new BuildException(
                        "Could not instantiate ComponentAdapterFactory " + delegateComponentAdapterFactoryClass, e);
            }
        }
        return componentAdapterFactory;
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
        registeredComponents.add(component);
        classNameToComponentMap.put(component.getClassname(), component);
    }

    public final void execute() {
        registerComponentsSpecifiedInAnt();
        doExecute();
    }

    protected void doExecute() {
        try {
            getPicoContainer().getComponents();
            configureComponents();
        } catch (Exception e) {
            throw new BuildException(e);
        }

        new MethodInvoker().invokeMethod("execute", getPicoContainer().getComponentRegistry());
    }

    protected void configureComponents() throws Exception {
    }

    private void registerComponentsSpecifiedInAnt() {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            Component componentdef = (Component) iterator.next();
            Parameter[] parameters = componentdef.getParameters();

            try {
                Class aClass = getClassLoader().loadClass(componentdef.getClassname());
                if (parameters != null) {
                    getPicoContainer().registerComponent(aClass, aClass, parameters);
                } else {
                    getPicoContainer().registerComponent(aClass, aClass);
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

    public DefaultPicoContainer getPicoContainer() {
        if (picoContainer == null) {
            try {
                picoContainer = createPicoContainer(getComponentAdapterFactory());
            } catch (PicoRegistrationException e) {
                throw new BuildException(e);
            } catch (PicoIntrospectionException e) {
                throw new BuildException(e);
            }
        }

        return picoContainer;
    }
}
