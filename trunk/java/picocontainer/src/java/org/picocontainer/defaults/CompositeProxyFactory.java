/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Chris Stevenson*
 *****************************************************************************/

package org.picocontainer.defaults;

import java.util.List;

/**
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.1 $
 */
public interface CompositeProxyFactory {
    Object createCompositeProxy(
            ClassLoader classLoader,
            List objectsToAggregateCallFor,
            boolean callInReverseOrder
    );
}
