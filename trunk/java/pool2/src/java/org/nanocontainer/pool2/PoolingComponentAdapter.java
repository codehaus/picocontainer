/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy                                                        *
 *****************************************************************************/
package org.nanocontainer.pool2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoException;
import org.picocontainer.defaults.DecoratingComponentAdapter;

/**
 * This Component Adapter maintains a pool of component instances.
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class PoolingComponentAdapter extends DecoratingComponentAdapter {
    
    private static final int DEFAULT_SIZE = 5;
    private int maxPoolSize;
    private int waitMilliSeconds;
    protected List available = new LinkedList();
    protected Map busy = new HashMap();

    /**
     * Construct a PoolingComponentAdapter.
     * @param delegate The delegate adapter that provides the pooled instances.
     * @param maxPoolSize maximum pool size. A size of -1 results in an ever growing pool.
     * @param waitMilliSeconds number of milliseconds to wait when {@link #getComponentInstance() }
     * is called. Three different values are possible here:
     * <ul>
     * <li>-1 : Wait when exhausted until a component is available.</li>
     * <li>0 : Fail immediately when exhausted.</li>
     * <li>n : Wait for max n milliseconds until a component is available. This will fail on timeout.</li>
     * </ul>
     */
    public PoolingComponentAdapter(ComponentAdapter delegate, int maxPoolSize, int waitMilliSeconds) {
        super(delegate);
        this.maxPoolSize = maxPoolSize;
        this.waitMilliSeconds = waitMilliSeconds;
    }

    public PoolingComponentAdapter(ComponentAdapter delegate, int maxPoolSize) {
        this(delegate, maxPoolSize, 0);
    }

    public PoolingComponentAdapter(ComponentAdapter delegate) {
        this(delegate, DEFAULT_SIZE);
    }

    public synchronized Object getComponentInstance() throws PicoException {
        Object componentInstance = null;
        if (getAvailable() > 0) {
            componentInstance = available.remove(0);
        } else {
            // none available. grow or wait.
            if (maxPoolSize < 0 || getTotalSize() < maxPoolSize) {
                componentInstance = super.getComponentInstance();
            } else {
                // can't grow more. wait for one to become available.
                try {
                    if(waitMilliSeconds > 0) {
                        wait(waitMilliSeconds);
                    }
                    if(waitMilliSeconds < 0) {
                        wait();
                    }
                    if (getAvailable() == 0) {
                        throw new ExhaustedException();
                    }
                    componentInstance = available.remove(0);
                } catch (IndexOutOfBoundsException e) {
                    throw new ExhaustedException();
                } catch (InterruptedException e) {
                    // give the client code of the current thread a chance to abort also
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("INTERRUPTED!");
                }
            }
        }
        return markInstanceAsBusyAndReturnIt(componentInstance);
    }

    public synchronized void returnComponentInstance(Object componentInstance) {
        if (!getComponentImplementation().equals(componentInstance.getClass())) {
            throw new BadTypeException(getComponentImplementation(), componentInstance.getClass());
        }
        if (!busy.keySet().contains(componentInstance)) {
            throw new UnmanagedInstanceException(componentInstance);
        }
        busy.remove(componentInstance);
        available.add(componentInstance);
        notify();
    }

    /**
     * @return Returns the current number of managed instances.
     */
    public int getTotalSize() {
        return getAvailable() + getBusy();
    }

    /**
     * @return Returns the busy instances.
     */
    public int getBusy() {
        return busy.size();
    }

    /**
     * @return Returns the available instances.
     */
    public int getAvailable() {
        return available.size();
    }
    
    /**
     * @return Returns the maximum pool size.
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    protected Object markInstanceAsBusyAndReturnIt(Object instance) {
        busy.put(instance, instance);
        return instance;
    }
}
