/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.nanocontainer.pool;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.defaults.ConstructorComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *  <p><code>DefaultPicoPool</code> is a pooling component that uses a pico container for the pool
 * and is picofiable
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $ Revision: 1.0 $
 */
public class DefaultPicoPool implements PicoPool {
    public static final byte FAIL_WHEN_EXHAUSTED = 0;
    public static final byte GROW_WHEN_EXHAUSTED = 1;
    public static final byte BLOCK_WHEN_EXHAUSTED = 2;

    public static final byte DEFAULT_EXHAUSTED_ACTION = FAIL_WHEN_EXHAUSTED;
    public static final byte DEFAULT_MAX_SIZE = 3;
    public static final byte DEFAULT_MAX_WAIT = 0;

    private int exhaustedAction = DEFAULT_EXHAUSTED_ACTION;
    private int maxSize = DEFAULT_MAX_SIZE;
    private int size;
    private long maxWait = DEFAULT_MAX_WAIT;
    private MutablePicoContainer pico;
    private List poolKeys = new ArrayList();
    private Map activeMap = new HashMap();

    private Class implementation;

    /**
     * ctor that uses default configuration
     * @param implementation
     */
    public DefaultPicoPool(Class implementation) {
        this(
                new PicoPoolConfiguration(
                        implementation,
                        DEFAULT_MAX_SIZE,
                        DEFAULT_EXHAUSTED_ACTION,
                        0,
                        null,
                        new DefaultPicoContainer()));
    }

    /**
     * ctor that takes a PicoPoolConfiguration
     * @param config the config to use
     */
    public DefaultPicoPool(PicoPoolConfiguration config) {
        setImplementation(config.getImplementation());
        exhaustedAction = config.getExhaustedAction();
        maxSize = config.getMaxSize();
        maxWait = config.getMaxWait();
        pico = new DefaultPicoContainer(config.getComponentAdapterFactory(),config.getPoolParentContainer());
    }

    /**
     * Validates the implememtation
     * @param clazz the implementation to be used for this pool
     */
    private void setImplementation(Class clazz) {
        if (clazz == null) {
            throw new PicoPoolException("Implementation cannot be null");
        }
        int mod = clazz.getModifiers();
        if (Modifier.isAbstract(mod) || clazz.isInterface()) {
            throw new NotConcreteRegistrationException(clazz);
        }
        implementation = clazz;
    }

    /**
     * Adds a component to be managed in the pool
     * @param component the component
     * @throws PicoException if the Max pool sized is reached and the
     * exhaustedAction is not GROW_WHEN_EXHAUSTED
     */
    protected synchronized void addComponent(Object component) throws PicoException {
        if ((activeMap.size() >= maxSize) && exhaustedAction != GROW_WHEN_EXHAUSTED) {
            throw new PicoPoolException("Maximum Pool size reached. Cannot add more components");
        }

        Object key = createComponentKey(component);
        if (component instanceof Class) {
            pico.registerComponentImplementation(key, (Class) component);
        } else {
            pico.registerComponentInstance(key, component);
        }
        poolKeys.add(key);
        setSize(getSize() + 1);
    }

    /**
     * Creates a key for a new component
     * @param component the component to create the key for
     * @return the new key
     */
    protected Object createComponentKey(Object component) {
        String key;
        if (component instanceof Class) {
            key = ((Class) component).getName() + getSize();
        } else {
            key = component.getClass().getName() + getSize();
        }
        return key;
    }

    /**
     * takes a component from the pool
     * @return the borrowed component
     */
    public synchronized Object borrowComponent() throws PicoPoolException {
        long start = System.currentTimeMillis();
        Object result = null;
        if (activeMap.size() < getMaxSize()) {
            Object key = getNextKey(true);
            result = pico.getComponentInstance(key);
        } else if (exhaustedAction == GROW_WHEN_EXHAUSTED) {
            //Create temporary object
            //We could also enforce some kind of eviction policy
            result = makeComponent();
        } else if (exhaustedAction == FAIL_WHEN_EXHAUSTED) {
            throw new NoSuchElementException();
        } else if (exhaustedAction == BLOCK_WHEN_EXHAUSTED) {

            try {
                if (maxWait <= 0) {
                    wait();
                } else {
                    wait(maxWait);
                }
            } catch (InterruptedException e) {
                //ignore
                System.out.println("Wait interrupted");
            }
            if (maxWait > 0 && ((System.currentTimeMillis() - start) >= maxWait) || getSize() == activeMap.size()) {
                throw new NoSuchElementException("Wait period expired for pooled object");
            } else {
                Object key = getNextKey(false);
                result = pico.getComponentInstance(key);
            }
        }
        activateComponent(result);
        return result;
    }

    /**
     * Gets the key of the next avalible component. If a key is not available
     * and the createNew flag is set.  A new object is added to the pool
     * and it's key returned
     * @param createNew if true a new key will be created if there are no keys
     * avaliable
     * @return an available key, or the key of the newly create componet if the
     * createNew flag was set, or null.
     */
    protected synchronized Object getNextKey(boolean createNew) throws PicoPoolException {
        if (poolKeys.size() == 0 && createNew) {
            addComponent(makeComponent());
        }

        Object key = poolKeys.remove(0);
        activeMap.put(pico.getComponentInstance(key), key);
        return key;
    }

    /**
     * Makes a component in the pool avalible again once it has been borrowed
     * @param component the borrowed component
     */
    public synchronized void returnComponent(Object component) throws PicoException {
        passivateComponent(component);
        Object key = activeMap.remove(component);
        if (key != null) {
            poolKeys.add(key);
            notifyAll();
        }
    }

    /**
     *
     * @return the number of components in the pool
     */
    public int getSize() {
        // can't use pico.getComponentKeys()getComponentInstances().size()
        //as this returns count of parent aswell
        return size;
    }

    /**
     * Creates a component for the pool
     * @return thenewly created component
     */
    protected Object makeComponent() throws PicoPoolException {
        //Create new "uncached" component
        ComponentAdapter componentAdapter = new ConstructorComponentAdapter(implementation, implementation);
        componentAdapter.setContainer(pico);
        Object object = componentAdapter.getComponentInstance();
        return object;
    }

    /**
     * Called when a component is borrowed from the pool, but before
     * the caller receives the component
     * @param component the component being borrowed
     */
    protected void activateComponent(Object component) {
        //noop
    }

    /**
     * Called when a component is returned to the pool, before
     * the component actually enters the pool
     * @param component the component being returned
     */
    protected void passivateComponent(Object component) {
        //noop
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
    public Class getImplementation() {
        return implementation;
    }

    /**
     * @return
     */
    public long getMaxWait() {
        return maxWait;
    }

    /**
     * @return
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @param i
     */
    protected void setSize(int i) {
        size = i;
    }

    /**
     *
     */
    protected MutablePicoContainer getPoolContainer() {
        return pico;
    }

    /* (non-Javadoc)
     * @see org.nanocontainer.pool.PicoPool#clearPool()
     */
    public void clearPool() {
        Iterator iter = pico.getComponentAdapters().iterator();
        while (iter.hasNext()) {
            final ComponentAdapter componentAdapter = (ComponentAdapter) iter.next();
            pico.unregisterComponent(componentAdapter.getComponentKey());
        }
        activeMap.clear();
        poolKeys.clear();
        size = 0;
    }

}
