package picocontainer.defaults;

import picocontainer.PicoContainer;
import picocontainer.PicoInstantiationException;
import picocontainer.Parameter;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class ConstantParameter implements Parameter {
    private Object arg;

    ConstantParameter(Object parameter) {
        this.arg = parameter;
    }

    public Object resolve(PicoContainer picoContainer, ComponentSpecification compSpec, Class targetType) throws PicoInstantiationException {
        return arg;
    }
}
