package picocontainer;

/**
 * Abstract baseclass for various Container implementations.
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public abstract class AbstractContainer implements Container {

    private Object[] cachedComponentInstances = null;

    public final Object[] getComponents() {
        if( cachedComponentInstances == null ) {
            Class[] componentTypes = getComponentTypes();
            cachedComponentInstances = new Object[componentTypes.length];
            for (int i = 0; i < componentTypes.length; i++) {
                Class componentType = componentTypes[i];
                cachedComponentInstances[i] = getComponent(componentType);
            }
        }
        return cachedComponentInstances;
    }
}
