package picocontainer;

import java.util.List;
import java.util.ArrayList;

public class OverriddenStartableLifecycleManager implements StartableLifecycleManager {

    private List started = new ArrayList();
    private List stopped = new ArrayList();

    public void startComponent(Object component) {
        started.add(component.getClass());
    }

    public void stopComponent(Object component) {
        stopped.add(component.getClass());
    }

    public List getStarted() {
        return started;
    }

    public List getStopped() {
        return stopped;
    }
}
