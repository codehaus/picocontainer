package org.picocontainer.gui.model;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;

import java.util.ArrayList;

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

    public void testContainerNodeWithChildrenCreatesContainerWithComponents() {
        ContainerNode containerNode = new ContainerNode();

        ComponentNode stringNode = new ComponentNode(String.class);
        ComponentNode listNode = new ComponentNode(ArrayList.class);

        containerNode.add(stringNode);
        containerNode.add(listNode);

        PicoContainer container = containerNode.createPicoContainer();
        assertEquals("", container.getComponentInstance(String.class));
        assertEquals(new ArrayList(), container.getComponentInstance(ArrayList.class));
    }

    public void testChildContainerNodeCreatesHierarchicalContainer() {
        ContainerNode parentContainerNode = new ContainerNode();
        ContainerNode childContainerNode = new ContainerNode();
        parentContainerNode.add(childContainerNode);

        ComponentNode fooNode = new ComponentNode(Foo.class);
        ComponentNode needsFooNode = new ComponentNode(NeedsFoo.class);

        parentContainerNode.add(fooNode);
        childContainerNode.add(needsFooNode);

        PicoContainer container = childContainerNode.createPicoContainer();
        assertNotNull(container.getComponentInstance(NeedsFoo.class));
    }

    public void testPropertiesAreSetOnComponents() {
        ContainerNode containerNode = new ContainerNode();
        ComponentNode fooNode = new ComponentNode(Foo.class);
        containerNode.add(fooNode);

        PicoContainer container = containerNode.createPicoContainer();
        Foo foo = (Foo) container.getComponentInstance(Foo.class);
        assertEquals("hello", foo.getBar());
    }

}
