package org.picocontainer.gui.model;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyTableModel extends DefaultTableModel {
    private static final String[] TABLE_HEADER = new String[] {"Property", "Value"};
    public static final TableModel EMPTY_MODEL = new DefaultTableModel(BeanPropertyTableModel.TABLE_HEADER, 0);

    private final List settableAndGettablePropertyDescriptors;

    public BeanPropertyTableModel(BeanInfo beanInfo) {
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        // It will be a little smaller
        settableAndGettablePropertyDescriptors = new ArrayList(propertyDescriptors.length);

        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            if(propertyDescriptor.getWriteMethod() != null) {
                settableAndGettablePropertyDescriptors.add(propertyDescriptor);
            }
        }

        Object[][] dataVector = new Object[settableAndGettablePropertyDescriptors.size()][2];
        int i = 0;
        for (Iterator iterator = settableAndGettablePropertyDescriptors.iterator(); iterator.hasNext();) {
            PropertyDescriptor propertyDescriptor = (PropertyDescriptor) iterator.next();
            dataVector[i++][0] = propertyDescriptor.getDisplayName();
        }

        this.setDataVector(dataVector, TABLE_HEADER);
    }

    public void display(Object bean) throws InvocationTargetException, IllegalAccessException {
        int i = 0;
        for(Iterator p = settableAndGettablePropertyDescriptors.iterator(); p.hasNext(); ) {
            PropertyDescriptor propertyDescriptor = (PropertyDescriptor) p.next();
            setValueAt(propertyDescriptor.getReadMethod().invoke(bean, null) , i++, 1);
        }
    }

    public void update(Object bean) throws InvocationTargetException, IllegalAccessException {
        int i = 0;
        for(Iterator p = settableAndGettablePropertyDescriptors.iterator(); p.hasNext(); ) {
            PropertyDescriptor propertyDescriptor = (PropertyDescriptor) p.next();
            propertyDescriptor.getWriteMethod().invoke(bean, new Object[] {
                getValueAt(i++, 1)
            });
        }
    }
}
