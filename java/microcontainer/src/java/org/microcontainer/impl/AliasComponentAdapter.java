package org.microcontainer.impl;

import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.ComponentAdapter;

/**
 * This component adapter allows for a component adapter to be mapped to more than one key.
 *
 * This was added because with JMX an MBean may need to be mapped to an ObjectName for use
 * with MBeanServer and to an Interface for other dependencies.
 *
 * @author Michael Ward
 */
public class AliasComponentAdapter extends DecoratingComponentAdapter {
	private Object key;

	public AliasComponentAdapter(Object key, ComponentAdapter delegate) {
		super(delegate);
		this.key = key;
	}

	public Object getComponentKey() {
		return key;
	}
}
