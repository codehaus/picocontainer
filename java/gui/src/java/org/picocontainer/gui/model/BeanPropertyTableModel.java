package org.picocontainer.gui.model;

import org.picocontainer.extras.BeanPropertyComponentAdapterFactory;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;
import java.beans.PropertyDescriptor;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyTableModel implements TableModel {
    private static final String[] TABLE_HEADER = new String[] {"Property", "Value"};
    public static final TableModel EMPTY_MODEL = new DefaultTableModel(BeanPropertyTableModel.TABLE_HEADER, 0);

    private final BeanPropertyComponentAdapterFactory.Adapter componentAdapter;

    public BeanPropertyTableModel(BeanPropertyComponentAdapterFactory.Adapter componentAdapter) {
        this.componentAdapter = componentAdapter;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return componentAdapter.getPropertyDescriptors().length;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean hasWriteMethod = componentAdapter.getPropertyDescriptors()[rowIndex].getWriteMethod() != null;
        return columnIndex == 1 && hasWriteMethod;
    }

    public Class getColumnClass(int columnIndex) {
        return Object.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        PropertyDescriptor propertyDescriptor = componentAdapter.getPropertyDescriptors()[rowIndex];
        if( columnIndex == 0 ) {
            return propertyDescriptor.getDisplayName();
        } else {
            return componentAdapter.getPropertValue(propertyDescriptor);
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        componentAdapter.setPropertyValue(componentAdapter.getPropertyDescriptors()[rowIndex], aValue);
    }

    public String getColumnName(int columnIndex) {
        return TABLE_HEADER[columnIndex];
    }

    public void addTableModelListener(TableModelListener l) {

    }

    public void removeTableModelListener(TableModelListener l) {

    }
}
