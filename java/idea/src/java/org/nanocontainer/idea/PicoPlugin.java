package org.picoextras.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.swing.ContainerTreePanel;
import org.picoextras.swing.IconHelper;
import org.picoextras.idea.action.StartContainerAction;
import org.picoextras.idea.action.StopContainerAction;
import org.picoextras.idea.action.RegisterComponentAction;
import org.picoextras.idea.action.UnregisterComponentAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoPlugin implements ProjectComponent {
    public static final String TOOL_WINDOW_ID = "PicoContainer";
    private static final String TOOL_WINDOW_TOOLBAR_ID = "PicoContainerToolBar";
    public static final String START_ACTION_ID = "PicoContainer.Start";
    public static final String STOP_ACTION_ID = "PicoContainer.Stop";
    public static final String REGISTER_ACTION_ID = "PicoContainer.Register";
    public static final String UNREGISTER_ACTION_ID = "PicoContainer.Unregister";

    private Project project;
    private DefaultPicoContainer pico;

    public PicoPlugin(Project project) {
        this.project = project;
    }

    public void projectOpened() {
        final ToolWindowManager manager = ToolWindowManager.getInstance(project);
        pico = new DefaultPicoContainer();
        ContainerTreePanel picoGui = new ContainerTreePanel(pico, createToolBar().getComponent());

        ToolWindow toolWindow = manager.registerToolWindow(TOOL_WINDOW_ID,
                picoGui,
                ToolWindowAnchor.RIGHT);
        toolWindow.setIcon(IconHelper.getIcon(IconHelper.PICO_CONTAINER_ICON));
    }

    private ActionToolbar createToolBar() {
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup toolGroup = (DefaultActionGroup) registerAction(actionManager, new DefaultActionGroup(TOOL_WINDOW_TOOLBAR_ID, false), TOOL_WINDOW_TOOLBAR_ID);

        List actions = new ArrayList();
        actions.add(registerAction(actionManager, new StartContainerAction(pico), START_ACTION_ID));
        actions.add(registerAction(actionManager, new StopContainerAction(pico), STOP_ACTION_ID));
        actions.add(Separator.getInstance());
        actions.add(registerAction(actionManager, new RegisterComponentAction(pico), REGISTER_ACTION_ID));
        actions.add(registerAction(actionManager, new UnregisterComponentAction(pico), UNREGISTER_ACTION_ID));
        addToolBarActions(toolGroup, actions);
        return actionManager.createActionToolbar(TOOL_WINDOW_TOOLBAR_ID, toolGroup, true);
    }

    private AnAction registerAction(ActionManager actionManager, AnAction action, String id) {
        id = project.getProjectFile().getNameWithoutExtension() + "." + id;
        if (actionManager.getAction(id) == null) {
            actionManager.registerAction(id, action);
            return action;
        } else {
            return actionManager.getAction(id);
        }
    }

    private void addToolBarActions(DefaultActionGroup toolGroup, List actions) {
        toolGroup.removeAll();
        for (Iterator iterator = actions.iterator(); iterator.hasNext();) {
            AnAction a = (AnAction) iterator.next();
            toolGroup.add(a);
        }
    }

    public String getComponentName() {
        return PicoPlugin.class.getName();
    }

    public void initComponent() {

    }

    public void disposeComponent() {

    }

    public void projectClosed() {

    }
}
