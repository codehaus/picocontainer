/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsén                                               *
 *****************************************************************************/

package nanocontainer.nanning;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.config.AspectSystem;
import picocontainer.DefaultComponentFactory;

public class NanningComponentFactory extends DefaultComponentFactory {
    private final AspectSystem aspectSystem;

    public NanningComponentFactory(AspectSystem aspectSystem) {
        this.aspectSystem = aspectSystem;
    }

    public Object createComponent(Class compType, Constructor constructor, Object[] args)
            throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object component = super.createComponent(compType, constructor, args);
        Class[] interfaces = compType.getInterfaces();

        // Nanning will only aspectify stuff that has one and only one interface
        if (interfaces.length == 1) {
            Class interfaze = interfaces[0];

            // the trick: set up first mixin manually with the component as target
            AspectInstance aspectInstance = new AspectInstance();
            MixinInstance mixin = new MixinInstance(interfaze, component);
            aspectInstance.addMixin(mixin);

            // let the aspects do it's work
            aspectSystem.initialize(aspectInstance);
            component = aspectInstance.getProxy();
        }
        return component;
    }
}
