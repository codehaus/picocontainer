package org.nanocontainer.booter;

import org.nanocontainer.OldDefaultNanoContainer;
import org.nanocontainer.ClassName;

import java.util.Map;

public class BrownBearHelper {

    public BrownBearHelper() throws ClassNotFoundException {
       OldDefaultNanoContainer nano = new OldDefaultNanoContainer();
        nano.registerComponent(Map.class, new ClassName("java.util.HashMap"));
    }

}
