package nanocontainer.servlet.containerfactory;

import nanocontainer.DomRegistrationNanoContainer;
import nanocontainer.InputSourceRegistrationNanoContainer;
import nanocontainer.servlet.ContainerFactory;
import org.xml.sax.InputSource;
import picocontainer.Container;
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
            return container;
        } catch (Exception e) {
            throw new CannotBuildContainerForWhateverReasonException("Cannot build container for config: " + configName, e);
        }
    }

    public Container buildContainerWithParent(Container parentContainer, String configName) {
        try {
            InputSourceRegistrationNanoContainer container = new DomRegistrationNanoContainer.WithParentContainer(parentContainer);
            container.registerComponents(getConfigInputStream(configName));
            return container;
        } catch (Exception e) {
            throw new CannotBuildContainerForWhateverReasonException("Cannot build container for config: " + configName, e);
        }
    }

    public void destroyContainer(Container container) {
        // TODO
    }

    private InputSource getConfigInputStream(String configName) {
        // TODO: find a way of caching this so the XML does not have to be continually reparsed
        return new InputSource(servletContext.getResourceAsStream("/WEB-INF/components-" + configName + ".xml"));
    }

}

