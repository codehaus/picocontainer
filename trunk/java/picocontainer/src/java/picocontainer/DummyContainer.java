package picocontainer;

public class DummyContainer implements Container {
    public boolean hasComponent(Class compType) {
        return false;
    }

    public Object getComponent(Class compType) {
        return null;
    }

    public Object[] getComponents() {
        return new Object[0];
    }
}
