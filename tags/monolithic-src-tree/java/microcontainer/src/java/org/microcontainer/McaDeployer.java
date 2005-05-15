/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.           *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mike Ward                                                *
 *****************************************************************************/

package org.microcontainer;

import java.io.IOException;
import java.net.URL;

/**
 * @author Mike Ward
 * Responsible for deploying a MCA to the file system.
 */
public interface McaDeployer {

    void deploy(String context, URL mcaURL) throws IOException;
}
