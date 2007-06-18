package org.picocontainer;

import java.util.Properties;
import java.io.Serializable;

public class ComponentCharacteristic implements Serializable, Cloneable {

    private Properties props = new Properties();

    public void setProperty(String name, String val) {
        props.setProperty(name, val);
    }
    public void removeProperty(String name) {
        props.remove(name);
    }
    

    public void mergeInto(ComponentCharacteristic characteristics) {
    }
    public void processed(ComponentCharacteristic characteristics) {
    }

    public boolean characterizes(ComponentCharacteristic characteristics) {
        return false;
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }

    public Object clone() {
        ComponentCharacteristic cc;
        try {
            cc = (ComponentCharacteristic) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
        cc.props = (Properties) props.clone();
        return cc;
    }

    public String toString() {
        return props.toString();   
    }

    public boolean hasUnProcessedEntries() {
        return !props.isEmpty();
    }
}
