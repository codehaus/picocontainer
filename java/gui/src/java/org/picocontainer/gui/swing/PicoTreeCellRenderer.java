package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.ComponentNode;
import org.picocontainer.gui.model.BeanPropertyModel;

import javax.swing.tree.DefaultTreeCellRenderer;
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
    private final Icon genericComponentIcon = new ImageIcon(getClass().getResource("picocontainer.gif"));

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
                    icon = genericComponentIcon;
                }
                label.setIcon(icon);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(tree, e.getStackTrace());
            }
        } else {
            label.setText("PicoContainer");
            label.setIcon(picoContainerIcon);
        }
        return label;
    }
}
