package org.picocontainer.swing;

import org.apache.commons.logging.LogFactory;

import javax.swing.*;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.HeadlessException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

import java.lang.reflect.Method;
import java.io.File;

/**
 *
 * @author <a href="mailto:aslak.hellesoy at bekk.no">Aslak Helles&oslash;y</a>
 * @version $Revision$
 */
class PropertySheet extends JPanel {
    public PropertySheet(Object bean) {
        setLayout(new GridLayout(0, 2));

        // Populate the sheet with components
        BeanInfo beanInfo = null;

        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        } catch (IntrospectionException e) {
            e.printStackTrace();
            throw new IllegalStateException("Couldn't get BeanInfo for " + bean.getClass() + ":" + e.getMessage());
        }

        // JOptionPane.showMessageDialog(null,"PropertySheet Got BeanInfo for " + bean.getClass() + ":" + beanInfo.getClass().getName());
        // Set the name on this panel. Needed for CardLayout to work.
        setName(beanInfo.getBeanDescriptor().getName());

        // Make property editors
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        for (int i = 0; i < propertyDescriptors.length; i++) {
            if (!propertyDescriptors[i].isHidden()) {
                try {
                    Component editor = createEditorComponent(propertyDescriptors[i], bean);

                    JLabel label = new JLabel(propertyDescriptors[i].getDisplayName());

                    label.setToolTipText(propertyDescriptors[i].getShortDescription());
                    add(label);
                    add(editor);
                } catch (IntrospectionException e) {
                    System.out.println(e.getMessage());
                } catch (HeadlessException e) {
                    e.printStackTrace(); //To change body of catch statement use Options | File Templates.
                }
            }
        }
    }

    private Component createEditorComponent(PropertyDescriptor propertyDescriptor, Object bean)
        throws IntrospectionException {
        // Instantiate a new editor.
        PropertyEditor propertyEditor = PropertyEditorManager.findEditor(propertyDescriptor.getPropertyType());

        if (propertyEditor == null) {
            throw new IntrospectionException("No property editor found for type "
                + propertyDescriptor.getPropertyType().getName());
        }

        // Ask the bean for its current value.
        Method getter = propertyDescriptor.getReadMethod();

        if (getter != null) {
            try {
                Object value = getter.invoke(bean, null);

                propertyEditor.setValue(value);
            } catch (Exception e) {
                LogFactory.getLog(PropertySheet.class).error("Couldn't create editor", e);
                JOptionPane.showMessageDialog(this,"Couldn't create editor: " + e.getMessage());
                throw new IntrospectionException(e.getMessage());
            }

            // Now figure out how to display it...
            if (propertyEditor.isPaintable() && propertyEditor.supportsCustomEditor()) {
                // editorComponent = new PropertyCanvas(frame, editor);
                return propertyEditor.getCustomEditor();
            } else if (propertyEditor.getTags() != null) {
                // editorComponent = new PropertySelector(editor);
            } else if (getter.getReturnType().equals(String.class)) {
                return new PropertyText(propertyEditor, propertyDescriptor, bean);
            } else if (getter.getReturnType().equals(File.class)) {
                return new PropertyFile(propertyEditor, propertyDescriptor, bean);
            }
        }
        throw new IntrospectionException("Property \"" + propertyDescriptor.getName()
            + "\" has non-displayabale editor.");
    }
}
