package org.nanocontainer.script;

import org.picocontainer.alternatives.AbstractDelegatingMutablePicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.Parameter;

import java.util.HashMap;
import java.util.ArrayList;

import junit.framework.Assert;

public class FooDecoratingPicoContainer extends AbstractDelegatingMutablePicoContainer {
    public FooDecoratingPicoContainer(MutablePicoContainer delegate) {
        super(delegate);
    }
    public MutablePicoContainer makeChildContainer() {
        return null;
    }

    public MutablePicoContainer registerComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters) throws PicoRegistrationException {
        Assert.assertEquals(HashMap.class, componentImplementationOrInstance);
        return super.registerComponent(ArrayList.class, ArrayList.class, parameters);
    }

}
