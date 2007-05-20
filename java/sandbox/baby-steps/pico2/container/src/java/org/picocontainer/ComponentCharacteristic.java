package org.picocontainer;

import java.util.Properties;
import java.io.Serializable;

public class ComponentCharacteristic implements Serializable, Cloneable {

    private Properties props = new Properties();

    public void setProperty(String name, String val) {
        props.setProperty(name, val);
    }
    

    public void mergeInto(ComponentCharacteristic characteristics) {
    }
    public boolean isSoCharacterized(ComponentCharacteristic characteristics) {
        return false;
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }

    public Object clone() {
        ComponentCharacteristic cc = null;
        try {
            cc = (ComponentCharacteristic) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
        cc.props = (Properties) props.clone();
        return cc;
    }
}
