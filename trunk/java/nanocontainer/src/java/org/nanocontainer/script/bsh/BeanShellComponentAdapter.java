/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.picoextras.script.bsh;

import java.util.Arrays;
import java.util.Collections;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.URL;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;

import bsh.Interpreter;
import bsh.EvalError;

/**
 * This adapter relies on <a href="http://beanshell.org/">Bsh</a> for instantiation
 * (and possibly also initialisation) of component instances.
 * <p>
 * When {@link #getComponentInstance} is called (by PicoContainer),
 * the adapter instance will look for a script with the same name as the component implementation
 * class (but with the .bsh extension). This script must reside in the same folder as the class.
 * (It's ok to have them both in a jar).
 * <p>
 * The bsh script's only contract is that it will have to instantiate a bsh variable called
 * "instance".
 * <p>
 * The script will have access to the following variables:
 * <ul>
 *   <li>adapter - the adapter calling the script</li>
 *   <li>picoContainer - the MutablePicoContainer calling the adapter</li>
 *   <li>componentKey - the component key</li>
 *   <li>componentImplementation - the component implementation</li>
 *   <li>parameters - the ComponentParameters (as a List)</li>
 * </ul>
 *
 * @author <a href="mail at leosimons dot com">Leo Simons</a>
 * @author Aslak Hellesoy
 * @version $Id$
 */
public class BeanShellComponentAdapter implements ComponentAdapter {
    private final Object componentKey;
    private final Class componentImplementation;
    private final Parameter[] parameters;

    private Object instance = null;

    public BeanShellComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
        this.componentKey = componentKey;
        this.componentImplementation = componentImplementation;
        this.parameters = parameters;
    }

    public Object getComponentKey() {
        return componentKey;
    }

    public Class getComponentImplementation() {
        return componentImplementation;
    }

    public Object getComponentInstance(MutablePicoContainer picoContainer)
            throws PicoInitializationException, PicoIntrospectionException {

        if (instance == null) {
            try {
                Interpreter i = new Interpreter();
                i.set("adapter", this);
                i.set("picoContainer", picoContainer);
                i.set("componentKey", componentKey);
                i.set("componentImplementation", componentImplementation);
                i.set("parameters", parameters != null ? Arrays.asList(parameters) : Collections.EMPTY_LIST);
                i.eval("import " + componentImplementation.getName() + ";");

                String scriptPath = "/" + componentImplementation.getName().replace('.', '/') + ".bsh";
                URL scriptURL = componentImplementation.getResource(scriptPath);
                if(scriptURL == null) {
                    throw new BeanShellScriptInitializationException("Couldn't load script at path " + scriptPath);
                }
                Reader sourceReader = new InputStreamReader(scriptURL.openStream());
                i.eval(sourceReader, i.getNameSpace(), scriptURL.toExternalForm());

                instance = i.get("instance");
                if(i == null) {
					throw new BeanShellScriptInitializationException("The 'instance' variable was not instantiated");
				}
            } catch (EvalError e) {
                throw new BeanShellScriptInitializationException(e);
            } catch (IOException e) {
                throw new BeanShellScriptInitializationException(e);
            }
        }
        return instance;
    }

    public void verify(PicoContainer picoContainer) throws NoSatisfiableConstructorsException {
    }
}
