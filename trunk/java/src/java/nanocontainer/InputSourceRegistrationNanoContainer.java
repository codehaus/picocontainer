/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package nanocontainer;

import org.xml.sax.InputSource;
import picocontainer.PicoRegistrationException;
import picocontainer.LifecycleContainer;

public interface InputSourceRegistrationNanoContainer extends LifecycleContainer {

    void registerComponents(InputSource registration) throws PicoRegistrationException, ClassNotFoundException;

}
