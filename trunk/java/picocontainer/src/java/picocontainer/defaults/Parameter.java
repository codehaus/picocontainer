package picocontainer.defaults;

import picocontainer.PicoContainer;
import picocontainer.PicoInitializationException;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public interface Parameter {
    Object resolve(PicoContainer picoContainer, ComponentSpecification compSpec, Class targetType)
            throws PicoInitializationException;
}
