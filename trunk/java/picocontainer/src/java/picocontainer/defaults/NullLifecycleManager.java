/*****************************************************************************
 * Copyright (C) ClassRegistrationPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import picocontainer.LifecycleManager;
import picocontainer.PicoStartException;
import picocontainer.PicoStopException;
import picocontainer.PicoDisposalException;

public class NullLifecycleManager
        implements LifecycleManager {
    public void startComponent(Object component) throws PicoStartException {
    }

    public void stopComponent(Object component) throws PicoStopException {
    }

    public void disposeOfComponent(Object component) throws PicoDisposalException {
    }
}
