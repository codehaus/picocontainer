/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package org.nanocontainer.servlet.containerfactory;

import org.nanocontainer.DomRegistrationNanoContainer;
import org.nanocontainer.InputSourceRegistrationNanoContainer;
import org.nanocontainer.servlet.ContainerFactory;
import org.nanocontainer.servlet.ObjectInstantiator;
import org.xml.sax.InputSource;
import org.picocontainer.*;
import org.picocontainer.extras.HierarchicalComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DefaultComponentRegistry;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.io.IOException;

public class XmlConfiguredNanoFactory implements ContainerFactory {
    private ServletContext servletContext;

    public XmlConfiguredNanoFactory(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public PicoContainer buildContainer(String configName, ComponentRegistry componentRegistry) {
        try {
            InputSourceRegistrationNanoContainer container = new DomRegistrationNanoContainer.WithComponentRegistry(componentRegistry);
            configureAndStart(configName, container);
            return container;
        } catch (Exception e) {
            // TODO: Better exception
            throw new RuntimeException("Cannot build container for config: " + configName, e);
        }
    }

    public PicoContainer buildContainerWithParent(PicoContainer parentContainer,
                                                  ComponentRegistry parentRegistry,
                                                  String configName) {
        try {
            HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(parentRegistry);
            InputSourceRegistrationNanoContainer childContainer =
                new DomRegistrationNanoContainer.WithComponentRegistry(hcr);
            configureAndStart(configName, childContainer);
            return childContainer;
        } catch (Exception e) {
            // TODO: Better exception
            throw new RuntimeException("Cannot build container for config: " + configName, e);
        }
    }

    private void configureAndStart(String configName, InputSourceRegistrationNanoContainer container)
            throws PicoRegistrationException, ClassNotFoundException, PicoInitializationException, IOException {
        InputStream in = getConfigInputStream(configName);
        try {
            container.registerComponents(new InputSource(in));
        } finally {
            in.close();
        }
        container.instantiateComponents();
    }

    public ObjectInstantiator buildInstantiator(final PicoContainer parentContainer, final ComponentRegistry parentRegistry) {
        return new ObjectInstantiator() {
            public Object newInstance(Class cls) {
                HierarchicalComponentRegistry hcr = new HierarchicalComponentRegistry.Default(parentRegistry);
                RegistrationPicoContainer childContainer = new DefaultPicoContainer.WithComponentRegistry(hcr);

                try {
                    childContainer.registerComponentByClass(cls);
                } catch (PicoRegistrationException e) {
                    // TODO: throw a custom exception
                    throw new RuntimeException("Could not instantiate " + cls.getName(), e);
                } catch (PicoIntrospectionException e) {
                    // TODO: throw a custom exception
                    throw new RuntimeException("Could not instantiate " + cls.getName(), e);
                }
                try {
                    childContainer.instantiateComponents();
                } catch (PicoInitializationException e) {
                    // TODO: throw a custom exception
                    throw new RuntimeException("Could not initialize container", e);
                }
                return childContainer.getComponent(cls);
            }
        };
    }

    public void destroyContainer(PicoContainer container) {
        // TODO
    }

    private InputStream getConfigInputStream(String configName) {
        // TODO: find a way of caching this so the XML does not have to be continually reparsed
        return servletContext.getResourceAsStream("/WEB-INF/components-" + configName + ".xml");
    }

}
