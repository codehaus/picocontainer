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
import picocontainer.ComponentFactory;
import picocontainer.PicoInitializationException;
import picocontainer.PicoIntrospectionException;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision: 1.7 $
 */
class NanningComponentFactory implements ComponentFactory {

    private final AspectSystem aspectSystem;
    private final ComponentFactory decoratedComponentFactory;

    public NanningComponentFactory(AspectSystem aspectSystem, ComponentFactory decoratedComponentFactory) {
        this.aspectSystem = aspectSystem;
        this.decoratedComponentFactory = decoratedComponentFactory;
    }

    public Object createComponent(Class componentType, Class componentImplementation, Class[] dependencies, Object[] dependencyInstances) throws PicoInitializationException {
        Object component = decoratedComponentFactory.createComponent(componentType, componentImplementation, dependencies, dependencyInstances);

        // Nanning will only aspectify stuff when its type is an interface
        if (componentType.isInterface()) {
            // the trick: set up first mixin manually with the component as target
            AspectInstance aspectInstance = new AspectInstance();
            MixinInstance mixin = new MixinInstance(componentType, component);
            aspectInstance.addMixin(mixin);

            // let the aspects do its work
            aspectSystem.initialize(aspectInstance);
            component = aspectInstance.getProxy();
        }
        return component;
    }

    public Class[] getDependencies(Class componentImplementation) throws PicoIntrospectionException {
        return decoratedComponentFactory.getDependencies(componentImplementation);
    }
}
