package org.picocontainer.adapters;

import org.picocontainer.defaults.ComponentAdapterFactory;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: May 25, 2007
 * Time: 6:15:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DecoratingComponentAdapterFactory {
    AbstractDecoratingComponentAdapterFactory forThis(ComponentAdapterFactory delegate);
}
