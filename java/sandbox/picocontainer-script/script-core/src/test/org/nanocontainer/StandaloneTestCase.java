/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;


/**
 * @author Mauro Talevi
 */
public class StandaloneTestCase {

    @Test public void testShouldBeAbleToInvokeMainMethodWithScriptFromFile() throws IOException {
        File absoluteScriptPath = getAbsoluteScriptPath();
        Standalone.main(new String[] {
            "-c",
            absoluteScriptPath.getAbsolutePath(),
            "-n"
        });
    }

    @Test public void testShouldBeAbleToInvokeMainMethodWithScriptFromClasspathWithXmlIncludes() throws IOException {
        Standalone.main(new String[] {
            "-r",
            "/org/nanocontainer/nanocontainer-with-include.xml", 
            "-n"
        });
    }

    private File getAbsoluteScriptPath() {
        String className = getClass().getName();
        String relativeClassPath = "/" + className.replace('.', '/') + ".class";
        URL classURL = Standalone.class.getResource(relativeClassPath);
        String absoluteClassPath = classURL.getFile();
        File absoluteDirPath = new File(absoluteClassPath).getParentFile();
        return new File(absoluteDirPath, "nanocontainer.xml");
    }

    @Test public void testCommandLineWithHelp() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-h"}, Standalone.createOptions());
        assertTrue(cl.hasOption('h'));
        assertFalse(cl.hasOption('v'));
        assertNull(cl.getOptionValue('c'));
        assertFalse(cl.hasOption('q'));
        assertFalse(cl.hasOption('n'));
    }

    @Test public void testCommandLineWithVersion() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-v"}, Standalone.createOptions());
        assertFalse(cl.hasOption('h'));
        assertTrue(cl.hasOption('v'));
        assertNull(cl.getOptionValue('c'));
        assertFalse(cl.hasOption('q'));
        assertFalse(cl.hasOption('n'));
    }

    @Test public void testCommandLineWithCompostion() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-cpath"}, Standalone.createOptions());
        assertFalse(cl.hasOption('h'));
        assertFalse(cl.hasOption('v'));
        assertEquals("path", cl.getOptionValue('c'));
        assertFalse(cl.hasOption('q'));
        assertFalse(cl.hasOption('n'));
    }

    @Test public void testCommandLineWithCompositionAndQuiet() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-cpath", "-q"}, Standalone.createOptions());
        assertFalse(cl.hasOption('h'));
        assertFalse(cl.hasOption('v'));
        assertEquals("path", cl.getOptionValue('c'));
        assertTrue(cl.hasOption('q'));
        assertFalse(cl.hasOption('n'));
    }

    @Test public void testCommandLineWithCompositionAndQuietAndNowait() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-cpath", "-q", "-n"}, Standalone.createOptions());
        assertFalse(cl.hasOption('h'));
        assertFalse(cl.hasOption('v'));
        assertEquals("path", cl.getOptionValue('c'));
        assertTrue(cl.hasOption('q'));
        assertTrue(cl.hasOption('n'));
    }

}
