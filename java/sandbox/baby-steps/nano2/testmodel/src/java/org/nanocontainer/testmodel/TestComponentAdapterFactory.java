package org.nanocontainer.testmodel;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.adapters.AnyInjectionFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public final class TestComponentAdapterFactory extends AnyInjectionFactory {

    public final StringBuffer sb;

    public TestComponentAdapterFactory(StringBuffer sb) {
        this.sb = sb;
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, NotConcreteRegistrationException {
        sb.append("called");
        return super.createComponentAdapter(componentMonitor, lifecycleStrategy, componentCharacteristic, componentKey, componentImplementation, parameters);
    }
}
