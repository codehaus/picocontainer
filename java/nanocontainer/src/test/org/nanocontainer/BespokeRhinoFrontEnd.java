/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.nanocontainer.rhino.DefaultRhinoFrontEnd;

public class BespokeRhinoFrontEnd extends DefaultRhinoFrontEnd {

    public static boolean used;
    public BespokeRhinoFrontEnd() {
        used = true;
    }
}
