package org.picocontainer.gui.model;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyTableModel implements TableModel {
    private static final String[] TABLE_HEADER = new String[]{"Property", "Value"};
    public static final TableModel EMPTY_MODEL = new DefaultTableModel(BeanPropertyTableModel.TABLE_HEADER, 0);

    private final Class clazz;
    private final PropertyDescriptor[] propertyDescriptors;
    private final Object[] propertyValues;

    public BeanPropertyTableModel(Class clazz) throws IntrospectionException {
        this.clazz = clazz;
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        propertyDescriptors = beanInfo.getPropertyDescriptors();
        propertyValues = new Object[propertyDescriptors.length];
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return propertyDescriptors.length;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean hasWriteMethod = propertyDescriptors[rowIndex].getWriteMethod() != null;
        return columnIndex == 1 && hasWriteMethod;
    }

    public Class getColumnClass(int columnIndex) {
        return Object.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return propertyDescriptors[rowIndex].getDisplayName();
        } else {
            return propertyValues[rowIndex];
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // columnIndex is always 1 (never 0)
        propertyValues[rowIndex] = aValue;
    }

    public String getColumnName(int columnIndex) {
        return TABLE_HEADER[columnIndex];
    }

    public void addTableModelListener(TableModelListener l) {

    }

    public void removeTableModelListener(TableModelListener l) {

    }
}
