/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.gems.monitors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.monitors.AbstractComponentMonitor;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @version $Revision: 2024 $
 */
public abstract class AbstractComponentMonitorTestCase extends TestCase {
    private ComponentMonitor componentMonitor;
    private Constructor constructor;
    private Method method;
    private File logFile;
    
    protected void setUp() throws Exception {
        constructor = getConstructor();
        method = getMethod();
        componentMonitor = makeComponentMonitor();
        logFile = new File("target/monitor.log");
    }

    protected abstract ComponentMonitor makeComponentMonitor();
    
    protected abstract Constructor getConstructor() throws NoSuchMethodException;

    protected abstract Method getMethod() throws NoSuchMethodException;
    
    protected abstract String getLogPrefix();

    protected void tearDown() throws Exception {
    }

    public void testShouldTraceInstantiating() throws Exception {
        componentMonitor.instantiating(constructor);        
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INSTANTIATING, new Object[]{constructor}));
    }

    public void testShouldTraceInstantiated() throws Exception {
        componentMonitor.instantiated(constructor, 543);
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INSTANTIATED, new Object[]{constructor, new Long(543)}));
    }

    public void testShouldTraceInstantiationFailed() throws Exception {
        componentMonitor.instantiationFailed(constructor, new RuntimeException("doh"));
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INSTANTIATION_FAILED, new Object[]{constructor, "doh"}));
    }

    public void testShouldTraceInvoking() throws Exception {
        componentMonitor.invoking(method, this);
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INVOKING, new Object[]{method, this}));
    }

    public void testShouldTraceInvoked() throws Exception {
        componentMonitor.invoked(method, this, 543);
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INVOKED, new Object[]{method, this, new Long(543)}));
    }

    public void testShouldTraceInvocatiationFailed() throws Exception {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INVOCATION_FAILED, new Object[]{method, this, "doh"}));
    }

    protected void assertFileContent(String line) throws IOException{        
        List lines = toLines( new FileReader( logFile ) );
        assertTrue("Line '" + line + "' not found", lines.toString().indexOf(line) > 0);
    }
    
    protected List toLines(Reader resource) throws IOException {
        BufferedReader br = new BufferedReader(resource);
        List lines = new ArrayList();
        String line = br.readLine();
        while ( line != null) {
            lines.add(line);
            line = br.readLine();
        }
        return lines;
    } 

}
