/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picoextras.swing;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ContextComboBoxModel extends AbstractListModel implements ComboBoxModel{
    private String[] values;
    private List filteredValues = new ArrayList();
    private String regexp = ".*";
    private Object selected;

    public int getSize() {
        return filteredValues.size();
    }

    public Object getElementAt(int index) {
        return filteredValues.get(index);
    }

    public Object getSelectedItem() {
        return selected;
    }

    public void setSelectedItem(Object anItem) {
        selected = anItem;
    }

    public void setValues(String[] values) {
        this.values = values;
        computeSelectedValues();
    }

    public void setFilter(String pseudoRegex) {
        pseudoRegex = pseudoRegex.toLowerCase();
        if(pseudoRegex.startsWith("*")) {
            pseudoRegex = "." + pseudoRegex;
        }
        regexp = ".*" + pseudoRegex + ".*";
        computeSelectedValues();
    }

    private void computeSelectedValues() {
        filteredValues.clear();
        for (int i = 0; i < values.length; i++) {
            if(Pattern.matches(regexp, values[i].toLowerCase())) {
                filteredValues.add(values[i]);
            }
        }
        fireContentsChanged(this, 0, getSize());
    }
}