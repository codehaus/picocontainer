package org.nanocontainer;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.PicoContainer;


public class NanoBuilder {

    private Class ncClass = DefaultNanoContainer.class;


    public NanoContainer build() {
        DefaultPicoContainer temp = new DefaultPicoContainer();
        temp.addComponent(NanoContainer.class, ncClass);
        return temp.getComponent(NanoContainer.class);
    }
}
