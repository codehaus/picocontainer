package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVerificationException;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class ImplementationHidingComponentAdapterTestCase extends TestCase {

    public void testMultipleInterfacesCanBeHidden() {
        ComponentAdapter ca = new ConstructorInjectionComponentAdapter(new Class[] {ActionListener.class, MouseListener.class},Footle.class);
        ImplementationHidingComponentAdapter ihca = new ImplementationHidingComponentAdapter(ca);
        Object comp = ihca.getComponentInstance();
        assertNotNull(comp);
        assertTrue(comp instanceof ActionListener);
        assertTrue(comp instanceof MouseListener);
    }

    public void testNonInterfaceInArrayCantBeHidden() {
        ComponentAdapter ca = new ConstructorInjectionComponentAdapter(new Class[] {String.class},Footle.class);
        ImplementationHidingComponentAdapter ihca = new ImplementationHidingComponentAdapter(ca);
        try {
            Object comp = ihca.getComponentInstance();
            fail("Oh no.");
        } catch (PicoIntrospectionException e) {
        }
    }

    public void testNonInterfaceCantBeHidden() {
        ComponentAdapter ca = new ConstructorInjectionComponentAdapter("ww",Footle.class);
        ImplementationHidingComponentAdapter ihca = new ImplementationHidingComponentAdapter(ca);
        try {
            Object comp = ihca.getComponentInstance();
            fail("Oh no.");
        } catch (PicoIntrospectionException e) {
        }
      }

    public class Footle implements ActionListener , MouseListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("--> foo");
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
