package org.picocontainer;

import java.util.Properties;
import java.io.Serializable;

public class ComponentCharacteristic implements Serializable {

    private Properties props = new Properties();

    public void setProperty(String name, String val) {
        props.setProperty(name, val);
    }
    

    public void mergeInto(ComponentCharacteristic rc) {
    }
    public boolean isSoCharacterized(ComponentCharacteristic rc) {
        return false;
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }
}
