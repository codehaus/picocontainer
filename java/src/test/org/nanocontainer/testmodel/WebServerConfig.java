/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.testmodel;

/**
 * For a two parameter confiuration, you might prefer to use
 * addParameterForComponent on OldRegistrationPicoContainer. This example
 * that uses a separate configuration object would be better
 * for cases where there were say fifty configurable things
 * about a particilar component.
 */
public interface WebServerConfig {

    String getHost();

    int getPort();
}
