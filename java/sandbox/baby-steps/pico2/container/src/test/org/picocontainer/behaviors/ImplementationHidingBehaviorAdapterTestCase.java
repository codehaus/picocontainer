package org.picocontainer.behaviors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.behaviors.ImplementationHidingBehaviorAdapter;
import org.picocontainer.injectors.ConstructorInjectionAdapter;

public class ImplementationHidingBehaviorAdapterTestCase extends TestCase {

    public void testMultipleInterfacesCanBeHidden() {
        ComponentAdapter ca = new ConstructorInjectionAdapter(new Class[]{ActionListener.class, MouseListener.class}, Footle.class);
        ImplementationHidingBehaviorAdapter ihca = new ImplementationHidingBehaviorAdapter(ca);
        Object comp = ihca.getComponentInstance(null);
        assertNotNull(comp);
        assertTrue(comp instanceof ActionListener);
        assertTrue(comp instanceof MouseListener);
    }

    public void testNonInterfaceInArrayCantBeHidden() {
        ComponentAdapter ca = new ConstructorInjectionAdapter(new Class[]{String.class}, Footle.class);
        ImplementationHidingBehaviorAdapter ihca = new ImplementationHidingBehaviorAdapter(ca);
        try {
            ihca.getComponentInstance(null);
            fail("PicoIntrospectionException expected");
        } catch (PicoIntrospectionException e) {
            // expected        
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
