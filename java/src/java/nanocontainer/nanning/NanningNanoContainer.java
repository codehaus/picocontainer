package nanocontainer.nanning;

import picocontainer.hierarchical.HierarchicalPicoContainer;
import picocontainer.ClassRegistrationPicoContainer;
import picocontainer.ComponentFactory;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.PicoInstantiationException;
import com.tirsen.nanning.config.Aspect;
import com.tirsen.nanning.config.AspectSystem;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class NanningNanoContainer extends HierarchicalPicoContainer {
    private final AspectSystem aspectSystem;

    private ClassRegistrationPicoContainer serviceAndAspectContainer;

    public NanningNanoContainer(ComponentFactory componentFactory, ClassRegistrationPicoContainer serviceAndAspectContainer, AspectSystem aspectSystem) {
        super(new NanningComponentFactory(aspectSystem,componentFactory), serviceAndAspectContainer);
        this.serviceAndAspectContainer = serviceAndAspectContainer;
        this.aspectSystem = aspectSystem;
    }

    /**
     * Register aspect or service, these will <em>not</em> be weaved by the aspects.
     * @param serviceClass
     */
    public void registerServiceOrAspect(Class serviceClass, Class compomentImplementation)
            throws PicoRegistrationException, PicoIntrospectionException {
        serviceAndAspectContainer.registerComponent(serviceClass, compomentImplementation);
    }

    /**
     * Register aspect or service, these will <em>not</em> be weaved by the aspects.
     * @param compomentImplementation
     */
    public void registerServiceOrAspect(Class compomentImplementation) throws PicoRegistrationException, PicoIntrospectionException {
        serviceAndAspectContainer.registerComponent(compomentImplementation);
    }

    public void instantiateComponents() throws PicoInstantiationException, PicoIntrospectionException {
        serviceAndAspectContainer.instantiateComponents();
        Object[] components = serviceAndAspectContainer.getComponents();
        for (int i = 0; i < components.length; i++) {
            Object component = components[i];
            if (component instanceof Aspect) {
                Aspect aspect = (Aspect) component;
                aspectSystem.addAspect(aspect);
            }
        }

        super.instantiateComponents();
    }
}
