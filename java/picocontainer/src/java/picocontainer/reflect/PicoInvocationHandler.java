package picocontainer.reflect;

import picocontainer.PicoContainer;

import java.lang.reflect.InvocationHandler;

/**
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public abstract class PicoInvocationHandler implements InvocationHandler {
    private PicoContainer picoContainer;

    protected PicoInvocationHandler( PicoContainer picoContainer ) {
        this.picoContainer = picoContainer;
    }

    protected PicoContainer getPicoContainer() {
        return picoContainer;
    }
}
