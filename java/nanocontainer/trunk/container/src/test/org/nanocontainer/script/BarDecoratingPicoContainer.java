package org.nanocontainer.script;

import org.picocontainer.alternatives.AbstractDelegatingMutablePicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.Parameter;
import junit.framework.Assert;

import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;

public class BarDecoratingPicoContainer extends AbstractDelegatingMutablePicoContainer {
    public BarDecoratingPicoContainer(MutablePicoContainer delegate) {
        super(delegate);
    }

    public MutablePicoContainer makeChildContainer() {
        return null;
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        Assert.assertEquals(Vector.class, componentImplementation);
        return super.registerComponentImplementation(HashMap.class, HashMap.class, parameters);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        Assert.assertEquals(Vector.class, componentImplementation);
        return super.registerComponentImplementation(HashMap.class, HashMap.class);
    }

}
