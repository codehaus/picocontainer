package nanocontainer.reflect;

import picocontainer.PicoContainer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

/**
 * InvocationHandler that adds multiple return type functionality.
 * The result is an array of the return values for each matching
 * method in the container's components.
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class MultipleReturnTypeInvocationHandler extends ContainerInvocationHandler {

    public MultipleReturnTypeInvocationHandler(PicoContainer picoContainer) {
        super(picoContainer);
    }

    /**
     * Returns an Array of Object, containing all the return values for the components
     * in the associated PicoContainer.
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List resultList = new ArrayList();

        // Try to invoke the method on all components.
        Object[] components = getContainer().getComponents();
        for (int i = 0; i < components.length; i++) {
            Object component = components[i];
            try {
                Object result = method.invoke( component, args );
                resultList.add( result );
            } catch( Exception ignore ) {
            }
        }
        return resultList.toArray();
    }
}
