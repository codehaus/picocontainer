package org.picocontainer.gui.model;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyTableModel implements TableModel {
    private static final String[] TABLE_HEADER = new String[]{"Property", "Value"};
    public static final TableModel EMPTY_MODEL = new DefaultTableModel(BeanPropertyTableModel.TABLE_HEADER, 0);

    private final BeanPropertyModel model;

    public BeanPropertyTableModel(BeanPropertyModel model) {
        this.model = model;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return model.getSize();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean hasWriteMethod = model.getPropertyDescriptor(rowIndex).getWriteMethod() != null;
        return columnIndex == 1 && hasWriteMethod;
    }

    public Class getColumnClass(int columnIndex) {
        return Object.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return model.getPropertyDescriptor(rowIndex).getDisplayName();
        } else {
            return model.getPropertyValue(rowIndex);
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        model.setPropertyValue(aValue, rowIndex);
    }

    public String getColumnName(int columnIndex) {
        return TABLE_HEADER[columnIndex];
    }

    public void addTableModelListener(TableModelListener l) {

    }

    public void removeTableModelListener(TableModelListener l) {

    }
}
