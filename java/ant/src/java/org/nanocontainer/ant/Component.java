package org.nanocontainer.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.nanocontainer.reflection.StringToObjectConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Instantiated by ant when the PicoContainer task element has a &lt;component&gt;
 * element. Holds class name of the component and additional properties.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class Component implements DynamicConfigurator {
    private String className;
    private Map properties = new HashMap();
    private String key;

    public void setClassname(String className) {
        this.className = className;
    }

    public String getClassname() {
        return className;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key != null ? key : getClassname();
    }

    public void setDynamicAttribute(String name, String value) throws BuildException {
        properties.put(name, value);
    }

    public Object createDynamicElement(String string) throws BuildException {
        throw new BuildException("No sub elements of " + string + " is allowed");
    }

    public Map getProperties() {
        return properties;
    }
}
