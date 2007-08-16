package org.picocontainer.gems.jndi;

import java.util.Properties;

import javax.naming.NamingException;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

/**
 * produce JNDI exposing behaviour
 * 
 * @author k.pribluda
 * 
 * @param <T>
 */
@SuppressWarnings("serial")
public class JNDIExposing extends AbstractBehaviorFactory {

	@Override
	public <T> ComponentAdapter<T> addComponentAdapter(
			ComponentMonitor componentMonitor,
			LifecycleStrategy lifecycleStrategy,
			Properties componentProperties, ComponentAdapter<T> adapter) {
		try {
			return new JNDIExposed<T>(super.addComponentAdapter(
					componentMonitor, lifecycleStrategy, componentProperties,
					adapter));
		} catch (NamingException e) {
			throw new PicoCompositionException(
					"unable to create JNDI behaviour", e);
		}
	}

	@Override
	public <T> ComponentAdapter<T> createComponentAdapter(
			ComponentMonitor componentMonitor,
			LifecycleStrategy lifecycleStrategy,
			Properties componentProperties, Object componentKey,
			Class<T> componentImplementation, Parameter... parameters)
			throws PicoCompositionException {
		// TODO Auto-generated method stub
		ComponentAdapter<T> componentAdapter = super.createComponentAdapter(
				componentMonitor, lifecycleStrategy, componentProperties,
				componentKey, componentImplementation, parameters);

		try {
			return new JNDIExposed<T>(componentAdapter);
		} catch (NamingException e) {
			throw new PicoCompositionException(
					"unable to create JNDI behaviour", e);
		}
	}

}
