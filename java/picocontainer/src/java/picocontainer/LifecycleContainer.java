package picocontainer;

public interface LifecycleContainer extends PicoContainer {

    /**
     * Instantiates all registered components.
     *
     * @throws PicoStartException if one or more components couldn't be instantiated.
     */
    void start() throws PicoStartException;

    void stop() throws PicoStopException;

    void dispose() throws PicoDisposalException;

}
