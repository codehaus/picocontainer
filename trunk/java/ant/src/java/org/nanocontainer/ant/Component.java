package org.picoextras.ant;

import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.BuildException;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentParameter;
import org.picoextras.reflection.StringToObjectConverter;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class Component implements DynamicConfigurator {
    private String className;
    private Map properties = new HashMap();
    private List parameters = new ArrayList();
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

//    public void setPropertiesOn(BeanPropertyComponentAdapterFactory.Adapter adapter) throws BeanPropertyComponentAdapterFactory.NoSuchPropertyException {
//        for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
//            String name = (String) i.next();
//            String value = (String) properties.get(name);
//            adapter.setPropertyValue(name, value);
//        }
//    }

    public ConstantParam createConstant() {
        ConstantParam constant = new ConstantParam();
        parameters.add(constant);
        return constant;
    }

    public Parameter[] getParameters() {
        if (parameters.size() == 0) {
            return null;
        }

        Parameter[] result = new Parameter[parameters.size()];
        for (ListIterator iterator = parameters.listIterator(); iterator.hasNext();) {
            AbstractParam constant = (AbstractParam) iterator.next();
            result[iterator.previousIndex()] = constant.createParameter();
        }

        return result;
    }

    public ComponentParam createComponent() {
        ComponentParam param = new ComponentParam();
        parameters.add(param);
        return param;
    }

    public Map getProperties() {
        return properties;
    }

    public static abstract class AbstractParam {
        protected Class type = String.class;

        public abstract Parameter createParameter();

        public void setType(Class type) {
            this.type = type;
        }
    }

    public static class ComponentParam extends AbstractParam {
        public Parameter createParameter() {
            return new ComponentParameter(type);
        }
    }

    public static class ConstantParam extends AbstractParam {
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public Parameter createParameter() {
            return new ConstantParameter(new StringToObjectConverter().convertTo(type, value));
        }
    }
}
