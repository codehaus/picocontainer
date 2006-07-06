package org.nanocontainer.nanowar.server;

import org.mortbay.jetty.servlet.FilterHolder;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.servlet.Filter;

/**
 * @deprecated - to be replaced by forthcoming 'Jervlet' release
 */
public class FilterHolderPicoEdition extends FilterHolder {

    private final PicoContainer parentContainer;

    public FilterHolderPicoEdition(PicoContainer parentContainer) {
        this.parentContainer = parentContainer;
    }

    public FilterHolderPicoEdition(Class filterClass, PicoContainer parentContainer) {
        super(filterClass);
        this.parentContainer = parentContainer;
    }

    public synchronized Object newInstance() throws InstantiationException, IllegalAccessException {
        DefaultPicoContainer child = new DefaultPicoContainer(parentContainer);
        child.registerComponentImplementation(Filter.class, _class);
        return child.getComponentInstance(Filter.class);
    }


}
