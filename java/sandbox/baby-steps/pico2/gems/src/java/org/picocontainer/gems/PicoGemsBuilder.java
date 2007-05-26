package org.picocontainer.gems;

import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.gems.adapters.ImplementationHidingComponentAdapterFactory;

public class PicoGemsBuilder {

    public static ComponentAdapterFactory IMPL_HIDING() {
        return new ImplementationHidingComponentAdapterFactory();
    }


}
