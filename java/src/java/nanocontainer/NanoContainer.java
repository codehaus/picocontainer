/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer;

import picocontainer.Container;
import picocontainer.PicoStopException;
import picocontainer.PicoStartException;

public interface NanoContainer extends Container
{

    void start() throws PicoStartException;
    void stop() throws PicoStopException;

}
