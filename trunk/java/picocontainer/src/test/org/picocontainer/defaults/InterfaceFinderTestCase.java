package org.picocontainer.defaults;

import junit.framework.TestCase;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTree;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServicesListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class InterfaceFinderTestCase extends TestCase {
    public void testMostCommonSuperclassForClassesWithACommonBaseClass() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(Writer.class, i.getClass(new Object[]{new StringWriter(), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, i.getClass(new Object[]{new OutputStreamWriter(System.out), new StringWriter()}));
    }

    public void testMostCommonSuperclassForClassesAreInSameHierarchy() throws IOException {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(OutputStreamWriter.class, i.getClass(new Object[]{new FileWriter("~"), new OutputStreamWriter(System.out)}));
        assertEquals(OutputStreamWriter.class, i.getClass(new Object[]{new OutputStreamWriter(System.out), new FileWriter("~")}));
    }

    public void testMostCommonSuperclassForClassesInSameOrDifferentHierarchy() throws IOException {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(Writer.class, i.getClass(new Object[]{new FileWriter("~"), new StringWriter(), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, i.getClass(new Object[]{new FileWriter("~"), new OutputStreamWriter(System.out), new StringWriter()}));
        assertEquals(Writer.class, i.getClass(new Object[]{new StringWriter(), new FileWriter("~"), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, i.getClass(new Object[]{new OutputStreamWriter(System.out), new FileWriter("~"), new StringWriter()}));
        assertEquals(Writer.class, i.getClass(new Object[]{new StringWriter(), new OutputStreamWriter(System.out), new FileWriter("~")}));
        assertEquals(Writer.class, i.getClass(new Object[]{new OutputStreamWriter(System.out), new StringWriter(), new FileWriter("~")}));
    }

    public void testMostCommonSuperclassForUnmatchingObjects() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(Object.class, i.getClass(new Object[]{new Integer(1), new OutputStreamWriter(System.out)}));
        assertEquals(Object.class, i.getClass(new Object[]{new OutputStreamWriter(System.out), new Integer(1)}));
    }

    public void testMostCommonSuperclassForEmptyArray() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(Void.class, i.getClass(new Object[]{}));
    }

    public void testMostCommonSuperclassForNullElements() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(Void.class, i.getClass(new Object[]{ null, null }));
    }

    public void testMostCommonSuperclassForJComponents() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(JComponent.class, i.getClass(new Object[]{new JButton(), new JTree()}));
    }

    public void testAllInterfacesOfListShouldBeFound() {
        Class[] interfaces = new InterfaceFinder().getAllInterfaces(BeanContextServices.class);
        List interfaceList = Arrays.asList(interfaces);
        assertTrue(interfaceList.contains(BeanContextServices.class));
        assertTrue(interfaceList.contains(BeanContext.class));
        assertTrue(interfaceList.contains(Collection.class));
        assertTrue(interfaceList.contains(BeanContextServicesListener.class));
        assertTrue(interfaceList.contains(EventListener.class));
    }
}