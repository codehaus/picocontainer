/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.pool2;

import org.picocontainer.*;
import org.picocontainer.defaults.DecoratingComponentAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * This Component Adapter maintains a pool of component instances.
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PoolingComponentAdapter extends DecoratingComponentAdapter {
    private static final int DEFAULT_SIZE = 5;
    private int maxPoolSize;
    private final int waitMilliSeconds;
    private List available = new LinkedList();
    private List busy = new LinkedList();

    /**
     * @param delegate The delegate adapter that provides the pooled instances.
     * @param maxPoolSize maximum pool size.
     * @param waitMilliSeconds number of milliseconds to wait when {@link #getComponentInstance(MutablePicoContainer) }
     * is called. Three different values are possible here:
     * <ul>
     * <li>-1 : Grow when exhausted.</li>
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
        final int availableSize = available.size();
        if (availableSize > 0) {
            componentInstance = available.remove(0);
        } else {
            // none available. grow or wait.
            if (getTotalSize() < maxPoolSize) {
                componentInstance = super.getComponentInstance();
            } else {
                // can't grow more. wait for one to become available.
                try {
                    if(waitMilliSeconds > 0) {
                        wait(waitMilliSeconds);
                    }
                    componentInstance = available.remove(0);
                } catch (IndexOutOfBoundsException e) {
                    throw new ExhaustedException();
                } catch (InterruptedException e) {
                    throw new RuntimeException("INTERRUPTED!");
                }
            }
        }
        busy.add(componentInstance);
        return componentInstance;
    }

    public synchronized void returnComponentInstance(Object componentInstance) {
        if (!getComponentImplementation().equals(componentInstance.getClass())) {
            throw new BadTypeException(getComponentImplementation(), componentInstance.getClass());
        }
        if (!busy.contains(componentInstance)) {
            throw new UnmanagedInstanceException(componentInstance);
        }
        busy.remove(componentInstance);
        available.add(componentInstance);
        notify();
    }

    public int getTotalSize() {
        return getAvailable() + getBusy();
    }

    public int getBusy() {
        return busy.size();
    }

    public int getAvailable() {
        return available.size();
    }
}
