/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.picoextras.script.PicoCompositionException;
import org.realityforge.cli.CLArgsParser;
import org.realityforge.cli.CLOptionDescriptor;
import org.realityforge.cli.CLOption;
import org.realityforge.cli.CLUtil;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;

public class Main {

    private static final int HELP_OPT = 'h';
    private static final int VERSION_OPT = 'v';
    private static final int MONITOR_OPT = 'm';
    private static final int COMPOSITION_OPT = 'c';

    private static final CLOptionDescriptor[] OPTIONS = new CLOptionDescriptor[]
    {
        new CLOptionDescriptor("help",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                HELP_OPT,
                "print this message and exit"),
        new CLOptionDescriptor("version",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                VERSION_OPT,
                "print the version information and exit"),
        new CLOptionDescriptor("monitor",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                MONITOR_OPT,
                "specify the monitor implemenatation"),
        new CLOptionDescriptor("composition",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                COMPOSITION_OPT,
                "specify the assembly file")

    };

    public static void main(String[] args) throws IOException, ClassNotFoundException, PicoCompositionException, ParserConfigurationException {
        List options = getOptions(args);

        String monitor = "";
        String composition = "";

        for (Iterator iterator = options.iterator(); iterator.hasNext();) {
            CLOption option = (CLOption) iterator.next();

            switch (option.getId()) {
                case CLOption.TEXT_ARGUMENT:
                    //This occurs when a user supplies an argument that
                    //is not an option
                    System.out.println("Unknown argument: " + option.getArgument());
                    break;

                case HELP_OPT:
                    printUsage();
                    break;

                case VERSION_OPT:
                    printVersion();
                    break;

                case MONITOR_OPT:
                    monitor = option.getArgument();
                    break;

                case COMPOSITION_OPT:
                    composition = option.getArgument();
                    break;

            }
        }

        // Monitor
        NanoContainerMonitor nanoContainerMonitor = createMonitor(args, monitor);

        createComposition(composition, nanoContainerMonitor);
    }

    private static void createComposition(String composition, NanoContainerMonitor nanoContainerMonitor) throws PicoCompositionException, ClassNotFoundException, IOException, ParserConfigurationException {
        if (composition.toLowerCase().endsWith(".js")) {
            NanoContainer nano = new JavaScriptCompositionNanoContainer(new FileReader(composition), nanoContainerMonitor);
            nano.addShutdownHook();
        } else if (composition.toLowerCase().endsWith(".xml")) {
            NanoContainer nano = new XmlCompositionNanoContainer(new FileReader(composition), nanoContainerMonitor);
            nano.addShutdownHook();
        } else {
            System.err.println("NanoContainer: Unknown configuration file suffix, .js or .xml expected");
            System.exit(30);
        }
    }

    private static NanoContainerMonitor createMonitor(String[] args, String monitor) {
        NanoContainerMonitor nanoContainerMonitor = new NullNanoContainerMonitor();
        if (args.length == 2) {
            if (monitor.equals("CommonsLogging")) {
                nanoContainerMonitor = new CommonsLoggingNanoContainerMonitor();
            } else if (monitor.equals("Log4J")) {
                nanoContainerMonitor = new Log4JNanoContainerMonitor();
            } else if (monitor.equals("Console")) {
                nanoContainerMonitor = new ConsoleNanoContainerMonitor();
            }
        }
        return nanoContainerMonitor;
    }

    private static List getOptions(String[] args) {
        if (args.length == 0) {
            System.err.println("NanoContainer: Needs a configuation file as a parameter");
            System.exit(10);
        }

        CLArgsParser parser = new CLArgsParser(args, OPTIONS);

        //Make sure that there was no errors parsing
        //arguments
        if (null != parser.getErrorString()) {
            System.err.println("Error: " + parser.getErrorString());
            System.exit(20);
        }

        // Get a list of parsed options
        final List options = parser.getArguments();
        return options;
    }

    private static void printVersion() {
        System.out.println("1.0");
        System.exit(0);
    }

    private static void printUsage() {
        final String lineSeparator = System.getProperty("line.separator");

        final StringBuffer msg = new StringBuffer();

        msg.append(lineSeparator);
        msg.append("Foo!");

        /*
         * Notice that the next line uses CLUtil.describeOptions to generate the
         * list of descriptions for each option
         */
        msg.append(CLUtil.describeOptions(OPTIONS).toString());

        System.out.println(msg.toString());

        System.exit(0);
    }
}
    

