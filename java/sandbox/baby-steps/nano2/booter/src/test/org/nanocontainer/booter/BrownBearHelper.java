package org.nanocontainer.booter;

import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.ClassName;

import java.util.Map;

public class BrownBearHelper {

    public BrownBearHelper() throws ClassNotFoundException {
       DefaultNanoContainer nano = new DefaultNanoContainer();
        nano.registerComponent(Map.class, new ClassName("java.util.HashMap"));
    }

}
