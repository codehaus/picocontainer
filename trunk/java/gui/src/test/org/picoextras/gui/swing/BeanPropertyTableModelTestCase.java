package org.picoextras.gui.swing;

import junit.framework.TestCase;
import java.beans.IntrospectionException;
import org.picoextras.gui.model.BeanPropertyTableModel;
import org.picocontainer.extras.BeanPropertyComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.PicoIntrospectionException;

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

    protected void setUp() throws PicoIntrospectionException {
        BeanPropertyComponentAdapterFactory factory = new BeanPropertyComponentAdapterFactory(
                new DefaultComponentAdapterFactory()
        );
//        BeanPropertyComponentAdapterFactory.Adapter adapter =
//                (BeanPropertyComponentAdapterFactory.Adapter) factory.createComponentAdapter("whatever", Man.class, null);
//        model = new BeanPropertyTableModel(adapter);
    }

    public void testRows() throws IntrospectionException {
//        assertEquals(4, model.getRowCount());
//
//        // they are automatically alphabetically sorted, and there is class too
//        assertEquals("name", model.getValueAt(3,0));
    }

}
