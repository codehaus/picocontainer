package org.nanocontainer.idea;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.util.*;

/**
 * Toggle action for XDoclet Build.
 *
 * @author <a href="mailto:mbo at jcs.be">Mathias Bogaert</a>
 * @version $Revision$
 */
public class XDocletToggle extends ToggleAction {
    public static final String ID = "XDocletToggleAction";
    public final static String XDOCLET_BUILD = "XDoclet Build";
    private static final Icon ICON = new ImageIcon(XDocletToggle.class.getResource("toggle.gif"));

    private Map consoles = new HashMap();
    private List managers = new ArrayList();

    public boolean isSelected(AnActionEvent event) {
        Project project = (Project) event.getDataContext().getData(DataConstants.PROJECT);
        return project != null && consoles.get(project) != null;
    }

    public void setSelected(AnActionEvent event, boolean isSelected) {
        Project project = (Project) event.getDataContext().getData(DataConstants.PROJECT);
        if (project != null) {
            final ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindow console = (ToolWindow) consoles.get(project);
            if (isSelected && console == null) {
//                BeanContextSupportEx xdocletContainer = new BeanContextSupportEx();
//                XDoclet xdoclet = new XDoclet();
//                xdocletContainer.add(xdoclet);
//
//                try {
//                    xdoclet.createPlugin("velocity");
//                    xdoclet.createPlugin("jelly");
//                }
//                catch (XDocletException e) {
//                    e.printStackTrace();
//                }
//
//                // show window
//                manager.registerToolWindow(XDOCLET_BUILD,
//                        new BeanContextConfigurationPanel(xdocletContainer),
//                        ToolWindowAnchor.RIGHT);
//
//                console = manager.getToolWindow(XDOCLET_BUILD);
//
//                if (console != null) {
//                    // Add manager to array to do unregister on finalize
//                    managers.add(manager);
//                    consoles.put(project, console);
//                    console.setIcon(ICON);
//                    console.show(null);
//                    console.activate(null);
//                }
            }
            else if (!isSelected && console != null) {
                // Hide window
                console.hide(null);
                manager.unregisterToolWindow(XDOCLET_BUILD);
                managers.remove(manager);
                consoles.remove(project);
            }
        }
    }

    public XDocletToggle() {
        super(XDOCLET_BUILD, "Show/Hide " + XDOCLET_BUILD, ICON);
    }

    public void clear() {
        // unregister all created ToolWindows
        Iterator iterator = managers.iterator();
        while (iterator.hasNext()) {
            ToolWindowManager manager = (ToolWindowManager) iterator.next();
            manager.unregisterToolWindow(XDOCLET_BUILD);
        }
        managers.clear();
        consoles.clear();
    }
}
