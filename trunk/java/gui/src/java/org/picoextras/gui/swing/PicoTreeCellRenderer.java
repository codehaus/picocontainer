package org.picoextras.gui.swing;

import org.picoextras.gui.model.ComponentNode;
import org.picoextras.gui.model.BeanPropertyModel;
import org.picoextras.gui.model.ContainerNode;
import org.picoextras.gui.model.BeanPropertyTableModel;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.Introspector;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoTreeCellRenderer extends DefaultTreeCellRenderer {
    private final Icon picoContainerIcon = new ImageIcon(getClass().getResource("picocontainer.gif"));
    private final Icon defaultComponentIcon = new ImageIcon(getClass().getResource("defaultcomponent.gif"));

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if( value instanceof ComponentNode ) {
            BeanPropertyModel beanPropertyModel = (BeanPropertyModel) ((ComponentNode)value).getUserObject();
            Class componentImplementation = beanPropertyModel.getBeanClass();
            label.setText(componentImplementation.getName());
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(componentImplementation);
                Image image = beanInfo.getIcon(BeanInfo.ICON_COLOR_16x16);
                Icon icon;
                if(image != null) {
                    icon = new ImageIcon(image);
                } else {
                    icon = defaultComponentIcon;
                }
                label.setIcon(icon);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(tree, e.getStackTrace());
            }
        } else if(value instanceof PicoContainer) {
            label.setText("PicoContainer");
            label.setIcon(picoContainerIcon);
        } else if(value instanceof ComponentAdapter){
//            BeanPropertyTableModel beanPropertyTableModel = (BeanPropertyTableModel) ((DefaultMutableTreeNode)value).getUserObject();
//            return new JScrollPane( new JTable(beanPropertyTableModel));
            ComponentAdapter componentAdapter = (ComponentAdapter) value;

            label.setText(componentAdapter.getComponentImplementation().getName());
        }
        return label;
    }
}
