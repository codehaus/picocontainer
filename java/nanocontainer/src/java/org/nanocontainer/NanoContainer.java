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
import org.nanocontainer.xml.EmptyXmlConfigurationException;

import java.io.Reader;
import java.util.*;

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

    public NanoContainer(Reader configuration, NanoContainerMonitor monitor) throws Exception {
        this.monitor = monitor;
        configure(configuration);
    }

    protected void instantiateComponentsBreadthFirst(PicoContainer picoContainer) throws EmptyXmlConfigurationException {
        LifecyclePicoAdapter lpa = new DefaultLifecyclePicoAdapter(picoContainer);
        lifecycleAdapters.add(lpa);
        List comps = picoContainer.getComponentInstances();
        if (comps.size() == 0) {
            throw new EmptyXmlConfigurationException();
        }
        monitor.componentsInstantiated(picoContainer);
        Collection childContainers = picoContainer.getChildContainers();
        for (Iterator iterator = childContainers.iterator(); iterator.hasNext();) {
            PicoContainer childContainer = (PicoContainer) iterator.next();
            instantiateComponentsBreadthFirst(childContainer);
        }
    }

    protected void startComponentsBreadthFirst() {
        for (Iterator iterator = lifecycleAdapters.iterator(); iterator.hasNext();) {
            DefaultLifecyclePicoAdapter lpa= (DefaultLifecyclePicoAdapter) iterator.next();
            lpa.start();
            monitor.componentsLifecycleEvent("started",lpa);
        }
        Collections.reverse(lifecycleAdapters); // for stop and dispose
    }

    public void stopComponentsDepthFirst() {
        for (Iterator iterator = lifecycleAdapters.iterator(); iterator.hasNext();) {
            DefaultLifecyclePicoAdapter lpa= (DefaultLifecyclePicoAdapter) iterator.next();
            lpa.stop();
            monitor.componentsLifecycleEvent("stopped",lpa);
        }
    }

    public void disposeComponentsDepthFirst() {
        for (Iterator iterator = lifecycleAdapters.iterator(); iterator.hasNext();) {
            DefaultLifecyclePicoAdapter lpa= (DefaultLifecyclePicoAdapter) iterator.next();
            lpa.dispose();
            monitor.componentsLifecycleEvent("disposed",lpa);
        }
    }



    protected static void addShutdownHook(final NanoContainer nano) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                nano.stopComponentsDepthFirst();
                nano.disposeComponentsDepthFirst();
            }
        });
    }

    protected abstract void configure(Reader configuration) throws Exception;
}
