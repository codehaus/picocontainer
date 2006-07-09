package org.picocontainer.gems.monitors;

import junit.framework.TestCase;
import org.picocontainer.testmodel.DependsOnList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class DotGraphComponentMonitorTestCase extends TestCase {


    public void testSimpleClassDependencyGraph() throws NoSuchMethodException {

        DotGraphComponentMonitor monitor = new DotGraphComponentMonitor();

        Vector vec = new Vector();
        List list = new ArrayList();
        DependsOnList dol = new DependsOnList(list);
        DependsOnDependsOnListAndVector dodolav = new DependsOnDependsOnListAndVector(vec, dol);

        monitor.instantiated(Vector.class.getConstructor(new Class[0]), vec, new Object[]{}, 8);
        monitor.instantiated(ArrayList.class.getConstructor(new Class[]{Collection.class}), list, new Object[]{vec}, 12);
        monitor.instantiated(DependsOnList.class.getConstructors()[0], dol, new Object[]{list}, 10);
        monitor.instantiated(DependsOnDependsOnListAndVector.class.getConstructors()[0], dodolav, new Object[]{vec, dol}, 16);

        String graph = monitor.getClassDependencyGraph();

        String expected = "" +
                "  java.util.ArrayList -> java.util.Vector;\n" +
                "  org.picocontainer.testmodel.DependsOnList -> java.util.ArrayList;\n" +
                "  org.picocontainer.gems.monitors.DotGraphComponentMonitorTestCase$DependsOnDependsOnListAndVector -> org.picocontainer.testmodel.DependsOnList;\n" +
                "  org.picocontainer.gems.monitors.DotGraphComponentMonitorTestCase$DependsOnDependsOnListAndVector -> java.util.Vector;\n" +
                "";

        assertEquals(expected, graph);

    }

    public static class DependsOnDependsOnListAndVector {
        public DependsOnDependsOnListAndVector(Vector vec, DependsOnList dol) {
        }
    }


}
