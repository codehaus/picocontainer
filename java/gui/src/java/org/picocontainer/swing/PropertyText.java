package org.picocontainer.swing;

import org.apache.commons.logging.LogFactory;

import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import java.beans.*;

/**
 * Support for a PropertyEditor that uses text.
 * @author <a href="mailto:aslak.hellesoy at bekk.no">Aslak Helles&oslash;y</a>
 * @version $Revision$
 */
class PropertyText extends JTextField implements DocumentListener, PropertyChangeListener {
    private final PropertyEditor _propertyEditor;
    private final PropertyDescriptor _propertyDescriptor;
    private final Object _bean;

    PropertyText(PropertyEditor propertyEditor, PropertyDescriptor propertyDescriptor, Object bean ) {
        super();
        _propertyEditor = propertyEditor;
        _propertyDescriptor = propertyDescriptor;
        _bean = bean;

        setText(_propertyEditor.getValue() != null ? _propertyEditor.getAsText() : "");

        getDocument().addDocumentListener(this);
        _propertyEditor.addPropertyChangeListener(this);
        updateEditor();
    }

    protected void updateEditor() {
        try {
            _propertyEditor.setAsText(getText());
        } catch (IllegalArgumentException e) {
            LogFactory.getLog(PropertyText.class).error("Couldn't set new value", e);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        // Set the new value
        try {
            // Get the source. Should be "our" Property editor.
            // The event will always contain null as the newValue :-(
            PropertyEditor propertyEditor = (PropertyEditor) evt.getSource();
            String newValue = propertyEditor.getAsText();
            _propertyDescriptor.getWriteMethod().invoke( _bean, new Object[] {newValue} );
        } catch (Exception e) {
            LogFactory.getLog(PropertyText.class).error("Couldn't set new value", e);
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
