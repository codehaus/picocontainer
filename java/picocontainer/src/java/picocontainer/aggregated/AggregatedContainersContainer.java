package picocontainer.aggregated;

import picocontainer.AbstractContainer;
import picocontainer.Container;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class AggregatedContainersContainer extends AbstractContainer {

    private final Container[] containers;

    public AggregatedContainersContainer(final Container[] containers) {
        if( containers == null ) {
            throw new NullPointerException("containers can't be null");
        }
        for (int i = 0; i < containers.length; i++) {
            Container container = containers[i];
            if( container == null ) {
                throw new NullPointerException("Container at position " + i + " was null");
            }
        }
        this.containers = containers;
    }

    public static class Filter extends AggregatedContainersContainer {
        private final Container subject;

        public Filter(final Container container) {
            super(new Container[]{container});
            subject = container;
        }

        public Container getSubject() {
            return subject;
        }
    }

    public boolean hasComponent(Class compType) {
        for (int i = 0; i < containers.length; i++) {
            Container container = containers[i];
            if (container.hasComponent(compType)) {
                return true;
            }
        }
        return false;
    }

    public Object getComponent(Class compType) {
        for (int i = 0; i < containers.length; i++) {
            Container container = containers[i];
            if (container.hasComponent(compType)) {
                return container.getComponent(compType);
            }
        }
        return null;
    }

    public Class[] getComponentTypes() {
        Set componentTypes = new HashSet();
        for (int i = 0; i < containers.length; i++) {
            Container container = containers[i];
            componentTypes.addAll(Arrays.asList(container.getComponentTypes()));
        }
        return (Class[]) componentTypes.toArray(new Class[containers.length]);
    }
}
