/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.swing;

import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.*;

public class AutoCompleteComboBox extends JComboBox {
    private static final Locale[] INSTALLED_LOCALES = Locale.getAvailableLocales();
    private ComboBoxModel model = null;

    public static void main(String[] args) {
        JFrame f = new JFrame("AutoCompleteComboBox");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AutoCompleteComboBox box = new AutoCompleteComboBox(INSTALLED_LOCALES, false);
        f.getContentPane().add(box);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    /**
     * Constructor for AutoCompleteComboBox - 	 * The Default Model is a TreeSet which is alphabetically sorted and doesnt allow duplicates.	 * @param items
     */
    public AutoCompleteComboBox(Object[] items, boolean caseSensitive) {
        super(items);
        model = new ComboBoxModel(items);
        setModel(model);
        setEditable(true);
        setEditor(new AutoCompleteEditor(this, caseSensitive));
    }

    /**
     * Constructor for AutoCompleteComboBox - 	 * The Default Model is a TreeSet which is alphabetically sorted and doesnt allow duplicates.	 * @param items
     */
    public AutoCompleteComboBox(Vector items, boolean caseSensitive) {
        super(items);
        model = new ComboBoxModel(items);
        setModel(model);
        setEditable(true);
        setEditor(new AutoCompleteEditor(this, caseSensitive));
    }

    /**
     * Constructor for AutoCompleteComboBox - 	 * This constructor uses JComboBox's Default Model which is a Vector.	 * @param caseSensitive
     */
    public AutoCompleteComboBox(boolean caseSensitive) {
        super();
        setEditable(true);
        setEditor(new AutoCompleteEditor(this, caseSensitive));
    }	/*	 * ComboBoxModel.java	 */

    public class ComboBoxModel extends DefaultComboBoxModel {
        /**
         * The TreeSet which holds the combobox's data (ordered no duplicates)
         */
        private TreeSet values = null;

        public ComboBoxModel(List items) {
            super();
            this.values = new TreeSet();
            int i, c;
            for (i = 0, c = items.size(); i < c; i++) values.add(items.get(i).toString());
            Iterator it = values.iterator();
            while (it.hasNext()) super.addElement(it.next().toString());
        }

        public ComboBoxModel(final Object items[]) {
            this(Arrays.asList(items));
        }
    }	/*	 * AutoCompleteEditor.java	 */

    public class AutoCompleteEditor extends BasicComboBoxEditor {
        public AutoCompleteEditor(JComboBox combo, boolean caseSensitive) {
            super();
            editor = new AutoCompleteEditorComponent(combo, caseSensitive);
        }
    }	/*	 * AutoCompleteEditorComponent.java	 */

    public class AutoCompleteEditorComponent extends JTextField {
        JComboBox combo = null;
        boolean caseSensitive = false;

        public AutoCompleteEditorComponent(JComboBox combo, boolean caseSensitive) {
            super();
            this.combo = combo;
            this.caseSensitive = caseSensitive;
        }

        /**
         * overwritten to return custom PlainDocument which does the work
         */
        protected Document createDefaultModel() {
            return new PlainDocument() {
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                    if (str == null || str.length() == 0) return;
                    int size = combo.getItemCount();
                    String text = getText(0, getLength());
                    for (int i = 0; i < size; i++) {
                        String item = combo.getItemAt(i).toString();
                        if (getLength() + str.length() > item.length()) continue;
                        if (!caseSensitive) {
                            if ((text + str).equalsIgnoreCase(item) || item.substring(0, getLength() + str.length()).equalsIgnoreCase(text + str)) {
                                combo.setSelectedIndex(i);
                                super.remove(0, getLength());
                                super.insertString(0, item, a);
                                return;
                            }
                        } else if (caseSensitive) {
                            if ((text + str).equals(item) || item.substring(0, getLength() + str.length()).equals(text + str)) {
                                combo.setSelectedIndex(i);
                                super.remove(0, getLength());
                                super.insertString(0, item, a);
                                return;
                            }
                        }
                    }
                }
            };
        }
    }
}

