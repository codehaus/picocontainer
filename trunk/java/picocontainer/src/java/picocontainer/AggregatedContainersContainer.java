package picocontainer;

import java.util.ArrayList;
import java.util.Arrays;

public class AggregatedContainersContainer implements Container {

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

    public Object[] getComponents() {
        ArrayList comps = new ArrayList();
        for (int i = 0; i < containers.length; i++) {
            Container container = containers[i];
            comps.addAll(Arrays.asList(container.getComponents()));
        }
        return comps.toArray();
    }
}
