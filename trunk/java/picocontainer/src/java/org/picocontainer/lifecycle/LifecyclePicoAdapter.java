/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.lifecycle;

import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Ward Cunningham
 */
public interface LifecyclePicoAdapter extends Startable, Disposable {

    boolean isStarted();

    boolean isStopped();

    boolean isDisposed();

    PicoContainer getPicoContainer();

}
