package org.picocontainer.containers;

import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVisitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.ComponentCharacteristic;

import java.io.Serializable;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

/**
 * empty pico container serving as recoil damper in situations where you
 * do not like to check whether container reference suplpied to you
 * is null or not
 *
 * @author Konstantin Pribluda
 * @since 1.1
 */
public class ImmutablePicoContainer extends AbstractDelegatingMutablePicoContainer {

    public ImmutablePicoContainer(MutablePicoContainer delegate) {
        super(delegate);
    }

    public MutablePicoContainer makeChildContainer() {
        throw new UnsupportedOperationException();
    }


    public MutablePicoContainer addComponent(Object componentKey,
                                             Object componentImplementationOrInstance,
                                             Parameter... parameters) throws PicoRegistrationException
    {
        throw new UnsupportedOperationException();
    }

    public MutablePicoContainer addComponent(Object implOrInstance) throws PicoRegistrationException {
        throw new UnsupportedOperationException();
    }

    public MutablePicoContainer addAdapter(ComponentAdapter componentAdapter) throws PicoRegistrationException {
        throw new UnsupportedOperationException();
    }

    public ComponentAdapter removeComponent(Object componentKey) {
        throw new UnsupportedOperationException();
    }

    public ComponentAdapter removeComponentByInstance(Object componentInstance) {
        throw new UnsupportedOperationException();
    }

    public void start() {
        throw new UnsupportedOperationException();
    }

    public void stop() {
        throw new UnsupportedOperationException();
    }

    public void dispose() {
        throw new UnsupportedOperationException();
    }

    public MutablePicoContainer addChildContainer(PicoContainer child) {
        throw new UnsupportedOperationException();
    }

    public boolean removeChildContainer(PicoContainer child) {
        throw new UnsupportedOperationException();
    }

    public MutablePicoContainer change(ComponentCharacteristic... characteristics) {
        throw new UnsupportedOperationException();
    }

    public MutablePicoContainer as(ComponentCharacteristic... characteristics) {
        throw new UnsupportedOperationException();
    }

    public ComponentAdapter lastCA() {
        throw new UnsupportedOperationException();
    }

    public void accept(PicoVisitor visitor) {
        getDelegate().accept(visitor);
    }


    public boolean equals(Object obj) {
        return obj == this
               || (obj != null && obj == getDelegate())
               || (obj instanceof ImmutablePicoContainer && ((ImmutablePicoContainer) obj).getDelegate() == getDelegate())
            ;
    }


    public int hashCode() {
        return getDelegate().hashCode();   
    }
}
