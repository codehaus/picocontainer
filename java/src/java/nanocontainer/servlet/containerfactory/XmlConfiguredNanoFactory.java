/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package nanocontainer.servlet.containerfactory;

import nanocontainer.DomRegistrationNanoContainer;
import nanocontainer.InputSourceRegistrationNanoContainer;
import nanocontainer.servlet.ContainerFactory;
import nanocontainer.servlet.ObjectInstantiater;
import org.xml.sax.InputSource;
import picocontainer.*;
import picocontainer.hierarchical.HierarchicalPicoContainer;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.io.IOException;

public class XmlConfiguredNanoFactory implements ContainerFactory {
    private ServletContext servletContext;

    public XmlConfiguredNanoFactory(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public PicoContainer buildContainer(String configName) {
        try {
            InputSourceRegistrationNanoContainer container = new DomRegistrationNanoContainer.Default();
            configureAndStart(configName, container);
            return container;
        } catch (Exception e) {
            // TODO: Better exception
            throw new RuntimeException("Cannot build container for config: " + configName, e);
        }
    }

    public PicoContainer buildContainerWithParent(PicoContainer parentContainer, String configName) {
        try {
            InputSourceRegistrationNanoContainer container = new DomRegistrationNanoContainer.WithParentContainer(parentContainer);
            configureAndStart(configName, container);
            return container;
        } catch (Exception e) {
            // TODO: Better exception
            throw new RuntimeException("Cannot build container for config: " + configName, e);
        }
    }

    private void configureAndStart(String configName, InputSourceRegistrationNanoContainer container) throws PicoRegistrationException, ClassNotFoundException, PicoInstantiationException, IOException {
        InputStream in = getConfigInputStream(configName);
        try {
            container.registerComponents(new InputSource(in));
        } finally {
            in.close();
        }
        container.instantiateComponents();
    }

    public ObjectInstantiater buildInstantiater(final PicoContainer parentContainer) {
        return new ObjectInstantiater() {
            public Object newInstance(Class cls) {
                ClassRegistrationPicoContainer container = new HierarchicalPicoContainer.WithParentContainer(parentContainer);
                try {
                    container.registerComponent(cls);
                } catch (PicoRegistrationException e) {
                    // TODO: throw a custom exception
                    throw new RuntimeException("Could not instantiate " + cls.getName(), e);
                }
                try {
                    container.instantiateComponents();
                } catch (PicoInstantiationException e) {
                    // TODO: throw a custom exception
                    throw new RuntimeException("Could not instantiateComponents container", e);
                }
                return container.getComponent(cls);
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
