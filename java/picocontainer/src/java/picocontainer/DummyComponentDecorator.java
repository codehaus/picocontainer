package picocontainer;

public class DummyComponentDecorator implements ComponentDecorator {
    public Object decorateComponent(Class compType, Object instance) {
        return instance;
    }
}
