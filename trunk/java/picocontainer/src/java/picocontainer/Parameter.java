package picocontainer;

import picocontainer.PicoContainer;
import picocontainer.PicoInitializationException;
import picocontainer.defaults.ComponentSpecification;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public interface Parameter {
    Object resolve(PicoContainer picoContainer, ComponentSpecification compSpec, Class targetType)
            throws PicoInitializationException;
}
