package org.picocontainer.gui.model;

import junit.framework.TestCase;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.lifecycle.Startable;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.gui.model.ComponentRegistryTreeNode;
import org.picocontainer.gui.model.ComponentTreeNode;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentRegistryTreeNodeTestCase extends TestCase {
    public static class Foo implements Startable {
        public boolean wasStarted;

        public void start() throws Exception {
            wasStarted = true;
        }

    }
    public static class Bar implements Startable {
        public boolean wasStarted;

        public Bar(Foo foo) {}

        public void start() throws Exception {
            wasStarted = true;
        }
    }

    public void testCreateRegistryChild() {
        ComponentRegistryTreeNode parentNode = new ComponentRegistryTreeNode();
        assertEquals(0, parentNode.getChildCount());

        // register a new child registry.
        ComponentRegistryTreeNode regChild = new ComponentRegistryTreeNode();
        parentNode.add(regChild);
        assertEquals(1, parentNode.getChildCount());

        // see that the parent is set.
        assertSame(parentNode, regChild.getParent());
    }

    public void testCreateComponentChild() throws PicoIntrospectionException, PicoRegistrationException {
        ComponentRegistryTreeNode parentNode = new ComponentRegistryTreeNode();

        // register a new child component.
        ComponentTreeNode regChild = new ComponentTreeNode(Foo.class);
        parentNode.add(regChild);
        assertEquals(1, parentNode.getChildCount());

        // see that the parent is set.
        assertSame(parentNode, regChild.getParent());
    }

    public void testSuccessfullyInstantiateComponents() throws Exception, PicoInitializationException {
        ComponentRegistryTreeNode parentNode = new ComponentRegistryTreeNode();

        // register a new child component.
        parentNode.add(new ComponentTreeNode(Foo.class));

        // register a new child registry with a child component.
        ComponentRegistryTreeNode childNode = new ComponentRegistryTreeNode();
        parentNode.add(childNode);

        ComponentTreeNode childBar = new ComponentTreeNode(Bar.class);
        childNode.add(childBar);

        PicoContainer parentPico = new DefaultPicoContainer.WithComponentRegistry(parentNode.createHierarchicalComponentRegistry());
        assertNotNull(parentPico.getComponent(Foo.class));
        assertNull(parentPico.getComponent(Bar.class));

        PicoContainer childPico = new DefaultPicoContainer.WithComponentRegistry(childNode.createHierarchicalComponentRegistry());
        Foo foo = (Foo) childPico.getComponent(Foo.class);
        Bar bar = (Bar) childPico.getComponent(Bar.class);
        assertNotNull(foo);
        assertNotNull(bar);

        Startable startable = (Startable) childPico.getComponentMulticaster();
        startable.start();
        assertTrue(foo.wasStarted);
        assertTrue(bar.wasStarted);
    }

    public void testUnsuccessfullyInstantiateComponents() throws PicoInitializationException, PicoRegistrationException {
        ComponentRegistryTreeNode parentNode = new ComponentRegistryTreeNode();

        // register a new child component.
        parentNode.add(new ComponentTreeNode(Bar.class));

        // register a new child registry with a child component.
        ComponentRegistryTreeNode childNode = new ComponentRegistryTreeNode();
        childNode.add(new ComponentTreeNode(Foo.class));

        PicoContainer parentPico = new DefaultPicoContainer.WithComponentRegistry(parentNode.createHierarchicalComponentRegistry());
        try {
            parentPico.getComponent(Bar.class);
            fail();
        } catch (PicoInitializationException e) {
        }

        PicoContainer childPico = new DefaultPicoContainer.WithComponentRegistry(childNode.createHierarchicalComponentRegistry());
        assertNotNull(childPico.getComponent(Foo.class));
        assertNull(childPico.getComponent(Bar.class));
    }
}
