/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.codehaus.groovy.syntax.TokenStream;
import org.codehaus.groovy.syntax.lexer.CharStream;
import org.codehaus.groovy.syntax.lexer.Lexer;
import org.codehaus.groovy.syntax.lexer.LexerTokenStream;
import org.codehaus.groovy.syntax.lexer.StringCharStream;
import org.codehaus.groovy.syntax.parser.Parser;
import org.picocontainer.PicoContainer;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @author Daniel Bodart
 * @version $Revision$
 */
public class GroovyAssemblyNanoContainer extends NanoContainer {

    Parser parser;

    public GroovyAssemblyNanoContainer(Reader nanoContainerGroovy) throws IOException {
        this(nanoContainerGroovy, new ConsoleNanoContainerMonitor());
    }

    public GroovyAssemblyNanoContainer(Reader nanoContainerGroovy, NanoContainerMonitor monitor) throws IOException {
        super(monitor);

        LineNumberReader assemblyLineReader = new LineNumberReader( nanoContainerGroovy );
        String line = null;
        String assembly = "";
        while ((line = assemblyLineReader.readLine()) != null) {
            assembly = assembly + line + "\n";
        }
        parser = newParser( assembly );
        org.codehaus.groovy.tools.Compiler compiler = new org.codehaus.groovy.tools.Compiler();
//        clazz = compiler.xxx();
//        clazz.execute();
//
//        GroovyFrontEnd gfe = new GroovyFrontEnd();
        final PicoContainer rootContainer = null; //gfe.createPicoContainer( ?? );

        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }

    protected Parser newParser(String assembly)
    {
        CharStream  chars  = new StringCharStream( assembly );
        Lexer       lexer  = new Lexer( chars );
        TokenStream tokens = new LexerTokenStream( lexer );

        return new Parser( tokens );
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerXml = args[0];
        if (nanoContainerXml == null) {
            nanoContainerXml = "config/nanocontainer.groovy";
        }
        NanoContainer nano = new GroovyAssemblyNanoContainer(new FileReader(nanoContainerXml));
        addShutdownHook(nano);
    }
}
