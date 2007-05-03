package org.nanocontainer.testmodel;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.componentadapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class TestComponentAdapterFactory extends AnyInjectionComponentAdapterFactory {

    public StringBuffer sb;

    public TestComponentAdapterFactory(StringBuffer sb) {
        this.sb = sb;
    }

    public ComponentAdapter createComponentAdapter(ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        sb.append("called");
        return super.createComponentAdapter(registerationCharacteristic, componentKey, componentImplementation, parameters); 
    }
}
