package org.picocontainer.defaults;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InstanceComponentAdapter extends AbstractComponentAdapter {
    private Object componentInstance;

    public InstanceComponentAdapter(Object componentKey, Object componentInstance) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentInstance.getClass());
        this.componentInstance = componentInstance;
    }

    public Object getComponentInstance(AbstractPicoContainer picoContainer) {
        return componentInstance;
    }
}
