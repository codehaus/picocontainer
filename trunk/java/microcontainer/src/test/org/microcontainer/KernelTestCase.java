package org.megacontainer;

public class KernelTestCase {

    public void testDeploymentOfMarFile() {
        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // assertNotNull(o);
        // Method m = o.getClass().getMethod("testMe", new Object[0]);
        // assertEqual("hello", m.invoke(o, new Object[0]);
    }

    public void testDeferredDeplymentOfMarFile() {
        // kernel.deferredDeploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // assertNull(o);
        // kernel.start("test/org.megacontainer.test.TestComp");
        // o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // assertNotNull(o);
        // Method m = o.getClass().getMethod("testMe", new Object[0]);
        // assertEqual("hello", m.invoke(o, new Object[0]);
    }

    public void testDeplyedMarsComponentsAreInDiffClassloader() {
        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // assertNotNull(o);
        // assertEqual(kernel.getClass().getClassLoader(), o.getClass().getClassLoader().getParent().getParent());
    }

    public void testAPIisPromoted() {
        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // Class clazz = o.getClass().getClassLoader().getParent().loadClass("org.megacontainer.test.TestAPI");
        // assertNotNull(clazz);
    }

}
