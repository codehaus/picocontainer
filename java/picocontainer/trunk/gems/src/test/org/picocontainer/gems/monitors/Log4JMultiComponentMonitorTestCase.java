package org.picocontainer.gems.monitors;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class Log4JMultiComponentMonitorTestCase extends Log4JComponentMonitorTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected Log4JComponentMonitor makeComponentMonitor() {
        return new Log4JMultiComponentMonitor();
    }

    protected Method getMethod() throws NoSuchMethodException {
        return String.class.getMethod("toString",new Class[0]);
    }

    protected Constructor getConstructor() throws NoSuchMethodException {
        return String.class.getConstructors()[0];
    }

    protected String getCategory() {
        return "[" + String.class.getName() + "] ";
    }


}
