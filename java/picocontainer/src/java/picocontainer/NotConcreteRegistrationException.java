package picocontainer;

/**
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class NotConcreteRegistrationException extends PicoRegistrationException {
    private final Class componentClass;

    public NotConcreteRegistrationException(Class componentClass) {
        this.componentClass = componentClass;
    }

    public String getMessage() {
        return "Bad Access: " + componentClass.getName();
    }

    public Class getComponentClass() {
        return componentClass;
    }
}
