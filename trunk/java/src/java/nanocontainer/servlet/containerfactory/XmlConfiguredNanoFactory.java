package nanocontainer.servlet.containerfactory;

import nanocontainer.DomRegistrationNanoContainer;
import nanocontainer.InputSourceRegistrationNanoContainer;
import nanocontainer.servlet.ContainerFactory;
import nanocontainer.servlet.ObjectInstantiater;
import org.xml.sax.InputSource;
import picocontainer.*;

import javax.servlet.ServletContext;

public class XmlConfiguredNanoFactory implements ContainerFactory {
    private ServletContext servletContext;

    public XmlConfiguredNanoFactory(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Container buildContainer(String configName) {
        try {
            InputSourceRegistrationNanoContainer container = new DomRegistrationNanoContainer.Default();
            container.registerComponents(getConfigInputStream(configName));
            container.start();
            return container;
        } catch (Exception e) {
            // TODO: Better exception
            throw new RuntimeException("Cannot build container for config: " + configName, e);
        }
    }

    public Container buildContainerWithParent(Container parentContainer, String configName) {
        try {
            InputSourceRegistrationNanoContainer container = new DomRegistrationNanoContainer.WithParentContainer(parentContainer);
            container.registerComponents(getConfigInputStream(configName));
            container.start();
            return container;
        } catch (Exception e) {
            // TODO: Better exception
            throw new RuntimeException("Cannot build container for config: " + configName, e);
        }
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

    private InputSource getConfigInputStream(String configName) {
        // TODO: find a way of caching this so the XML does not have to be continually reparsed
        return new InputSource(servletContext.getResourceAsStream("/WEB-INF/components-" + configName + ".xml"));
    }

}
