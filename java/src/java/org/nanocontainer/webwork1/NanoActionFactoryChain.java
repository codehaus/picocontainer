/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/

package org.nanocontainer.webwork1;

import webwork.action.factory.*;
import webwork.action.Action;

public class NanoActionFactoryChain extends ActionFactory {

    private ActionFactory factory;

    public NanoActionFactoryChain() {
        factory = new NanoActionFactory();
        factory = new PrefixActionFactoryProxy(factory);
        factory = new CommandActionFactoryProxy(factory);
        factory = new AliasingActionFactoryProxy(factory);
        factory = new CommandActionFactoryProxy(factory);
        factory = new ContextActionFactoryProxy(factory);
        factory = new PrepareActionFactoryProxy(factory);
        factory = new ParametersActionFactoryProxy(factory);
        factory = new ChainingActionFactoryProxy(factory);
    }

    public Action getActionImpl(String actionName) throws Exception {
        return factory.getActionImpl(actionName);
    }

}
