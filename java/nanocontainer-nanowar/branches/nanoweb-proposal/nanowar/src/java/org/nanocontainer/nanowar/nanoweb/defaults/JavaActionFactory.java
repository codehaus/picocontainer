package org.nanocontainer.nanowar.nanoweb.defaults;

import org.nanocontainer.nanowar.nanoweb.ScriptException;

public class JavaActionFactory extends AbstractActionFactory {

    private final String basePackage;

    public JavaActionFactory() {
        this.basePackage = "";
    }

    public JavaActionFactory(String basePackage) {
        if (basePackage.endsWith(".")) {
            this.basePackage = basePackage;
        } else {
            this.basePackage = basePackage + ".";
        }
    }

    protected Class getClass(String path) throws ScriptException {
        try {
            return Class.forName(basePackage + parsePath(path));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String parsePath(String path) {
        return path.substring(1, path.length()).replace('/', '.');
    }

}
