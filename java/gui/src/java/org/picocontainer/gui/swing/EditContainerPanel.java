package org.picocontainer.gui.swing;

import javax.swing.*;
import javax.swing.text.Document;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class EditContainerPanel extends JPanel {

    public EditContainerPanel(AddPicoComponentAction addPicoComponentAction,
                              AddPicoContainerAction addPicoContainerAction,
                              ExecuteContainerAction executeContainerAction,
                              Document componentImplementationDocument) {

        JTextField componentField = new JTextField(componentImplementationDocument,PicoGui.A.class.getName(), 25);
        componentField.addActionListener(addPicoComponentAction);

        add(componentField);
        add(new JButton(addPicoComponentAction));
        add(new JButton(addPicoContainerAction));
        add(new JButton(executeContainerAction));
    }
}
