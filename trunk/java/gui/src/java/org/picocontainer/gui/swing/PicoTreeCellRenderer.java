package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.ComponentNode;

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
    private final Icon picocontainer = new ImageIcon(getClass().getResource("picocontainer.gif"));

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if( value instanceof ComponentNode ) {
            Class componentImplementation = (Class) ((ComponentNode)value).getUserObject();
            label.setText(componentImplementation.getName());
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(componentImplementation);
                Icon icon = new ImageIcon(beanInfo.getIcon(BeanInfo.ICON_COLOR_16x16));
                label.setIcon(icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            label.setText("PicoContainer");
            label.setIcon(picocontainer);
        }
        return label;
    }
}
