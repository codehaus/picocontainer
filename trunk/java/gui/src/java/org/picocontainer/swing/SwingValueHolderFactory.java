package org.picocontainer.swing;

import org.picocontainer.gui.ValueHolderFactory;
import org.picocontainer.gui.ValueHolder;

import javax.swing.JTextField;
import java.beans.FeatureDescriptor;

/**
 * TODO remove this class?
 * @author <a href="mailto:aslak.hellesoy at bekk.no">Aslak Helles&oslash;y</a>
 * @version $Revision$
 */
class SwingValueHolderFactory implements ValueHolderFactory {
    public ValueHolder createValueHolder(Class parameterClass, FeatureDescriptor featureDescriptor) {
        return new StringValueHolder();
    }

    private static class StringValueHolder extends JTextField implements ValueHolder {
        public Object getValue() {
            return getText();
        }

        public void setValue(Object o) {
            setText(o.toString());
        }
    }
}
