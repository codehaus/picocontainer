package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.ComponentNode;
import org.picocontainer.ComponentAdapter;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if( value instanceof ComponentNode ) {
            Class componentImplementation = (Class) ((ComponentNode)value).getUserObject();
            label.setText(componentImplementation.getName());
        } else {
            label.setText("PicoContainer");
        }
        return label;
    }
}
