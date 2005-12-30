/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.nanocontainer.script.groovy.buildernodes;

import java.util.Map;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.DefaultNanoContainer;

/**
 * @author Paul Hammant
 * @version $Revision: 2695 $
 */
public class ClassLoaderElementNode extends AbstractCustomBuilderNode {

    public static final String NODE_NAME = "classLoader";

    public ClassLoaderElementNode() {
        super(NODE_NAME);
    }


    public Object createNewNode(Object current, Map attributes) {

        NanoContainer nanoContainer = (NanoContainer) current;
        return new DefaultNanoContainer(nanoContainer.getComponentClassLoader(), nanoContainer.getPico());
    }

}
