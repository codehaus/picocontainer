package org.picoextras.jmx;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import javax.management.MBeanServer;
import java.io.Serializable;

public class MX4JComponentAdapterFactory implements ComponentAdapterFactory, Serializable {
    private MBeanServer mbeanServer;
    private ComponentAdapterFactory delegate;

    public MX4JComponentAdapterFactory(MBeanServer mbeanServer, ComponentAdapterFactory componentAdapterFactory) {
        this.mbeanServer = mbeanServer;
        delegate = componentAdapterFactory;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new MX4JComponentAdapter(mbeanServer, delegate.createComponentAdapter(componentKey, componentImplementation, parameters));
    }
}
