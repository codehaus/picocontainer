/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.picocontainer.PicoContainer;
import org.picocontainer.extras.DefaultLifecyclePicoAdapter;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picoextras.script.PicoCompositionException;
import org.picoextras.script.xml.EmptyCompositionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @author Ward Cunningham
 * @author Paul Hammant
 * @version $Revision$
 */
public abstract class NanoContainer {

    private final List lifecycleAdapters = new ArrayList();
    private final NanoContainerMonitor monitor;
    private PicoContainer rootContainer;

    public NanoContainer(NanoContainerMonitor monitor) throws EmptyCompositionException {
        this.monitor = monitor;
    }

    protected abstract PicoContainer createPicoContainer() throws PicoCompositionException;

    protected void init() throws PicoCompositionException {
        rootContainer = createPicoContainer();
        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }

    public PicoContainer getRootContainer() {
        return rootContainer;
    }

    private void instantiateComponentsBreadthFirst(PicoContainer picoContainer) throws EmptyCompositionException {
        if (picoContainer instanceof LifecyclePicoAdapter) {
            lifecycleAdapters.add(picoContainer);
        } else {
            lifecycleAdapters.add(new DefaultLifecyclePicoAdapter(picoContainer));
        }
        List comps = picoContainer.getComponentInstances();
        if (comps.size() == 0) {
            throw new EmptyCompositionException();
        }
        monitor.componentsInstantiated(picoContainer);
        Collection childContainers = picoContainer.getChildContainers();
        for (Iterator iterator = childContainers.iterator(); iterator.hasNext();) {
            PicoContainer childContainer = (PicoContainer) iterator.next();
            instantiateComponentsBreadthFirst(childContainer);
        }
    }

    public void startComponentsBreadthFirst() {
        for (Iterator iterator = lifecycleAdapters.iterator(); iterator.hasNext();) {
            LifecyclePicoAdapter lpa= (LifecyclePicoAdapter) iterator.next();
            lpa.start();
            monitor.componentsLifecycleEvent("started",lpa);
        }
        Collections.reverse(lifecycleAdapters); // for stop and dispose
    }

    public void stopComponentsDepthFirst() {
        for (Iterator iterator = lifecycleAdapters.iterator(); iterator.hasNext();) {
            LifecyclePicoAdapter lpa= (LifecyclePicoAdapter) iterator.next();
            lpa.stop();
            monitor.componentsLifecycleEvent("stopped",lpa);
        }
    }

    public void disposeComponentsDepthFirst() {
        for (Iterator iterator = lifecycleAdapters.iterator(); iterator.hasNext();) {
            LifecyclePicoAdapter lpa= (LifecyclePicoAdapter) iterator.next();
            lpa.dispose();
            monitor.componentsLifecycleEvent("disposed",lpa);
        }
    }

    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
    }

    private Runnable shutdownHook = new Runnable() {
        public void run() {
            stopComponentsDepthFirst();
            disposeComponentsDepthFirst();

        }
    };
}
