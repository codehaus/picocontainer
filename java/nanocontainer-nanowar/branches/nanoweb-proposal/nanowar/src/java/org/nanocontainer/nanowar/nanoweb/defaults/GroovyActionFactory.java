package org.nanocontainer.nanoweb.defaults;

import groovy.lang.GroovyClassLoader;

import java.io.File;

/**
 * Loads classes from scripts and caches them based on the URL's timestamp.
 * 
 * @version $Revision: 1.1 $
 */
public class GroovyActionFactory extends AbstractFileBasedActionFactory {

    public GroovyActionFactory(String rootPath, String extension) {
        super(rootPath, extension);
    }

    public GroovyActionFactory(String rootPath) {
        super(rootPath, "groovy");
    }

    protected Class getClass(File actionFile) throws Exception {
        GroovyClassLoader loader = new GroovyClassLoader(getClass().getClassLoader());
        return loader.parseClass(actionFile);
    }

}
