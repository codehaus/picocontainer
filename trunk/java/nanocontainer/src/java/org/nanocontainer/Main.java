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
import org.realityforge.cli.CLArgsParser;
import org.realityforge.cli.CLOptionDescriptor;
import org.realityforge.cli.CLOption;
import org.realityforge.cli.CLUtil;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {

    private static final int HELP_OPT = 'h';
    private static final int VERSION_OPT = 'v';
    private static final int MONITOR_OPT = 'm';
    private static final int ASSEMBLY_OPT = 'a';

    private static final CLOptionDescriptor[] OPTIONS = new CLOptionDescriptor[]
    {
        new CLOptionDescriptor( "help",
                                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                HELP_OPT,
                                "print this message and exit" ),
        new CLOptionDescriptor( "version",
                                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                VERSION_OPT,
                                "print the version information and exit" ),
        new CLOptionDescriptor( "monitor",
                                CLOptionDescriptor.ARGUMENT_REQUIRED,
                                MONITOR_OPT,
                                "specify the monitor implemenatation" ),
        new CLOptionDescriptor( "assembly",
                                CLOptionDescriptor.ARGUMENT_REQUIRED,
                                ASSEMBLY_OPT,
                                "specify the assembly file" )

    };

    public static void main(String[] args) throws IOException, ClassNotFoundException, PicoConfigurationException, ParserConfigurationException {
        if (args.length == 0) {
            System.err.println("NanoContainer: Needs a configuation file as a parameter");
            System.exit(10);
        }

        CLArgsParser parser = new CLArgsParser(args, OPTIONS);

        //Make sure that there was no errors parsing
        //arguments
        if( null != parser.getErrorString() )
        {
            System.err.println( "Error: " + parser.getErrorString() );
            return;
        }

        // Get a list of parsed options
        final List options = parser.getArguments();
        final int size = options.size();

        String monitor = "";
        String nanoContainerConfig = "";

        for( int i = 0; i < size; i++ )
        {
            final CLOption option = (CLOption)options.get( i );

            switch( option.getId() )
            {
                case CLOption.TEXT_ARGUMENT:
                    //This occurs when a user supplies an argument that
                    //is not an option
                    System.out.println( "Unknown arg: " + option.getArgument() );
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

                case ASSEMBLY_OPT:
                    nanoContainerConfig = option.getArgument();
                    break;

            }
        }

        // Monitor
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

    private static void printVersion()
    {
        System.out.println( "1.0" );
        System.exit( 0 );
    }

    private static void printUsage()
    {
        final String lineSeparator = System.getProperty( "line.separator" );

        final StringBuffer msg = new StringBuffer();

        msg.append( lineSeparator );
        msg.append( "Foo!" );

        /*
         * Notice that the next line uses CLUtil.describeOptions to generate the
         * list of descriptions for each option
         */
        msg.append( CLUtil.describeOptions( OPTIONS ).toString() );

        System.out.println( msg.toString() );

        System.exit( 0 );
    }
}
    

