package org.microcontainer.test.hopefullyhidden;

import org.microcontainer.test.TestComp;
import org.picocontainer.Startable;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class TestCompImpl implements TestComp, Startable {

    private boolean isRunning;
    public void start() {
        isRunning = true;
    }

    public void stop() {
        isRunning = false;
    }

    public String testMe() {
        return "hello";
    }

    public ClassLoader unHideImplClassLoader() {
        return this.getClass().getClassLoader();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void testKernelImplIsInvisibleFromMcaSandbox() {
        //TODO
    }
}
