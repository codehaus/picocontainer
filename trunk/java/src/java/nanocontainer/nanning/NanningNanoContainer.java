package nanocontainer.nanning;

import picocontainer.*;
import picocontainer.hierarchical.HierarchicalPicoContainer;
import com.tirsen.nanning.config.Aspect;
import com.tirsen.nanning.config.AspectSystem;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class NanningNanoContainer extends AspectSystem {
    ClassRegistrationPicoContainer serviceAndAspectContainer;
    ClassRegistrationPicoContainer componentContainer;

    public NanningNanoContainer() {
        serviceAndAspectContainer = new HierarchicalPicoContainer.Default();
        componentContainer = new HierarchicalPicoContainer(serviceAndAspectContainer, new NanningComponentFactory(this));
    }

    /**
     * Register aspect or service, these will <em>not</em> be weaved by the aspects.
     * @param serviceClass
     */
    public void registerServiceOrAspect(Class serviceClass, Class compomentImplementation)
            throws PicoRegistrationException {
        serviceAndAspectContainer.registerComponent(serviceClass, compomentImplementation);
    }

    /**
     * Register aspect or service, these will <em>not</em> be weaved by the aspects.
     * @param compomentImplementation
     */
    public void registerServiceOrAspect(Class compomentImplementation) throws PicoRegistrationException {
        serviceAndAspectContainer.registerComponent(compomentImplementation);
    }

    public void start() throws PicoInitializationException {
        serviceAndAspectContainer.initializeContainer();
        Object[] components = serviceAndAspectContainer.getComponents();
        for (int i = 0; i < components.length; i++) {
            Object component = components[i];
            if (component instanceof Aspect) {
                Aspect aspect = (Aspect) component;
                addAspect(aspect);
            }
        }

        componentContainer.initializeContainer();
    }

    public Object getComponent(Class componentType) {
        return componentContainer.getComponent(componentType);
    }

    public void registerComponent(Class componentType, Class componentImplementation) throws PicoRegistrationException {
        componentContainer.registerComponent(componentType, componentImplementation);
    }
}
