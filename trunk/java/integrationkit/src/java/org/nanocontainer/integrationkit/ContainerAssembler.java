/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.integrationkit;

import org.picocontainer.MutablePicoContainer;

/**
 * @author Joe Walnes <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public interface ContainerAssembler {

    void assembleContainer(MutablePicoContainer container, String name);

}
