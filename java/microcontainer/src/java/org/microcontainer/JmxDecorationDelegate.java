package org.microcontainer;

import org.nanocontainer.script.NanoContainerBuilderDecorationDelegate;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.jmx.MBeanComponentAdapterFactory;
import org.nanocontainer.NanoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.lang.reflect.Method;

/**
 * @author Michael Ward
 */
public class JmxDecorationDelegate implements NanoContainerBuilderDecorationDelegate {

	protected MBeanServer mBeanServer;

	public ComponentAdapterFactory decorate(ComponentAdapterFactory componentAdapterFactory, Map attributes) {
		return componentAdapterFactory;
	}

	public MutablePicoContainer decorate(MutablePicoContainer picoContainer) {
		return picoContainer;
	}

	public Object createNode(Object name, Map attributes, Object parentElement) {
        if(name.equals("jmx")) {
			return createJmxNode(attributes, (NanoContainer)parentElement);
		}
		else if(name.equals("component")) {
            return createComponent(attributes, (JmxDefinition)parentElement);
		}

		throw new NanoContainerMarkupException("can't handle " + name );
	}

	public void rememberComponentKey(Map attributes) {
	}

	protected Object createComponent(Map attributes, JmxDefinition jmxDefinition) {
		MutablePicoContainer picoContainer = jmxDefinition.getPicoContainer();
		Class componentKey = (Class)attributes.remove("key"); // interface
		Class componentImplementation = (Class)attributes.remove("class"); // implementation

		// register the implementation with pico
		picoContainer.registerComponentImplementation(componentKey, componentImplementation);

		// Now MBean stuff...
		List methods = getMatchingMethods(jmxDefinition.getMethods(), componentKey);
		MBeanOperationInfo[] mBeanOperationInfos = buildMBeanOperationInfoArray(methods);

		// register the MBeanInfo
		String className = componentImplementation.getName();
		MBeanInfo mBeanInfo = new MBeanInfo(className, "description", null, null, mBeanOperationInfos, null);
		picoContainer.registerComponentInstance(className.concat("MBeanInfo"), mBeanInfo);

		// register the MBean
		ComponentAdapter ca = new MBeanComponentAdapterFactory().createComponentAdapter(jmxDefinition.getKey(), componentImplementation, null);
		picoContainer.registerComponent(ca);

		return picoContainer;
	}

	protected MBeanOperationInfo[] buildMBeanOperationInfoArray(List methods) {
		MBeanOperationInfo[] mBeanOperationInfos = new MBeanOperationInfo[methods.size()];

		for (int i = 0; i < methods.size(); i++) {
			Method method = (Method) methods.get(i);
			mBeanOperationInfos[i] = new MBeanOperationInfo("No description provided", method);
		}

		return mBeanOperationInfos;
	}

	/**
	 * Find all methods with the matching name
	 */
	protected List getMatchingMethods(List operations, Class clazz) {
		Method[] allMethods = clazz.getMethods();
		List matching = new LinkedList();

		for (int i = 0; i < allMethods.length; i++) {
			Method method = allMethods[i];

			// iterate the operations to find a match
			for (int j = 0; j < operations.size(); j++) {
				String methodName = (String) operations.get(j);

				if(method.getName().equals(methodName)) {
					matching.add(method);
				}
			}
		}

		return matching;
	}

	protected MBeanServer getMBeanServer(MutablePicoContainer picoContainer) {
		return (MBeanServer)picoContainer.getComponentInstance(MBeanServer.class);
	}

	protected Object createJmxNode(Map attributes, NanoContainer nanoContainer) {
		String key = (String)attributes.remove("key");
		List methods = (List)attributes.remove("methods");

		JmxDefinition jmxDefinition = new JmxDefinition(nanoContainer.getPico());
		jmxDefinition.setKey(key);
		jmxDefinition.setMethods(methods);

		return jmxDefinition;
	}
}
