package org.microcontainer.test;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public interface TestComp {
    String testMe();
    ClassLoader unHideImplClassLoader();
    boolean isRunning();
    void testKernelImplIsInvisibleFromMcaSandbox();
}
