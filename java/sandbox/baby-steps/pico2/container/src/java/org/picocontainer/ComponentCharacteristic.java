package org.picocontainer;

import java.util.Properties;
import java.io.Serializable;

public class ComponentCharacteristic implements Serializable, Cloneable {

    private Properties props = new Properties();

    protected void setProperty(String name, String val) {
        props.setProperty(name, val);
    }
    protected void removeProperty(String name) {
        props.remove(name);
    }
    

    public void mergeInto(ComponentCharacteristic characteristics) {
    }
    public void setProcessedIn(ComponentCharacteristic characteristics) {
    }

    public boolean isCharacterizedIn(ComponentCharacteristic characteristics) {
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
