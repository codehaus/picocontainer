package org.picocontainer.defaults;

import org.picocontainer.extras.DecoratingComponentAdapterFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CachingComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    public CachingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }
}
