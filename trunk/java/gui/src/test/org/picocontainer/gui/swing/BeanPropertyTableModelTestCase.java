package org.picocontainer.gui.swing;

import junit.framework.TestCase;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

import org.picocontainer.gui.model.BeanPropertyTableModel;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyTableModelTestCase extends TestCase {
    private BeanPropertyTableModel model;

    public static class Man {
        public String birth;
        public String haircolour;
        public String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBirth() {
            return birth;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public String getHaircolour() {
            return haircolour;
        }

        public void setHaircolour(String haircolour) {
            this.haircolour = haircolour;
        }
    }

    protected void setUp() throws IntrospectionException {
        BeanInfo manInfo = Introspector.getBeanInfo(Man.class);
        model = new BeanPropertyTableModel(manInfo);
    }

    public void testRows() throws IntrospectionException {
        assertEquals(3, model.getRowCount());

        // they are automatically alphabetically sorted :-)
        assertEquals("name", model.getValueAt(2,0));
    }

    public void testDisplay() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Man man = new Man();
        man.birth = "1971";
        man.haircolour = "Blond";
        man.name = "Aslak";

        model.display(man);
        
        assertEquals(3, model.getRowCount());

        // they are automatically alphabetically sorted :-)
        assertEquals("Aslak", model.getValueAt(2,1));
    }

    public void testUpdate() throws InvocationTargetException, IllegalAccessException {
        model.setValueAt("rinkrank", 2, 1);

        Man man = new Man();
        model.update(man);
    }
}
