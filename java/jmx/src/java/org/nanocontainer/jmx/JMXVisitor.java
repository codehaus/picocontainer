package org.nanocontainer.jmx;

import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.AbstractPicoVisitor;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.DynamicMBean;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class JMXVisitor extends AbstractPicoVisitor {
	private Map map = new HashMap(20);
	private PicoContainer picoContainer;

	/**
	 * register an MBeanInfo to an ObjectName
	 */
	public void register(ObjectName objectName, MBeanInfo mBeanInfo) {
		MBeanInfoWrapper wrapper = new MBeanInfoWrapper(mBeanInfo, objectName);
		map.put(mBeanInfo.getClassName(), wrapper);
	}

	/**
	 * register an MBeanInfo to an ObjectName
	 */
	public void register(ObjectName objectName, Class management) {
		map.put(management.getName(), objectName);
	}

	public void visitContainer(PicoContainer picoContainer) {
		this.picoContainer = picoContainer;
	}

	public void visitComponentAdapter(ComponentAdapter componentAdapter) {
		String className = getComponentKeyAsString(componentAdapter);

		if (map.containsKey(className)) {
			Object obj = map.get(className);

			if (obj instanceof MBeanInfoWrapper) {
            	handleMBeanInfo(componentAdapter, (MBeanInfoWrapper)obj);
			} else { // StandardMBean
				handleStandardMBean(componentAdapter, (ObjectName)obj);
			}
		}
	}

	public void visitParameter(Parameter parameter) {
		// does nothing
	}

	protected String getComponentKeyAsString(ComponentAdapter componentAdapter) {
		Object componentKey = componentAdapter.getComponentKey();

		if(componentKey instanceof Class) {
			return ((Class)componentKey).getName();
		}

		return (String)componentKey;
	}

	/**
	 * Create a NanoMBean from the MBeanInfoWrapper being passed in
	 */
	protected void handleMBeanInfo(ComponentAdapter componentAdapter, MBeanInfoWrapper mBeanInfoWrapper) {
		MBeanInfo mBeanInfo = mBeanInfoWrapper.getmBeanInfo();
		ObjectName objectName = mBeanInfoWrapper.getObjectName();
		Object instance = componentAdapter.getComponentInstance(picoContainer);
		NanoMBean nanoMBean = new NanoMBean(instance, mBeanInfo);

		registerWithMBeanServer(nanoMBean, objectName);
	}

	/**
	 * Create a StandardMBean and register it to the MBeanServer
	 */
	protected void handleStandardMBean(ComponentAdapter componentAdapter, ObjectName objectName) {
		StandardMBean standardMBean = (StandardMBean)componentAdapter.getComponentInstance(picoContainer);
		registerWithMBeanServer(standardMBean, objectName);
	}

	protected void registerWithMBeanServer(DynamicMBean dynamicMBean, ObjectName objectName) {
		MBeanServer mBeanServer = (MBeanServer) picoContainer.getComponentInstance(MBeanServer.class);

		if (mBeanServer == null) {
			throw new JMXRegistrationException("An MBeanServer instance MUST be registered with the container");
		}

		try {
			mBeanServer.registerMBean(dynamicMBean, objectName);
		} catch (Exception e) {
			throw new RuntimeException("Unable to register MBean to MBean Server", e);
		}
	}

	/**
	 * Simple wrapper to tie a MBeanInfo to an ObjectName
	 */
	private class MBeanInfoWrapper {
		private MBeanInfo mBeanInfo;
		private ObjectName objectName;

		public MBeanInfoWrapper(MBeanInfo mBeanInfo, ObjectName objectName) {
			this.mBeanInfo = mBeanInfo;
			this.objectName = objectName;
		}

		public MBeanInfo getmBeanInfo() {
			return mBeanInfo;
		}

		public ObjectName getObjectName() {
			return objectName;
		}
	}

}
