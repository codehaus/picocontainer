/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

public class Standalone {

    private static final char HELP_OPT = 'h';
    private static final char VERSION_OPT = 'v';
    private static final char COMPOSITION_OPT = 'c';
    private static final char QUIET_OPT = 'q';
    private static final char NOWAIT_OPT = 'n';
    private static final Options OPTIONS = createOptions();

    private static final Options createOptions(){
        Options options = new Options();
        options.addOption(String.valueOf(HELP_OPT), "help", false,
                "print this message and exit");
        options.addOption(String.valueOf(VERSION_OPT), "version", false,
                "print the version information and exit");
        options.addOption(String.valueOf(COMPOSITION_OPT), "composition", true,
                "specify the composition file");
        options.addOption(String.valueOf(QUIET_OPT), "quiet", false,
                "forces NanoContainer to be quiet");
        options.addOption(String.valueOf(NOWAIT_OPT), "nowait", false,
        		"forces NanoContainer to exit after start");
        return options;
    }
    
    public static void main(String[] args) {
        CommandLine cl = null;
        try {
            cl = getCommandLine(args );
        } catch ( ParseException e ) {
            System.out.println( "Error in parsing arguments: ");
            e.printStackTrace();
            System.exit( -1 );
        }

        if ( cl.hasOption(HELP_OPT) ){
            printUsage();
            System.exit(0);
        }
        if ( cl.hasOption(VERSION_OPT) ){
            printVersion();
            System.exit(0);
        }
        
        String composition = cl.getOptionValue(COMPOSITION_OPT);
        if ( composition == null ) {
            printUsage();
            System.exit(0);
        }
        boolean quiet = cl.hasOption(QUIET_OPT);
        boolean nowait = cl.hasOption(NOWAIT_OPT);
        try {
            buildAndStartContainer(composition, quiet, nowait);
        } catch (RuntimeException e) {
            System.err.println("NanoContainer has failed to start application. Cause : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("NanoContainer has failed to start application, for IO reasons. Exception message : " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("NanoContainer has failed to start application. A Class was not found. Exception message : " + e.getMessage());
            e.printStackTrace();
        }
        if (!quiet) {
            System.out.println("Exiting NanoContainer's standalone main method.");
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
    private static void buildAndStartContainer(String compositionFileName, final boolean quiet, boolean nowait) throws IOException, ClassNotFoundException {

        final NanoContainer nanoContainer = new NanoContainer(new File(compositionFileName));

        final ObjectReference containerRef = new SimpleReference();
        nanoContainer.getContainerBuilder().buildContainer(containerRef, null, null, true);

        if (nowait == false) {
            setShutdownHook(quiet, nanoContainer, containerRef);
        } else {
//            shuttingDown(quiet, nanoContainer, containerRef);
        }
    }

    private static void setShutdownHook(final boolean quiet, final NanoContainer nanoContainer, final ObjectReference containerRef) {
        // add a shutdown hook that will tell the builder to kill it.
        Runnable shutdownHook = new Runnable() {
            public void run() {
                shuttingDown(quiet, nanoContainer, containerRef);
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
    }

    private static void shuttingDown(final boolean quiet, final NanoContainer nanoContainer, final ObjectReference containerRef) {
        try {
            nanoContainer.getContainerBuilder().killContainer(containerRef);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (!quiet) {
                System.out.println("NanoContainer: Exiting Virtual Machine");
            }
        }
    }


    static CommandLine getCommandLine(String[] args) throws ParseException {
        if (args.length == 0) {
            throw new ParseException("No arguments specified");
        }
        CommandLineParser parser = new PosixParser();
        return parser.parse( OPTIONS, args );
    }

    private static void printUsage() {
        final String lineSeparator = System.getProperty("line.separator");

        final StringBuffer usage = new StringBuffer();
        usage.append(lineSeparator);
        usage.append("NanoContainer: Standalone -c <composition> [-q|-n|-h|-v]");
        usage.append(OPTIONS.getOptions());
        System.out.println(usage.toString());
    }

    private static void printVersion() {
        System.out.println("1.0");
    }



}


