/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.picocontainer.PicoContainer;
import org.codehaus.groovy.syntax.lexer.CharStream;
import org.codehaus.groovy.syntax.lexer.ReaderCharStream;

import java.io.FileReader;
import java.io.Reader;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Daniel Bodart
 * @version $Revision$
 */
public class GroovyAssemblyNanoContainer extends NanoContainer {

    public GroovyAssemblyNanoContainer(Reader nanoContainerGroovy) throws Exception {
        this(nanoContainerGroovy, new ConsoleNanoContainerMonitor());
    }

    public GroovyAssemblyNanoContainer(Reader nanoContainerScript, NanoContainerMonitor monitor) throws Exception {
        super(nanoContainerScript, monitor);
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerXml = args[0];
        if (nanoContainerXml == null) {
            nanoContainerXml = "config/nanocontainer.groovy";
        }
        NanoContainer nano = new GroovyAssemblyNanoContainer(new FileReader(nanoContainerXml));
        addShutdownHook(nano);
    }

    protected void configure(Reader configuration) throws Exception {
        org.codehaus.groovy.tools.Compiler compiler = new org.codehaus.groovy.tools.Compiler();
        CharStream charStream = new ReaderCharStream(configuration);
        compiler.compile(charStream);
        final PicoContainer rootContainer = null; //gfe.createPicoContainer( ?? );

        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }
}
