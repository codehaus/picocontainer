/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.picocontainer.PicoConfigurationException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, PicoConfigurationException, ParserConfigurationException {
        if (args.length == 0) {
            System.err.println("NanoContainer: Needs a configuation file as a parameter");
            System.exit(10);
        }

        // Monitor
        NanoContainerMonitor nanoContainerMonitor = new NullNanoContainerMonitor();
        if (args.length == 2) {
            if (args[1].equals("CommonsLogging")) {
                nanoContainerMonitor = new CommonsLoggingNanoContainerMonitor();
            } else if (args[1].equals("Log4J")) {
                nanoContainerMonitor = new Log4JNanoContainerMonitor();
            } else if (args[1].equals("Console")) {
                nanoContainerMonitor = new ConsoleNanoContainerMonitor();
            }
        }

        String nanoContainerConfig = args[0];
        if (nanoContainerConfig.toLowerCase().endsWith(".js")) {
            NanoContainer nano = new JavaScriptAssemblyNanoContainer(new FileReader(nanoContainerConfig), nanoContainerMonitor);
            JavaScriptAssemblyNanoContainer.addShutdownHook(nano);
        } else if (nanoContainerConfig.toLowerCase().endsWith(".xml")) {
            NanoContainer nano = new XmlAssemblyNanoContainer(new FileReader(nanoContainerConfig), nanoContainerMonitor);
            XmlAssemblyNanoContainer.addShutdownHook(nano);
        } else {
            System.err.println("NanoContainer: Unknown configuration file suffix, .js or .xml expected");
            System.exit(10);
        }
    }
}    
    

