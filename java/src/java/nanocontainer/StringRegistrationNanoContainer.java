/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer;

import picocontainer.PicoRegistrationException;
import picocontainer.LifecycleContainer;

public interface StringRegistrationNanoContainer extends LifecycleContainer {

    void registerComponent(String compClassName) throws PicoRegistrationException, ClassNotFoundException;

    void registerComponent(String typeClassName, String compClassName) throws PicoRegistrationException, ClassNotFoundException;

}
