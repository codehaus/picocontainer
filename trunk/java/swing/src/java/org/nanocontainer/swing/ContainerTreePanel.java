/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.swing;

import org.picocontainer.ComponentAdapter;
import org.nanocontainer.guimodel.BeanProperty;
import org.nanocontainer.guimodel.ComponentAdapterModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * An horizontal split-panel that displays a PicoContainer tree (top) and a
 * table with selected node properties (bottom).
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTreePanel extends JPanel {
	private final ContainerTree tree;
	private JTable table;

	public ContainerTreePanel(final ContainerTree tree, JComponent toolbar) {
        super(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.tree = tree;
		this.table = new JTable();

		JScrollPane topPane = new JScrollPane(this.tree);
		topPane.setMinimumSize(new Dimension(300, 150));
		topPane.setPreferredSize(new Dimension(400, 200));

		JScrollPane bottomPane = new JScrollPane(this.table);
		bottomPane.setMinimumSize(new Dimension(300, 150));
		bottomPane.setPreferredSize(new Dimension(400, 200));

		splitPane.setTopComponent(topPane);
		splitPane.setBottomComponent(bottomPane);

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

		// This selection listener update the table according
		// to the selected node.
		this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				final TreePath selPath = evt.getNewLeadSelectionPath();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							ComponentAdapter componentAdapter = (ComponentAdapter) selPath.getLastPathComponent();

							TableModel model = ComponentAdapterTableModel.getInstance(componentAdapter);
							table.setModel(model);

							validate();
						} catch (Exception e) {
							// Ignore it
						}
					}
				});
			}
		});

		// This mouse listener displays an edit dialog when
		// a property is selected.
		this.table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					TreePath path = tree.getSelectionPath();
					try {
						ComponentAdapter obj = (ComponentAdapter) path.getLastPathComponent();

						ComponentAdapterModel model = ComponentAdapterModel.getInstance(obj);
						BeanProperty bp = model.getProperty(table.getSelectedRow());

						// Only display the dialog if we can set the value
						// through its editor
						if (bp.isWritable() && (bp.getPropertyEditor() != null)) {
							BeanPropertyEditDialog dialog = new BeanPropertyEditDialog(bp);
							dialog.show();
							table.repaint();
						}
					} catch (Exception ex) {
						// Ignore it
					}
				}
			}
		});
	}
}
