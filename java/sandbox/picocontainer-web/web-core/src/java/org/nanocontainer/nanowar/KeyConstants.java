/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.picocontainer.script.ContainerComposer;

/**
 * Attribute keys used to store containers in various webapp scopes.
 * 
 * @author Joe Walnes
 */
public interface KeyConstants {
    
    String BUILDER = "nanocontainer.builder";
    
    String NANOCONTAINER_PREFIX = "nanocontainer";
    String CONTAINER_COMPOSER = ContainerComposer.class.getName();
    String CONTAINER_COMPOSER_CONFIGURATION = CONTAINER_COMPOSER + ".configuration";
    String KILLER_HELPER = "KILLER_HELPER";

}
