package picocontainer;

public class DummyStartableLifecycleManagerImpl
        implements StartableLifecycleManager {
    public void startComponent(Object component) throws PicoStartException {
    }

    public void stopComponent(Object component) throws PicoStopException {
    }
}
