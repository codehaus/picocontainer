/*****************************************************************************
 * Copyright (C) ClassRegistrationPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

import java.util.List;
import java.util.ArrayList;

public class OverriddenStartableLifecycleManager implements LifecycleManager {

    private List started = new ArrayList();
    private List stopped = new ArrayList();

    public void startComponent(Object component) {
        started.add(component.getClass());
    }

    public void stopComponent(Object component) {
        stopped.add(component.getClass());
    }

    public void disposeOfComponent(Object component) throws PicoDisposalException {        
    }

    public List getStarted() {
        return started;
    }

    public List getStopped() {
        return stopped;
    }
}
