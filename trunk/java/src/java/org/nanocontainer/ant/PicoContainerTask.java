package org.nanocontainer.ant;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.Parameter;
import org.picocontainer.internals.ComponentAdapter;
import org.nanocontainer.MethodInvoker;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

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
    private final RegistrationPicoContainer picoContainer;
    private final Map classNameToComponentMap = new HashMap();
    private AntComponentRegistry componentRegistry = new AntComponentRegistry();

    public final class AntComponentRegistry extends DefaultComponentRegistry {

        public Object createComponent(ComponentAdapter componentAdapter) throws PicoInitializationException {
            Object result = super.createComponent(componentAdapter);
            Component component = findComponent(result);
            if( component != null) {
                component.setPropertiesOn(result, getProject());
            }

            return result;
        }

        private Component findComponent(Object instance) {
            return (Component) classNameToComponentMap.get(instance.getClass().getName());
        }
    };

    private final AntComponentRegistry getAntComponentRegistry() {
        return componentRegistry;
    }

    protected RegistrationPicoContainer createRegistrationPicoContainer(ComponentRegistry componentRegistry) throws PicoRegistrationException, PicoIntrospectionException {
        return new DefaultPicoContainer.WithComponentRegistry(componentRegistry);
    }


    public PicoContainerTask() {
        try {
            picoContainer = createRegistrationPicoContainer(componentRegistry);
        } catch (PicoRegistrationException e) {
            throw new BuildException(e);
        } catch (PicoIntrospectionException e) {
            throw new BuildException(e);
        }
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
        classNameToComponentMap.put(component.getClassname(), component);
    }

    public final void execute() {
        registerComponentsSpecifiedInAnt();
        doExecute();
    }

    protected void doExecute() {
        try {
            getPicoContainer().instantiateComponents();
        } catch (PicoInitializationException e) {
            throw new BuildException(e);
        }

        try {
            configureComponents();
        } catch (Exception e) {
            throw new BuildException(e);
        }

        new MethodInvoker().invokeMethod("execute", getAntComponentRegistry());
    }

    protected void configureComponents() throws Exception {
    }

    private void registerComponentsSpecifiedInAnt() {
        for (Iterator iterator = classNameToComponentMap.values().iterator(); iterator.hasNext();) {
            Component componentdef = (Component) iterator.next();
            Parameter[] parameters = componentdef.getParameters();

            try {
                Class aClass = getClassLoader().loadClass(componentdef.getClassname());
                if (parameters != null) {
                    picoContainer.registerComponent(aClass, aClass, parameters);
                } else {
                    picoContainer.registerComponent(aClass, aClass);
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
}
