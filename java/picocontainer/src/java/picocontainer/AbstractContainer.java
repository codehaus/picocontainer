package picocontainer;

/**
 * Abstract baseclass for various Container implementations.
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public abstract class AbstractContainer implements Container {

    public final Object[] getComponents() {
        Class[] componentTypes = getComponentTypes();
        Object[] result = new Object[componentTypes.length];
        for (int i = 0; i < componentTypes.length; i++) {
            Class componentType = componentTypes[i];
            result[i] = getComponent(componentType);
        }
        return result;
    }
}
