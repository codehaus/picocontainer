package org.picocontainer.gems.monitors;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class CommonsLoggingMultiComponentMonitorTestCase extends CommonsLoggingComponentMonitorTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected CommonsLoggingComponentMonitor makeComponentMonitor() {
        return new CommonsLoggingMultiComponentMonitor();
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
