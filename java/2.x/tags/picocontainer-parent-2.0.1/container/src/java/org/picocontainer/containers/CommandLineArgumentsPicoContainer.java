/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.containers;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;

/**
 * CommandLineArgumentsPicoContainer configured itself from array of strings
 * which are most likely coming in as command line arguments
 * 
 */
@SuppressWarnings("serial")
public class CommandLineArgumentsPicoContainer extends AbstractDelegatingPicoContainer {
    public CommandLineArgumentsPicoContainer(String separator, String[] arguments) {
    	this(separator,arguments,null);
    }

    public CommandLineArgumentsPicoContainer(String separator, String[] arguments, PicoContainer parent ) {
    	super(new DefaultPicoContainer(parent));
        for (String argument : arguments) {
            processArgument(argument, separator);
        }
    }
    public CommandLineArgumentsPicoContainer(String separator, StringReader argumentsProps) throws IOException {
        this(separator, argumentsProps, new String[0]);
    }
    
    public CommandLineArgumentsPicoContainer(String separator, StringReader argumentProperties, String[] arguments) throws IOException{
    	this(separator,argumentProperties,arguments,null);
    }

    public CommandLineArgumentsPicoContainer(String separator, StringReader argumentProperties, String[] arguments, PicoContainer parent)
        throws IOException {
    	super(new DefaultPicoContainer(parent));
    	
        LineNumberReader lnr = new LineNumberReader(argumentProperties);
        String line = lnr.readLine();
        while (line != null) {
            processArgument(line, separator);
            line = lnr.readLine();
        }
        for (String argument : arguments) {
            processArgument(argument, separator);
        }
    }
    
    public CommandLineArgumentsPicoContainer(String[] arguments) {
        this("=", arguments);
    }

    public CommandLineArgumentsPicoContainer(String[] arguments, PicoContainer parent) {
    	this("=", arguments,parent);
    }

    private void addConfig(String key, Object val) {
        if (getDelegate().getComponent(key) != null) {
            getDelegate().removeComponent(key);
        }
        getDelegate().addConfig(key, val);
    }

    public <T> T getComponent(Class<T> componentType) {
        return null;
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        return null;
    }

    public PicoContainer getParent() {
        return new EmptyPicoContainer();
    }

    private void processArgument(String argument, String separator) {
        String[] kvs = argument.split(separator);
        if (kvs.length == 2) {
            addConfig(kvs[0], kvs[1]);
        } else if (kvs.length == 1) {
            addConfig(kvs[0], "true");
        } else if (kvs.length > 2) {
            throw new PicoCompositionException(
                "Argument name'"+separator+"'value pair '" + argument + "' has too many '"+separator+"' characters");
        }
    }
    
    public MutablePicoContainer getDelegate() {
    	return (MutablePicoContainer) super.getDelegate();
    }
}
