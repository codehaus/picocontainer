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

public interface StringRegistrationNanoContainer extends NanoContainer {

    void registerComponent(String compClassName) throws NanoRegistrationException, ClassNotFoundException;

    void registerComponent(String typeClassName, String compClassName) throws NanoRegistrationException, ClassNotFoundException;

}
