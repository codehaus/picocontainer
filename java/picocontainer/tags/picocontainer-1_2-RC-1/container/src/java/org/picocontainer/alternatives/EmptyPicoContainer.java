/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/
package org.picocontainer.alternatives;

import java.io.Serializable;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ComponentAdapter;

/**
 * empty pico container serving as recoil damper in situations where you 
 * do not like to check whether container reference suplpied to you 
 * is null or not
 * @author  Konstantin Pribluda 
 * @since 1.1
*/
public class EmptyPicoContainer implements PicoContainer, Serializable {
    public Object getComponentInstance(Object componentKey) {
        return null;
    }
   
    public Object getComponentInstanceOfType(Class componentType) {
        return null;
    }
    public List getComponentInstances() {
        return Collections.EMPTY_LIST;
    }
    
    public PicoContainer getParent() {
        return null;
    }
    public ComponentAdapter getComponentAdapter(Object componentKey) {
        return null;
    }
    
    public ComponentAdapter getComponentAdapterOfType(Class componentType) {
        return null;
    }
   
    public Collection getComponentAdapters() {
        return Collections.EMPTY_LIST;
    }
    
    public List getComponentAdaptersOfType(Class componentType) {
        return Collections.EMPTY_LIST;
    }
    
    public void verify()  {}
    
    public void accept(PicoVisitor visitor) { }
    
    public List getComponentInstancesOfType(Class componentType) {
        return Collections.EMPTY_LIST;
    }

    public void start() {}
    public void stop() {}
    public void dispose() {}
}
