package org.picoextras.swing;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picoextras.guimodel.BeanProperty;
import org.picoextras.guimodel.ComponentAdapterModel;

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
public class ContainerTreePanel extends JSplitPane {
	private ContainerTree tree;
	private JTable table;

	public ContainerTreePanel(PicoContainer pico) {
		super(JSplitPane.VERTICAL_SPLIT);
		this.tree = new ContainerTree(pico);
		this.table = new JTable();

		JScrollPane topPane = new JScrollPane(this.tree);
		topPane.setMinimumSize(new Dimension(300, 150));
		topPane.setPreferredSize(new Dimension(400, 200));

		JScrollPane bottomPane = new JScrollPane(this.table);
		bottomPane.setMinimumSize(new Dimension(300, 150));
		bottomPane.setPreferredSize(new Dimension(400, 200));

		this.setTopComponent(topPane);
		this.setBottomComponent(bottomPane);

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
