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

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.config.AspectSystem;
import picocontainer.ComponentFactory;
import picocontainer.PicoInitializationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.defaults.ComponentSpecification;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision: 1.7 $
 */
public class NanningComponentFactory implements ComponentFactory {

    private final AspectSystem aspectSystem;
    private final ComponentFactory decoratedComponentFactory;

    public NanningComponentFactory(AspectSystem aspectSystem, ComponentFactory decoratedComponentFactory) {
        this.aspectSystem = aspectSystem;
        this.decoratedComponentFactory = decoratedComponentFactory;
    }

    public Object createComponent(ComponentSpecification componentSpec, Object[] dependencyInstances) throws PicoInitializationException {
        Object component = decoratedComponentFactory.createComponent(componentSpec, dependencyInstances);

        // TODO Nanning will at the moment only aspectify stuff when it has one and only one interface
        if (component.getClass().getInterfaces().length == 1) {
            Class intf = component.getClass().getInterfaces()[0];
            // the trick: set up first mixin manually with the component as target
            AspectInstance aspectInstance = new AspectInstance(intf);
            Mixin mixin = new Mixin(intf, component);
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
