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

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ContextComboBox extends JComboBox {
    private ContextComboBoxModel model;
    private JTextField textField;
    private boolean updateField = true;
    private Object item;

    public ContextComboBox(ContextComboBoxModel model) {
        super(model);
        this.model = model;
        setEditable(true);
        setEditor(new BasicComboBoxEditor(){
            {
                textField = (JTextField) getEditorComponent();
                textField.getDocument().addDocumentListener(new DocumentListener(){
                    public void changedUpdate(DocumentEvent e) {
                        updatePopup();
                    }

                    public void insertUpdate(DocumentEvent e) {
                        updatePopup();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        updatePopup();
                    }
                });

                textField.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("item = " + item);
                        setSelectedItem(item);
                        textField.setText(item.toString());
                    }
                });
            }
        });
        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                item = e.getItem();
                final int stateChange = e.getStateChange();
                System.out.println("stateChange = " + stateChange);
            }

        });
    }

    private void updatePopup() {
        if (updateField) {
            final String text = textField.getText();
            updateField = false;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    model.setFilter(text);
                    hidePopup();
                    showPopup();
                    textField.setText(text);
                    updateField = true;
                }
            });
        }
    }
}