package org.picocontainer.gui.tree;

import junit.framework.TestCase;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.lifecycle.Startable;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;

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
        ComponentRegistry parentRegistry = new DefaultComponentRegistry();
        ComponentRegistryTreeNode parentNode = new ComponentRegistryTreeNode();
        assertEquals(0, parentNode.getChildCount());

        // register a new child registry.
        ComponentRegistry childRegistry = new DefaultComponentRegistry();
        ComponentRegistryTreeNode regChild = new ComponentRegistryTreeNode();
        parentNode.add(regChild);
        assertEquals(1, parentNode.getChildCount());

        // see that the parent is set.
        assertSame(parentNode, regChild.getParent());
    }

    public void testCreateComponentChild() throws PicoIntrospectionException, PicoRegistrationException {
        ComponentRegistry parentRegistry = new DefaultComponentRegistry();
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
//        parentPico.instantiateComponents();
        assertNotNull(parentPico.getComponent(Foo.class));
        assertNull(parentPico.getComponent(Bar.class));

        PicoContainer childPico = new DefaultPicoContainer.WithComponentRegistry(childNode.createHierarchicalComponentRegistry());
//        childPico.instantiateComponents();
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
        ComponentRegistry parentRegistry = new DefaultComponentRegistry();
        ComponentRegistryTreeNode parentNode = new ComponentRegistryTreeNode();

        // register a new child component.
        parentNode.add(new ComponentTreeNode(Bar.class));

        // register a new child registry with a child component.
        ComponentRegistry childRegistry = new DefaultComponentRegistry();
        ComponentRegistryTreeNode childNode = new ComponentRegistryTreeNode();
        childNode.add(new ComponentTreeNode(Foo.class));

        PicoContainer parentPico = new DefaultPicoContainer.WithComponentRegistry(parentNode.createHierarchicalComponentRegistry());
        try {
            parentPico.getComponent(Bar.class);
            fail();
        } catch (PicoInitializationException e) {
        }

        PicoContainer childPico = new DefaultPicoContainer.WithComponentRegistry(childNode.createHierarchicalComponentRegistry());
//        childPico.instantiateComponents();
        assertNotNull(childPico.getComponent(Foo.class));
        assertNull(childPico.getComponent(Bar.class));
    }
}
