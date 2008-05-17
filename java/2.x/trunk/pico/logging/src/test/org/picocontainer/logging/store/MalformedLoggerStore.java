/*
 * Copyright (C) PicoContainer Organization. All rights reserved.            
 * ------------------------------------------------------------------------- 
 * The software in this package is published under the terms of the BSD      
 * style license a copy of which has been included with this distribution in 
 * the LICENSE.txt file.                                                     
 */ 
package org.picocontainer.logging.store;

import org.picocontainer.logging.Logger;
import org.picocontainer.logging.store.stores.AbstractLoggerStore;

/**
 * @author Peter Donald
 */
public class MalformedLoggerStore extends AbstractLoggerStore {
    protected Logger createLogger(String name) {
        return null;
    }

    public void close() {
    }
}
