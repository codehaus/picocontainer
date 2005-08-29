/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 29.08.2005 by Jörg Schaible
 */
package org.picocontainer.gems.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;


/**
 * @author J&ouml;rg Schaible
 */
public class PoolingComponentAdapterTest extends AbstractComponentAdapterTestCase {

    // -------- TCK -----------

    protected Class getComponentAdapterType() {
        return PoolingComponentAdapter.class;
    }

    protected int getComponentAdapterNature() {
        // @todo: It is serializable ...
        return super.getComponentAdapterNature() & ~(INSTANTIATING | RESOLVING | VERIFYING | SERIALIZABLE);
    }

    private ComponentAdapter createPoolOfTouchables() {
        return new PoolingComponentAdapter(new ConstructorInjectionComponentAdapter(
                Touchable.class, SimpleTouchable.class));
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return createPoolOfTouchables();
    }

    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        return createPoolOfTouchables();
    }

    protected ComponentAdapter prepDEF_visitable() {
        return createPoolOfTouchables();
    }
}
