package picocontainer.defaults;

import picocontainer.PicoContainer;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoIntrospectionException;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public interface Parameter {
    Object resolve(PicoContainer picoContainer, ComponentSpecification compSpec, Class targetType)
            throws PicoInstantiationException, PicoIntrospectionException;
}
