package org.picocontainer.alternatives;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ImplementationHidingComponentAdapterTestCase extends TestCase {

    public void testMultipleInterfacesCanBeHidden() {
        ComponentAdapter ca = new ConstructorInjectionComponentAdapter(new Class[]{ActionListener.class, MouseListener.class}, Footle.class);
        ImplementationHidingComponentAdapter ihca = new ImplementationHidingComponentAdapter(ca, true);
        Object comp = ihca.getComponentInstance(null);
        assertNotNull(comp);
        assertTrue(comp instanceof ActionListener);
        assertTrue(comp instanceof MouseListener);
    }

    public void testNonInterfaceInArrayCantBeHidden() {
        ComponentAdapter ca = new ConstructorInjectionComponentAdapter(new Class[]{String.class}, Footle.class);
        ImplementationHidingComponentAdapter ihca = new ImplementationHidingComponentAdapter(ca, true);
        try {
            ihca.getComponentInstance(null);
            fail("Oh no.");
        } catch (PicoIntrospectionException e) {
        }
    }

    public void testShouldThrowExceptionWhenAccessingNonInterfaceKeyedComponentInStrictMode() {
        ComponentAdapter ca = new ConstructorInjectionComponentAdapter("ww", Footle.class);
        ImplementationHidingComponentAdapter ihca = new ImplementationHidingComponentAdapter(ca, true);
        try {
            ihca.getComponentInstance(null);
            fail("Oh no.");
        } catch (PicoIntrospectionException e) {
        }
    }

    public class Footle implements ActionListener, MouseListener {
        public void actionPerformed(ActionEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

    }
}
