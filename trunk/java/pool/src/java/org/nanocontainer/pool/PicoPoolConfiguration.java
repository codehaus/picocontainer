
package org.picoextras.pool;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 *
 *  <p><code>PicoPoolConfiguration</code> provides all the possible parameters for
 * Pico pool and will validate or provide defaults where necessary.
 * It's probably a good idea to use a configuration object for a pool
 * as in the long term there could be other parameters, such as PoolEvictionPolicy
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $ Revision: 1.0 $
 */
public class PicoPoolConfiguration {
    private byte exhaustedAction = DefaultPicoPool.DEFAULT_EXHAUSTED_ACTION;
    private int maxSize = DefaultPicoPool.DEFAULT_MAX_SIZE;
    private long maxWait = DefaultPicoPool.DEFAULT_MAX_WAIT;
    private Class implementation;
    private MutablePicoContainer poolParentContainer;
    private ComponentAdapterFactory componentAdapterFactory;

    public PicoPoolConfiguration(
            Class implementation,
            int maxSize,
            byte exhaustAction,
            long maxWait,
            ComponentAdapterFactory componentAdapterFactory,
            MutablePicoContainer poolParent) {
        setImplementation(implementation);
        setComponentAdapterFactory(componentAdapterFactory);
        setMaxSize(maxSize);
        setExhaustedAction(exhaustAction);
        setMaxWait(maxWait);
        setPoolParentContainer(poolParent);
    }

    /**
     * @return
     */
    public int getExhaustedAction() {
        return exhaustedAction;
    }

    /**
     * @return
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @return
     */
    public long getMaxWait() {
        return maxWait;
    }

    /**
     * @param i
     */
    public void setExhaustedAction(byte action) {
        if (action < 0 || action > 2) {
            action = DefaultPicoPool.DEFAULT_EXHAUSTED_ACTION;
        }

        exhaustedAction = action;

    }

    /**
     * @param i
     */
    public void setMaxSize(int size) {
        if (size <= 0)
            size = DefaultPicoPool.DEFAULT_MAX_SIZE;
        maxSize = size;
    }

    /**
     * @param l
     */
    public void setMaxWait(long l) {
        if (l < 0)
            l = 0;
        maxWait = l;
    }

    /**
     * @return
     */
    public MutablePicoContainer getPoolParentContainer() {
        return poolParentContainer;
    }

    /**
     * @param container
     */
    public void setPoolParentContainer(MutablePicoContainer container) {
        if (container == null) {
            container = new DefaultPicoContainer();
        }
        poolParentContainer = container;
    }

    /**
     * @return
     */
    public Class getImplementation() {
        return implementation;
    }

    /**
     * @param class1
     */
    public void setImplementation(Class class1) {
        implementation = class1;
    }

    /**
     * @return
     */
    public ComponentAdapterFactory getComponentAdapterFactory() {
        if (componentAdapterFactory == null) {
            componentAdapterFactory = new DefaultComponentAdapterFactory();
        }
        return componentAdapterFactory;
    }

    /**
     * @param adapter
     */
    public void setComponentAdapterFactory(ComponentAdapterFactory factory) {
        componentAdapterFactory = factory;
    }

}