package org.picocontainer.defaults;

import com.sun.corba.se.internal.ior.ObjectIds;
import com.sun.corba.se.internal.ior.TaggedProfileTemplate;
import com.sun.corba.se.internal.ior.Identifiable;
import junit.framework.TestCase;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InterfaceFinderTestCase extends TestCase {
    public void testMostCommonSuperclassForClassesWithACommonBaseClass() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(AbstractList.class, i.getClass(new Object[]{new ArrayList(), new LinkedList()}));
        assertEquals(AbstractList.class, i.getClass(new Object[]{new LinkedList(), new ArrayList()}));
    }

    public void testMostCommonSuperclassForClassesAreInSameHierarchy() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(LinkedList.class, i.getClass(new Object[]{new ObjectIds(), new LinkedList()}));
        assertEquals(LinkedList.class, i.getClass(new Object[]{new LinkedList(), new ObjectIds()}));
    }

    public void testMostCommonSuperclassForClassesInSameOrDifferentHierarchy() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(AbstractList.class, i.getClass(new Object[]{new ObjectIds(), new ArrayList(), new LinkedList()}));
        assertEquals(AbstractList.class, i.getClass(new Object[]{new ObjectIds(), new LinkedList(), new ArrayList()}));
        assertEquals(AbstractList.class, i.getClass(new Object[]{new ArrayList(), new ObjectIds(), new LinkedList()}));
        assertEquals(AbstractList.class, i.getClass(new Object[]{new LinkedList(), new ObjectIds(), new ArrayList()}));
        assertEquals(AbstractList.class, i.getClass(new Object[]{new ArrayList(), new LinkedList(), new ObjectIds()}));
        assertEquals(AbstractList.class, i.getClass(new Object[]{new LinkedList(), new ArrayList(), new ObjectIds()}));
    }

    public void testMostCommonSuperclassForUnmatchingObjects() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(Object.class, i.getClass(new Object[]{new Integer(1), new LinkedList()}));
        assertEquals(Object.class, i.getClass(new Object[]{new LinkedList(), new Integer(1)}));
    }

    public void testMostCommonSuperclassEmptyArray() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(Void.class, i.getClass(new Object[]{}));
    }

    public void testAllInterfacesOfListShouldBeFound() {
        Class[] interfaces = new InterfaceFinder().getAllInterfaces(TaggedProfileTemplate.class);
        List interfaceList = Arrays.asList(interfaces);
        assertTrue(interfaceList.contains(TaggedProfileTemplate.class));
        assertTrue(interfaceList.contains(Identifiable.class));
        assertTrue(interfaceList.contains(List.class));
    }

}