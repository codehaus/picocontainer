/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.dynaop;

import java.net.URL;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import dynaop.remote.Home;

/**
 * <code>ComponentAdapter</code> that retrieves the component implementation
 * remotely using the Dynaop remoting framework. This class does not cache the
 * remote component locally; you might consider wrapping this component adapter
 * with a <code>CachingComponentAdapter</code>.
 * 
 * @author Stephen Molitor
 */
public class RemoteComponentAdapter implements ComponentAdapter {

    private URL remoteUrl;
    private Class remoteInterface;
    private Object componentKey;
    private PicoContainer container;
    private Home home;

    /**
     * Creates a new <code>RemoteComponentAdapter</code>.
     * 
     * @param remoteUrl
     *            the URL to retrieve the remote component from.
     * @param remoteInterface
     *            the remote interface
     * @param componentKey
     *            the Pico component key for this component.
     * @param home
     *            the Dynaop <code>Home</code> object to use to retrieve the
     *            remote reference.
     */
    public RemoteComponentAdapter(URL remoteUrl, Class remoteInterface,
                    Object componentKey, Home home) {
        this.remoteUrl = remoteUrl;
        this.remoteInterface = remoteInterface;
        this.componentKey = componentKey;
        this.home = home;
    }

    /**
     * Creates a new <code>RemoteComponentAdapter</code>.
     * 
     * @param remoteUrl
     *            the URL to retrieve the remote component from.
     * @param remoteInterface
     *            the remote interface
     * @param componentKey
     *            the Pico component key for this component.
     */
    public RemoteComponentAdapter(URL remoteUrl, Class remoteInterface,
                    Object componentKey) {
        this(remoteUrl, remoteInterface, componentKey, new Home());
    }

    /**
     * Gets the component key.
     * 
     * @return the component key.
     */
    public Object getComponentKey() {
        return componentKey;
    }

    /**
     * Gets the component implementation.
     * 
     * @return the component implementation.
     */
    public Class getComponentImplementation() {
        return remoteInterface;
    }

    /**
     * Retreives the remote component reference.
     * 
     * @return the remote reference.
     */
    public Object getComponentInstance() {
        return home.create(remoteUrl, remoteInterface);
    }

    /**
     * Gets the Pico container.
     * 
     * @return the Pico container.
     */
    public PicoContainer getContainer() {
        return container;
    }

    /**
     * Sets the Pico container.
     * 
     * @param picoContainer
     *            the Pico container.
     */
    public void setContainer(PicoContainer picoContainer) {
        this.container = picoContainer;
    }

    /**
     * Verifies that this components dependencies can be satisifed.
     */
    public void verify() {
    }

}