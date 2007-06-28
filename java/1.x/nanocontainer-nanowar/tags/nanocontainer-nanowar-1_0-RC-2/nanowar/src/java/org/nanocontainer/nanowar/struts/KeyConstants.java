/**
 * **************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 * *
 * ***************************************************************************
 */
package org.nanocontainer.nanowar.struts;

/**
 * Request, session, and servlet context attribute keys used to store the
 * various Pico containers.
 */
public interface KeyConstants extends org.nanocontainer.nanowar.KeyConstants {
    
    /**
     * The action container request attribute key.
     */
    String ACTIONS_CONTAINER = "org.nanocontainer.struts.actions";

}