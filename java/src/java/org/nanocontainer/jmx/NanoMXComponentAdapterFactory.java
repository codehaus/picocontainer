package org.nanocontainer.jmx;

import org.picocontainer.internals.ComponentAdapterFactory;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.Parameter;
import org.picocontainer.PicoIntrospectionException;

import javax.management.MBeanServer;
import java.io.Serializable;

public class NanoMXComponentAdapterFactory implements ComponentAdapterFactory, Serializable {
    private MBeanServer mbeanServer;
    private ComponentAdapterFactory delegate;

    public NanoMXComponentAdapterFactory(MBeanServer mbeanServer, ComponentAdapterFactory componentAdapterFactory) {
        this.mbeanServer = mbeanServer;
        delegate = componentAdapterFactory;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException {
        return new NanoMXComponentAdapter(mbeanServer, delegate.createComponentAdapter(componentKey, componentImplementation, parameters));
    }
}
