/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirs?n                                               *
 *****************************************************************************/

package nanocontainer.nanning;


import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.config.AspectSystem;
import picocontainer.PicoInvocationTargetInitailizationException;
import picocontainer.defaults.DefaultComponentFactory;

public class NanningComponentFactory extends DefaultComponentFactory {
    private final AspectSystem aspectSystem;

    public NanningComponentFactory(AspectSystem aspectSystem) {
        this.aspectSystem = aspectSystem;
    }

    public Object createComponent(Class compType, Class compImplementation, Class[] dependencies, Object[] args) throws PicoInvocationTargetInitailizationException {

        Object component = super.createComponent(compType, compImplementation, dependencies, args);

        // Nanning will only aspectify stuff when its type is an interface
        if (compType.isInterface()) {
            // the trick: set up first mixin manually with the component as target
            AspectInstance aspectInstance = new AspectInstance();
            MixinInstance mixin = new MixinInstance(compType, component);
            aspectInstance.addMixin(mixin);

            // let the aspects do it's work
            aspectSystem.initialize(aspectInstance);
            component = aspectInstance.getProxy();
        }
        return component;
    }
}
