package picocontainer.defaults;

import picocontainer.PicoContainer;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoIntrospectionException;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class ComponentParameter implements Parameter {
    public Object resolve(PicoContainer picoContainer, ComponentSpecification compSpec, Class requestedType)
            throws PicoInstantiationException, PicoIntrospectionException {
        // TODO figure out a way to remove this ugly cast?
        Object value = ((DefaultPicoContainer) picoContainer).createComponent(requestedType);

        if (value == null) {
            throw new UnsatisfiedDependencyInstantiationException(compSpec.getComponentImplementation(), requestedType);
        }

        return value;
    }
}
