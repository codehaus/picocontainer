package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InstanceComponentAdapter extends AbstractComponentAdapter {
    private Object componentInstance;
    
    private boolean registered;

    public InstanceComponentAdapter(Object componentKey, Object componentInstance) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentInstance.getClass());
        this.componentInstance = componentInstance;
    }

    public Object getComponentInstance(MutablePicoContainer picoContainer) {
        if(!registered) {
            picoContainer.registerOrderedComponentAdapter(this);
            picoContainer.addOrderedComponentAdapter(this);
            registered = true;
        }
        return componentInstance;
    }

    public void verify(PicoContainer picoContainer) {
    }
}
