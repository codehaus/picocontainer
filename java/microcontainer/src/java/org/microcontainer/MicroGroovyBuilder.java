package org.microcontainer;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.script.groovy.NanoContainerBuilder;

import java.util.Map;

public class MicroGroovyBuilder extends NanoContainerBuilder {

	public MicroGroovyBuilder() {
		super(new JmxDecorationDelegate());
	}

	// todo this should be pushed up to NanoContainerBuilder
    protected NanoContainer createChildContainer(Map attributes, NanoContainer parent) {
		if(parent == null) {
			parent = (NanoContainer)attributes.remove("parent");
		}

		return super.createChildContainer(attributes, parent);
    }
}