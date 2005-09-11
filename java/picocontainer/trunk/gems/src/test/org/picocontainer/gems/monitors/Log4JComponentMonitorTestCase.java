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
public class Log4JComponentMonitorTestCase extends TestCase {
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

    protected Constructor getConstructor() throws NoSuchMethodException {
        return getClass().getConstructor((Class[])null);
    }

    protected Method getMethod() throws NoSuchMethodException {
        return getClass().getDeclaredMethod("setUp", (Class[])null);
    }

    protected Log4JComponentMonitor makeComponentMonitor() {
        return new Log4JComponentMonitor();
    }

    protected void tearDown() throws Exception {
    }

    public void testShouldTraceInstantiating() throws Exception {
        componentMonitor.instantiating(constructor);        
        assertFileContent(getCategory() + Log4JComponentMonitor.format(Log4JComponentMonitor.INSTANTIATING, new Object[]{constructor}));
    }

    protected String getCategory() {
        return "[" + Log4JComponentMonitor.class.getName() + "] ";
    }

    public void testShouldTraceInstantiated() throws Exception {
        componentMonitor.instantiated(constructor, 543);
        assertFileContent(getCategory() + Log4JComponentMonitor.format(Log4JComponentMonitor.INSTANTIATED, new Object[]{constructor, new Long(543)}));
    }

    public void testShouldTraceInstantiationFailed() throws Exception {
        componentMonitor.instantiationFailed(constructor, new RuntimeException("doh"));
        assertFileContent(getCategory() + Log4JComponentMonitor.format(Log4JComponentMonitor.INSTANTIATION_FAILED, new Object[]{constructor, "doh"}));
    }

    public void testShouldTraceInvoking() throws Exception {
        componentMonitor.invoking(method, this);
        assertFileContent(getCategory() + Log4JComponentMonitor.format(Log4JComponentMonitor.INVOKING, new Object[]{method, this}));
    }

    public void testShouldTraceInvoked() throws Exception {
        componentMonitor.invoked(method, this, 543);
        assertFileContent(getCategory() + Log4JComponentMonitor.format(Log4JComponentMonitor.INVOKED, new Object[]{method, this, new Long(543)}));
    }

    public void testShouldTraceInvocatiationFailed() throws Exception {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertFileContent(getCategory() + Log4JComponentMonitor.format(Log4JComponentMonitor.INVOCATION_FAILED, new Object[]{method, this, "doh"}));
    }
    
    private void assertFileContent(String line) throws IOException{        
        List lines = toLines( new FileReader( logFile ) );
        assertTrue("Line '" + line + "' not found", lines.toString().indexOf(line) > 0);
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
