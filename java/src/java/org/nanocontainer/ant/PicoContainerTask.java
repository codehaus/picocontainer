package org.nanocontainer.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoContainer;
import org.picocontainer.extras.InvokingComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.Parameter;
import org.picocontainer.MutablePicoContainer;

import java.util.*;

/**
 * An Ant task that makes the use of OldPicoContainer possible from Ant.
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

    private Class delegateComponentAdapterFactoryClass = DefaultComponentAdapterFactory.class;
    private MutablePicoContainer pico;

    public PicoContainerTask() {
        ComponentAdapterFactory componentAdapterFactory = createComponentAdapterFactory();
        pico = new DefaultPicoContainer(componentAdapterFactory);
    }

    public void setComponentAdapterFactoryClass(Class factoryClass) {
        this.delegateComponentAdapterFactoryClass = factoryClass;
    }

    private final ComponentAdapterFactory createComponentAdapterFactory() {
        // We're nesting several component adapter factories:
        // - A default one that does instantiation - pluggable from the outside
        // - A Bean property one that sets properties
        // - An invoking one that calls execute()

        try {
            ComponentAdapterFactory instantiator = (ComponentAdapterFactory) delegateComponentAdapterFactoryClass.newInstance();

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

    public void addConfiguredComponent(Component component) {
        keyToComponentMap.put(component.getKey(), component);
    }

    public final void execute() {
        registerComponentsSpecifiedInAnt();
        try {
            pico.getComponentInstances();
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    private void registerComponentsSpecifiedInAnt() {

        for (Iterator iterator = keyToComponentMap.values().iterator(); iterator.hasNext();) {
            Component componentdef = (Component) iterator.next();
            Parameter[] parameters = componentdef.getParameters();

            try {
                Class aClass = getClassLoader().loadClass(componentdef.getClassname());
                if (parameters != null) {
                    pico.registerComponentImplementation(componentdef.getKey(), aClass, parameters);
                } else {
                    pico.registerComponentImplementation(componentdef.getKey(), aClass);
                }
            } catch (PicoIntrospectionException e) {
                throw new BuildException(e);
            } catch (PicoRegistrationException e) {
                throw new BuildException(e);
            } catch (ClassNotFoundException e) {
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
        return pico;
    }

    // Callback from the AntPropertyComponentAdaptoerFactory
    Component findComponent(String componentKey) {
        return (Component) keyToComponentMap.get(componentKey);
    }
}
