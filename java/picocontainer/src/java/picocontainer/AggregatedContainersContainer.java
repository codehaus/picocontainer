package picocontainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class AggregatedContainersContainer extends AbstractContainer {

    private final Container[] containers;

    public AggregatedContainersContainer(final Container[] containers) {
        this.containers = containers;
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
