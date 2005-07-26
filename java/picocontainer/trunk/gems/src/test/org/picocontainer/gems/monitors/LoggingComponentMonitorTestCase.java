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

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @version $Revision: 2024 $
 */
public class LoggingComponentMonitorTestCase extends TestCase {
    private ComponentMonitor componentMonitor;
    private static final String NL = System.getProperty("line.separator");
    private Constructor constructor;
    private Method method;
    private File logFile;
    
    protected void setUp() throws Exception {
        constructor = getClass().getConstructor((Class[])null);
        method = getClass().getDeclaredMethod("setUp", (Class[])null);
        componentMonitor = new LoggingComponentMonitor();
        logFile = new File("target/monitor.log");
    }
    
    protected void tearDown() throws Exception {
    }

    public void testShouldTraceInstantiating() throws Exception {
        componentMonitor.instantiating(constructor);        
        assertFileContent(LoggingComponentMonitor.format(LoggingComponentMonitor.INSTANTIATING, new Object[]{constructor}));
    }

    public void testShouldTraceInstantiated() throws Exception {
        componentMonitor.instantiated(constructor, 543);
        assertFileContent(LoggingComponentMonitor.format(LoggingComponentMonitor.INSTANTIATED, new Object[]{constructor, new Long(543)}));
    }

    public void testShouldTraceInstantiationFailed() throws Exception {
        componentMonitor.instantiationFailed(constructor, new RuntimeException("doh"));
        assertFileContent(LoggingComponentMonitor.format(LoggingComponentMonitor.INSTANTIATION_FAILED, new Object[]{constructor, "doh"}));
    }

    public void testShouldTraceInvoking() throws Exception {
        componentMonitor.invoking(method, this);
        assertFileContent(LoggingComponentMonitor.format(LoggingComponentMonitor.INVOKING, new Object[]{method, this}));
    }

    public void testShouldTraceInvoked() throws Exception {
        componentMonitor.invoked(method, this, 543);
        assertFileContent(LoggingComponentMonitor.format(LoggingComponentMonitor.INVOKED, new Object[]{method, this, new Long(543)}));
    }

    public void testShouldTraceInvocatiationFailed() throws Exception {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertFileContent(LoggingComponentMonitor.format(LoggingComponentMonitor.INVOCATION_FAILED, new Object[]{method, this, "doh"}));
    }
    
    private void assertFileContent(String line) throws IOException{        
        List lines = toLines( new FileReader( logFile ) );
        assertTrue(lines.toString().indexOf(line) > 0);
    }
    
    public List toLines(Reader resource) throws IOException {
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
