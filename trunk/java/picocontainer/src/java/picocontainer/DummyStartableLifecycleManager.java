/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

public class DummyStartableLifecycleManager
        implements StartableLifecycleManager {
    public void startComponent(Object component) throws PicoStartException {
    }

    public void stopComponent(Object component) throws PicoStopException {
    }
}
