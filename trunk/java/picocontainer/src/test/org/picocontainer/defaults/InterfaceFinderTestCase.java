package org.picocontainer.defaults;

import com.sun.corba.se.internal.ior.ObjectIds;
import com.sun.corba.se.internal.ior.TaggedProfileTemplate;
import com.sun.corba.se.internal.ior.Identifiable;
import junit.framework.TestCase;

import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InterfaceFinderTestCase extends TestCase {
    public void testMostCommonSuperclassForArrayListAndSequentialListIsAbstractList() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(AbstractList.class, i.getClass(new Object[]{new ArrayList(), new LinkedList()}));
    }

    public void testMostCommonSuperclassForObjectIdsAndSequentialListIsAbstractSequentialList() {
        InterfaceFinder i = new InterfaceFinder();
        assertEquals(AbstractSequentialList.class, i.getClass(new Object[]{new ObjectIds(), new LinkedList()}));
    }

    public void testAllInterfacesOfListShouldBeFound() {
        Class[] interfaces = new InterfaceFinder().getAllInterfaces(TaggedProfileTemplate.class);
        List interfaceList = Arrays.asList(interfaces);
        assertTrue(interfaceList.contains(TaggedProfileTemplate.class));
        assertTrue(interfaceList.contains(Identifiable.class));
        assertTrue(interfaceList.contains(List.class));
    }

}