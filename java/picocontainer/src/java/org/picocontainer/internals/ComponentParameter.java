package org.picocontainer.internals;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.UnsatisfiedDependencyInstantiationException;

import java.io.Serializable;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class ComponentParameter implements Parameter, Serializable {

    private Object componentKey;

    public ComponentParameter() {
    }

    public ComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    public Object resolve(ComponentRegistry componentRegistry, ComponentAdapter compSpec, Class requestedType)
            throws PicoInitializationException {

        // TODO jon, shame on you, this is messy, tidy it up! -- jon

        Object componentInstance;
        ComponentAdapter componentSpecification = null;

        if (componentKey != null) {

            // is specified dependency already instantiated?
            componentInstance = componentRegistry.getComponentInstance(componentKey);

            if (componentInstance == null) {
                // try to find the component based on the key
                componentSpecification = componentRegistry.getComponentAdapter(componentKey);
            }

        } else {

            // try to find it directly using the requestType as the key
            componentInstance = componentRegistry.getComponentInstance(requestedType);

            if (componentInstance == null) {

                // is there already an instantiated instance that satisfies the dependency?
                componentInstance = componentRegistry.findImplementingComponent(requestedType);


                if (componentInstance == null) {
                    // try to find components that satisfy the interface (implements the component service asked for)
                    componentSpecification = componentRegistry.findImplementingComponentAdapter(requestedType);
                }
            }

        }

        if (componentSpecification != null) {
            // if the component does not exist yet, instantiate it
            componentInstance = componentRegistry.createComponent(componentSpecification);
        }

        if (componentInstance == null) {
            throw new UnsatisfiedDependencyInstantiationException(compSpec.getComponentImplementation(), componentKey, requestedType);
        }

        return componentInstance;
    }
}
