package org.picocontainer.defaults;

import junit.framework.TestCase;

import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServicesListener;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.AbstractList;
import java.util.Collection;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class ClassHierarchyIntrospectorTestCase extends TestCase {
    public void testMostCommonSuperclassForClassesWithACommonBaseClass() {
        assertEquals(Writer.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new StringWriter(), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new OutputStreamWriter(System.out), new StringWriter()}));
    }

    public void testMostCommonSuperclassForClassesAreInSameHierarchy() throws IOException {
        assertEquals(OutputStreamWriter.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new FileWriter(FileDescriptor.out), new OutputStreamWriter(System.out)}));
        assertEquals(OutputStreamWriter.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new OutputStreamWriter(System.out), new FileWriter(FileDescriptor.out)}));
    }

    public void testMostCommonSuperclassForClassesInSameOrDifferentHierarchy() throws IOException {
        assertEquals(Writer.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new FileWriter(FileDescriptor.out), new StringWriter(), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new FileWriter(FileDescriptor.out), new OutputStreamWriter(System.out), new StringWriter()}));
        assertEquals(Writer.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new StringWriter(), new FileWriter(FileDescriptor.out), new OutputStreamWriter(System.out)}));
        assertEquals(Writer.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new OutputStreamWriter(System.out), new FileWriter(FileDescriptor.out), new StringWriter()}));
        assertEquals(Writer.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new StringWriter(), new OutputStreamWriter(System.out), new FileWriter(FileDescriptor.out)}));
        assertEquals(Writer.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new OutputStreamWriter(System.out), new StringWriter(), new FileWriter(FileDescriptor.out)}));
    }

    public void testMostCommonSuperclassForUnmatchingObjects() {
        assertEquals(Object.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new Integer(1), new OutputStreamWriter(System.out)}));
        assertEquals(Object.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new OutputStreamWriter(System.out), new Integer(1)}));
    }

    public void testMostCommonSuperclassForEmptyArray() {
        // Makes more sense to return null perhaps? ==> Refactor Multicaster 
        assertEquals(Void.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{}));
    }

    public void testMostCommonSuperclassForNullElements() {
        // Makes more sense to return null perhaps? ==> Refactor Multicaster
        assertEquals(Void.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{ null, null }));
    }

    public void testMostCommonSuperclassForCollections() {
        assertEquals(AbstractList.class, ClassHierarchyIntrospector.getMostCommonSuperclass(new Object[]{new LinkedList(), new Vector()}));
    }

    public void testAllInterfacesOfListShouldBeFound() {
        Set interfaces = ClassHierarchyIntrospector.getAllInterfaces(BeanContextServices.class);
        assertTrue(interfaces.contains(BeanContextServices.class));
        assertTrue(interfaces.contains(BeanContext.class));
        assertTrue(interfaces.contains(Collection.class));
        assertTrue(interfaces.contains(BeanContextServicesListener.class));
        assertTrue(interfaces.contains(EventListener.class));
    }
}