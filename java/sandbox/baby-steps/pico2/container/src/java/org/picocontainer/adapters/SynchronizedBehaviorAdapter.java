/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.adapters;


import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.adapters.BehaviorAdapter;

/**
 * @author Aslak Helles&oslash;y
 * @author Manish Shah
 * @version $Revision$
 */
public class SynchronizedBehaviorAdapter extends BehaviorAdapter {
    public SynchronizedBehaviorAdapter(ComponentAdapter delegate) {
        super(delegate);
    }

    public synchronized Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
        return super.getComponentInstance(container);
    }
}
