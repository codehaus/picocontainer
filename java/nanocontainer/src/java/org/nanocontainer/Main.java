/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.integrationkit.ContainerBuilder;
import org.picoextras.script.jython.JythonContainerAssembler;
import org.picoextras.script.rhino.JavascriptContainerAssembler;
import org.picoextras.script.xml.XMLContainerBuilder;
import org.realityforge.cli.CLArgsParser;
import org.realityforge.cli.CLOption;
import org.realityforge.cli.CLOptionDescriptor;
import org.realityforge.cli.CLUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main {

    private static final int HELP_OPT = 'h';
    private static final int VERSION_OPT = 'v';
    private static final int MONITOR_OPT = 'm';
    private static final int COMPOSITION_OPT = 'c';

    private static final Map extensionToAssemblerMap = new HashMap();

    static {
        extensionToAssemblerMap.put(".js", JavascriptContainerAssembler.class);
        extensionToAssemblerMap.put(".xml", XMLContainerBuilder.class);
        extensionToAssemblerMap.put(".py", JythonContainerAssembler.class);
    }

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

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
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

        buildAndStartContainer(composition, nanoContainerMonitor);
    }

    /*
    Now that the breadth/depth-first traversal of "child" containers, we should consider adding support
    for "monitors" at a higher level of abstraction.

    I think that ideally this should be done on the multicaster level, so that we can get monitor
    events whenever *any* method is called via the multicaster. That way we could easily intercept lifecycle
    methods on individual components, not only on the container level.

    The most elegant way to deal with this is perhaps via Nanning, or we could add support for it
    directly in the MulticastInvoker class. (It could be constructed with an additional argument
    called InvocationInterceptor. MulticastInvoker would then call methods on this object in addition
    to the subject. The InvocationInterceptor would serve the same purpose as this NanoContainerMonitor,
    but at a much higher level of abstraction. It would be more reusable, since it would enable monitoring
    outside the scope of nano. It could be useful in e.g. WebWork or other environments.

    I think it should be up to the ContainerBuilder instances (in integrationkit) to decide what kind of
    monitor/InvocationInterceptor to use.

    AH
    */
    private static void buildAndStartContainer(String composition, NanoContainerMonitor nanoContainerMonitor) throws IllegalAccessException, InstantiationException {
        final String extension = composition.substring(composition.indexOf("."));
        Class containerAssemblerClass = (Class) extensionToAssemblerMap.get(extension);

        // This won't work. They all need different ctor parameters. We should use pico itself to assemble this!!
        ContainerAssembler ca = (ContainerAssembler) containerAssemblerClass.newInstance();
        final ContainerBuilder cb = new org.picoextras.integrationkit.DefaultLifecycleContainerBuilder(ca);

        final ObjectReference containerRef = new SimpleReference();

        // build and start the container
        cb.buildContainer(containerRef, null, null);

        // add a shutdown hook that will tell the builder to kill it.
        Runnable shutdownHook = new Runnable() {
            public void run() {
                cb.killContainer(containerRef);
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));

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
    

