/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package nanocontainer;

import org.xml.sax.InputSource;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoContainer;

public interface InputSourceRegistrationNanoContainer extends PicoContainer {

    /**
     * Register a list of components expressed in XML form.
     * @param registration The InputSource pertaining to the XML document detailing
     * assembly and optionally configuration.
     * @throws PicoRegistrationException If a problem registereing the component.
     * @throws ClassNotFoundException If th eclass could npot be found in any
     */
    void registerComponents(InputSource registration) throws PicoRegistrationException, ClassNotFoundException;

}
