package picocontainer;

public interface LifecycleContainer extends Container {

    /**
     * Instantiates all registered components.
     *
     * @throws PicoStartException if one or more components couldn't be instantiated.
     */
    void start() throws PicoStartException;

    void stop() throws PicoStopException;

}
