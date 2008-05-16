package org.picocontainer.logging.store.factories;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.picocontainer.logging.store.LoggerStore;
import org.picocontainer.logging.store.LoggerStoreFactory;

/**
 * The abstract class that makes it easy to create LoggerStoreFactory
 * implementations.
 * 
 * @author Peter Donald
 * @author Mauro Talevi
 */
public abstract class AbstractLoggerStoreFactory implements LoggerStoreFactory {
    /**
     * Creates a LoggerStore from a given set of configuration parameters. The
     * configuration map contains entrys specific to the concrete
     * implementation.
     * 
     * @param config the parameter map to configuration of the store
     * @return the LoggerStore
     * @throws Exception if unable to create the LoggerStore
     */
    public LoggerStore createLoggerStore(final Map<String,Object> config) throws Exception {
        final LoggerStore loggerStore = doCreateLoggerStore(config);
        return loggerStore;
    }

    protected abstract LoggerStore doCreateLoggerStore(Map<String,Object> config) throws Exception;

    /**
     * Utility method to throw exception indicating input data was invalid.
     * 
     * @return never returns
     * @throws Exception indicating input data was invalid
     */
    protected LoggerStore missingConfiguration() throws Exception {
        throw new Exception("Invalid configuration");
    }

    /**
     * A utility method to retrieve a InputStream from input map. It will
     * systematically go through the following steps to attempt to locate the
     * InputStream stopping at success.
     * <ul>
     * <li>Lookup LoggerStoreFactory.URL_LOCATION for string defining URL
     * location of input configuration.</li>
     * <li>Lookup java.net.URL for URL object defining URL location of input
     * configuration.</li>
     * <li>Lookup LoggerStoreFactory.FILE_LOCATION for string defining File
     * location of input configuration.</li>
     * <li>Lookup java.io.File for File object defining File location of input
     * configuration.</li>
     * <li>Lookup java.io.InputStream for InputStream object.</li>
     * </ul>
     * 
     * @param config the input map
     * @return the InputStream or null if no stream present
     * @throws Exception if there was a problem aquiring stream
     */
    protected InputStream getInputStream(final Map<String,Object> config) throws Exception {
        final String urlLocation = (String) config.get(URL_LOCATION);
        URL url = null;
        if (null != urlLocation) {
            url = new URL(urlLocation);
        }
        if (null == url) {
            url = (URL) config.get(URL.class.getName());
        }
        if (null != url) {
            return url.openStream();
        }

        final String fileLocation = (String) config.get(FILE_LOCATION);
        File file = null;
        if (null != fileLocation) {
            file = new File(fileLocation);
        }
        if (null == file) {
            file = (File) config.get(File.class.getName());
        }
        if (null != file) {
            return new FileInputStream(file);
        }

        return (InputStream) config.get(InputStream.class.getName());
    }
}
