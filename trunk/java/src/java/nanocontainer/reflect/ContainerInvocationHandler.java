package nanocontainer.reflect;

import picocontainer.PicoContainer;
import picocontainer.Container;

import java.lang.reflect.InvocationHandler;

/**
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public abstract class ContainerInvocationHandler implements InvocationHandler {
    private Container container;

    protected ContainerInvocationHandler( Container container ) {
        this.container = container;
    }

    protected Container getContainer() {
        return container;
    }
}
