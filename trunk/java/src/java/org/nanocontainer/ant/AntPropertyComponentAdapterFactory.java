package org.nanocontainer.ant;

import org.picocontainer.extras.BeanPropertyComponentAdapterFactory;
import org.picocontainer.extras.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.*;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoInitializationException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AntPropertyComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    private PicoContainerTask task;

    public AntPropertyComponentAdapterFactory(ComponentAdapterFactory delegate, PicoContainerTask task) {
        super(delegate);
        this.task = task;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new BeanPropertyComponentAdapterFactory.Adapter(super.createComponentAdapter(componentKey, componentImplementation, parameters)) {
            public Object getComponentInstance(AbstractPicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
                // Ask the corresponding Ant Component object to set its properties
                // (passed via Ant) on us. Super will then set them on the object.
                String componentKey = (String) getComponentKey();
                Component component = task.findComponent(componentKey);
                component.setPropertiesOn(this);
                return super.getComponentInstance(picoContainer);
            }
        };
    }
}