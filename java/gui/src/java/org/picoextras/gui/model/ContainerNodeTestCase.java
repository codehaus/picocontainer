package org.picoextras.gui.model;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;

import java.util.ArrayList;
import java.beans.IntrospectionException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ContainerNodeTestCase extends TestCase {

    public void testEmptyContainerNodeCreatesEmptyContainer() {
        ContainerNode containerNode = new ContainerNode();
        PicoContainer container = containerNode.createPicoContainer();
        assertEquals(0, container.getComponentKeys().size());
    }

    public void testContainerNodeWithChildrenCreatesContainerWithComponents() throws IntrospectionException {
        ContainerNode containerNode = new ContainerNode();

        ComponentNode stringNode = new ComponentNode(new BeanPropertyModel(String.class));
        ComponentNode listNode = new ComponentNode(new BeanPropertyModel(ArrayList.class));

        containerNode.add(stringNode);
        containerNode.add(listNode);

        PicoContainer container = containerNode.createPicoContainer();
        assertEquals("", container.getComponentInstance(String.class));
        assertEquals(new ArrayList(), container.getComponentInstance(ArrayList.class));
    }

    public void testChildContainerNodeCreatesHierarchicalContainer() throws IntrospectionException {
        ContainerNode parentContainerNode = new ContainerNode();
        ContainerNode childContainerNode = new ContainerNode();
        parentContainerNode.add(childContainerNode);

        ComponentNode fooNode = new ComponentNode(new BeanPropertyModel(Foo.class));
        ComponentNode needsFooNode = new ComponentNode(new BeanPropertyModel(NeedsFoo.class));

        parentContainerNode.add(fooNode);
        childContainerNode.add(needsFooNode);

        PicoContainer container = childContainerNode.createPicoContainer();
        assertNotNull(container.getComponentInstance(NeedsFoo.class));
    }

    public void testPropertiesAreSetOnComponents() throws IntrospectionException {
//        ContainerNode containerNode = new ContainerNode();
//        ComponentNode fooNode = new ComponentNode(new BeanPropertyModel(Foo.class));
//        containerNode.add(fooNode);
//
//        PicoContainer container = containerNode.createPicoContainer();
//        Foo foo = (Foo) container.getComponentInstance(Foo.class);
//        assertEquals("hello", foo.getBar());
    }

}
