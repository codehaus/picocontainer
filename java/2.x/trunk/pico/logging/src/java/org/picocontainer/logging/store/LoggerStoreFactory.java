package org.picocontainer.logging.store;

import java.util.Map;

/**
 * <p>
 * LoggerStoreFactory is a factory interface for LoggerStore instances. There is
 * a factory implementation for each specific logger implementation (Log4J,
 * JDK14).
 * <p>
 * The factory also acts a configurator, handling the specific way in which a
 * logger is configured. The LoggerStore is configured via a map of parameters
 * passed in the create method. LoggerStoreFactory defines the keys used to
 * retrieve the elements of the map.
 * </p>
 * 
 * @author Mauro Talevi
 * @author Peter Donald
 */
public interface LoggerStoreFactory {
    /**
     * The URL key. Used to define the URL where the configuration for
     * LoggerStore can be found.
     */
    String URL_LOCATION = "org.picocontainer.logging.store.url";

    /**
     * The URL key. Used to define the URL where the configuration for
     * LoggerStore can be found.
     */
    String FILE_LOCATION = "org.picocontainer.logging.store.file";

    /**
     * Creates a LoggerStore from a given set of configuration parameters.
     * 
     * @param config the Map of parameters for the configuration of the store
     * @return the LoggerStore
     * @throws Exception if unable to create the LoggerStore
     */
    LoggerStore createLoggerStore(Map<String, Object> config) throws Exception;
}
