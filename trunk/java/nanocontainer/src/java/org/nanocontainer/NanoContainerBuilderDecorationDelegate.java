/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant and Aslak Helles&oslash;y                   *
 *****************************************************************************/

package org.nanocontainer;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;

import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface NanoContainerBuilderDecorationDelegate {

    ComponentAdapterFactory decorate(ComponentAdapterFactory componentAdapterFactory, Map attributes);

    MutablePicoContainer decorate(MutablePicoContainer picoContainer);

    Object createChildOfContainerNode(Map attributes, Object name);

    void rememberComponentKey(Map attributes);
}
