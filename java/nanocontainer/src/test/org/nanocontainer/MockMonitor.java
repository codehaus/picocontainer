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
import org.picocontainer.lifecycle.LifecyclePicoAdapter;

import java.util.ArrayList;

public class MockMonitor implements NanoContainerMonitor {

    public static String monitorRecorder;
    public static ArrayList allComps;

    private String code(Object inst) {
        String name = inst.getClass().getName();
        return name.substring(name.length() - 1);
    }

    public void componentsLifecycleEvent(String eventName, LifecyclePicoAdapter lpa) {
        monitorRecorder += ("+" + code(lpa.getPicoContainer().getComponentInstances().get(0)) + "_" + eventName);
    }

    public void componentsInstantiated(PicoContainer picoContainer) {
        monitorRecorder += "*" + code(picoContainer.getComponentInstances().get(0));
        allComps.addAll(picoContainer.getComponentInstances());
    }
}
