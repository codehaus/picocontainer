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

    public Container buildContainer(String configName) {
        try {
            InputSourceRegistrationNanoContainer container = new DomRegistrationNanoContainer.Default();
            configureAndStart(configName, container);
            return container;
        } catch (Exception e) {
            // TODO: Better exception
            throw new RuntimeException("Cannot build container for config: " + configName, e);
        }
    }

    public Container buildContainerWithParent(Container parentContainer, String configName) {
        try {
            InputSourceRegistrationNanoContainer container = new DomRegistrationNanoContainer.WithParentContainer(parentContainer);
            configureAndStart(configName, container);
            return container;
        } catch (Exception e) {
            // TODO: Better exception
            throw new RuntimeException("Cannot build container for config: " + configName, e);
        }
    }

    private void configureAndStart(String configName, InputSourceRegistrationNanoContainer container) throws PicoRegistrationException, ClassNotFoundException, PicoStartException, IOException {
        InputStream in = getConfigInputStream(configName);
        try {
            container.registerComponents(new InputSource(in));
        } finally {
            in.close();
        }
        container.start();
    }

    public ObjectInstantiater buildInstantiater(final Container parentContainer) {
        return new ObjectInstantiater() {
            public Object newInstance(Class cls) {
                PicoContainer container = new HierarchicalPicoContainer.WithParentContainer(parentContainer);
                try {
                    container.registerComponent(cls);
                } catch (PicoRegistrationException e) {
                    // TODO: throw a custom exception
                    throw new RuntimeException("Could not instantiate " + cls.getName(), e);
                }
                try {
                    container.start();
                } catch (PicoStartException e) {
                    // TODO: throw a custom exception
                    throw new RuntimeException("Could not start container", e);
                }
                return container.getComponent(cls);
            }
        };
    }

    public void destroyContainer(Container container) {
        // TODO
    }

    private InputStream getConfigInputStream(String configName) {
        // TODO: find a way of caching this so the XML does not have to be continually reparsed
        return servletContext.getResourceAsStream("/WEB-INF/components-" + configName + ".xml");
    }

}
