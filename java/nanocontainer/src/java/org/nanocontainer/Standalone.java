/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.nanocontainer.NanoContainer;
import org.realityforge.cli.CLArgsParser;
import org.realityforge.cli.CLOption;
import org.realityforge.cli.CLOptionDescriptor;
import org.realityforge.cli.CLUtil;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Standalone {

    private static final int HELP_OPT = 'h';
    private static final int VERSION_OPT = 'v';
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
        new CLOptionDescriptor("composition",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                COMPOSITION_OPT,
                "specify the assembly file")

    };

    public static void main(String[] args) {
        List options = getOptions(args);

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

                case COMPOSITION_OPT:
                    composition = option.getArgument();
                    break;

            }
        }

        try {
            buildAndStartContainer(composition);
        } catch (Exception e) {
            System.out.println("NanoContainer has failed to start application. Cause : " + e.getMessage());
            e.printStackTrace();
        }
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

    I think it should be up to the ContainerComposer instances (in integrationkit) to decide what kind of
    monitor/InvocationInterceptor to use.

    AH
    */
    private static void buildAndStartContainer(String compositionFileName) throws Exception {

        final NanoContainer nanoContainer = new NanoContainer(new File(compositionFileName));

        // add a shutdown hook that will tell the builder to kill it.
        Runnable shutdownHook = new Runnable() {
            public void run() {
                System.out.println("Shutting Down NanoContainer");
                try {
                    nanoContainer.killContainer();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Exiting VM");
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));

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


