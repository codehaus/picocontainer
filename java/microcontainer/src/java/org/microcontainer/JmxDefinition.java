package org.microcontainer;

import org.picocontainer.MutablePicoContainer;

import java.util.List;

/**
 * @author Michael Ward
 */
public class JmxDefinition {

	private String key;
	private List methods;
	private MutablePicoContainer picoContainer;

	public JmxDefinition(MutablePicoContainer picoContainer) {
		this.picoContainer = picoContainer;
	}

	public MutablePicoContainer getPicoContainer() {
		return picoContainer;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List getMethods() {
		return methods;
	}

	public void setMethods(List methods) {
		this.methods = methods;
	}
}
