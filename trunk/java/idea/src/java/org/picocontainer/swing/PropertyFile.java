package org.picocontainer.swing;

import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import java.beans.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Support for a PropertyEditor that uses text.
 * @author <a href="mailto:aslak.hellesoy at bekk.no">Aslak Helles&oslash;y</a>
 * @version $Revision$
 */
class PropertyFile extends JPanel implements DocumentListener, PropertyChangeListener {
    private final PropertyEditor _propertyEditor;
    private final PropertyDescriptor _propertyDescriptor;
    private final Object _bean;

    private JTextField _fileField = new JTextField();
    private JFileChooser _fileChooser = new JFileChooser();

    private class BrowseAction extends AbstractAction {
        public BrowseAction() {
            super("...");
        }

        public void actionPerformed(ActionEvent evt) {
            int returnVal = _fileChooser.showDialog(PropertyFile.this, "Output Directory");
            if( returnVal == JFileChooser.APPROVE_OPTION ) {
                File file = _fileChooser.getSelectedFile();
//                _propertyEditor.setValue( file );
                _fileField.setText( file.getAbsolutePath() );
            }
        }
    }

    PropertyFile(PropertyEditor propertyEditor, PropertyDescriptor propertyDescriptor, Object bean ) {
        super(new BorderLayout());
System.out.println("new PropertyFile");
        _propertyEditor = propertyEditor;
        _propertyDescriptor = propertyDescriptor;
        _bean = bean;

        _fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        add( _fileField, BorderLayout.CENTER);
        add( new JButton(new BrowseAction()), BorderLayout.EAST);

        _fileField.setText(_propertyEditor.getValue() != null ? _propertyEditor.getAsText() : "");

        _fileField.getDocument().addDocumentListener(this);
        _propertyEditor.addPropertyChangeListener(this);
        updateEditor();
    }

    protected void updateEditor() {
        try {
            _propertyEditor.setValue(new File( _fileField.getText() ));
        } catch (IllegalArgumentException e) {
            LogFactory.getLog(PropertyFile.class).error("Couldn't set new value", e);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        // Set the new value
        try {
            // Get the source. Should be "our" Property editor.
            // The event will always contain null as the newValue :-(
            PropertyEditor propertyEditor = (PropertyEditor) evt.getSource();
            Object newValue = propertyEditor.getValue();
System.out.println("New value:" + newValue);
            _propertyDescriptor.getWriteMethod().invoke( _bean, new Object[] {newValue} );
        } catch (Exception e) {
            LogFactory.getLog(PropertyFile.class).error("Couldn't set new value", e);
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void insertUpdate(DocumentEvent e) {
        updateEditor();
    }

    public void removeUpdate(DocumentEvent e) {
        updateEditor();
    }

    public void changedUpdate(DocumentEvent e) {
        updateEditor();
    }
}
