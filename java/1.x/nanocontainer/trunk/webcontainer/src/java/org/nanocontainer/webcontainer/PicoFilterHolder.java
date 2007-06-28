package org.nanocontainer.webcontainer;

import javax.servlet.Filter;

import org.mortbay.jetty.servlet.FilterHolder;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public class PicoFilterHolder extends FilterHolder {

    private final PicoContainer parentContainer;

    public PicoFilterHolder(PicoContainer parentContainer) {
        this.parentContainer = parentContainer;
    }

    public PicoFilterHolder(Class filterClass, PicoContainer parentContainer) {
        super(filterClass);
        this.parentContainer = parentContainer;
    }

    public synchronized Object newInstance() throws InstantiationException, IllegalAccessException {
        DefaultPicoContainer child = new DefaultPicoContainer(parentContainer);
        child.registerComponentImplementation(Filter.class, _class);
        return child.getComponentInstance(Filter.class);
    }


}
