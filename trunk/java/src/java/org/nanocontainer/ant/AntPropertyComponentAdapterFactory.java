package org.nanocontainer.ant;

import org.picocontainer.extras.BeanPropertyComponentAdapterFactory;
import org.picocontainer.internals.ComponentAdapterFactory;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.Parameter;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoInitializationException;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AntPropertyComponentAdapterFactory extends BeanPropertyComponentAdapterFactory {
    private PicoContainerTask task;

    public AntPropertyComponentAdapterFactory(ComponentAdapterFactory delegate, PicoContainerTask task) {
        super(delegate);
        this.task = task;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters) throws PicoIntrospectionException {
        return new BeanPropertyComponentAdapterFactory.Adapter(super.createComponentAdapter(componentKey, componentImplementation, parameters)) {
            public Object instantiateComponent(ComponentRegistry componentRegistry) throws PicoInitializationException {
                // Ask the corresponding Ant Component object to set its properties
                // (passed via Ant) on us. Super will then set them on the object.
                String componentKey = (String) getComponentKey();
                Component component = task.findComponent(componentKey);
                component.setPropertiesOn(this);
                return super.instantiateComponent(componentRegistry);
            }
        };
    }
}