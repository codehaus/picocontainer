package org.picoextras.idea;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.idea.action.AddContainer;
import org.picoextras.idea.action.RegisterComponent;
import org.picoextras.idea.action.StartContainer;
import org.picoextras.idea.action.StopContainer;
import org.picoextras.idea.action.UnregisterComponent;
import org.picoextras.swing.ContainerTree;
import org.picoextras.swing.ContainerTreePanel;
import org.picoextras.swing.IconHelper;
import org.picoextras.swing.action.AddContainerAction;
import org.picoextras.swing.action.RegisterComponentAction;
import org.picoextras.swing.action.StartContainerAction;
import org.picoextras.swing.action.StopContainerAction;
import org.picoextras.swing.action.UnregisterComponentAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoPlugin implements ProjectComponent {
    private static final String TOOL_WINDOW_ID = "PicoContainer";
    private static final String TOOL_WINDOW_TOOLBAR_ID = "PicoContainerToolBar";
    private static final String START_CONTAINER_ACTION_ID = "PicoContainer.StartContainer";
    private static final String STOP_CONTAINER_ACTION_ID = "PicoContainer.StopContainer";
    private static final String ADD_CONTAINER_ACTION_ID = "PicoContainer.AddContainer";
    private static final String REGISTER_COMPONENT_ACTION_ID = "PicoContainer.RegisterComponent";
    private static final String UNREGISTER_COMPONENT_ACTION_ID = "PicoContainer.UnregisterComponent";

    private Project project;
    private ContainerTree tree = new ContainerTree(new DefaultPicoContainer(), IconHelper.getIcon("/nodes/class.png", false));

    public PicoPlugin(Project project) {
        this.project = project;
    }

    public void projectOpened() {
        final ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ContainerTreePanel picoGui = new ContainerTreePanel(tree, createToolBar().getComponent());

        ToolWindow toolWindow = manager.registerToolWindow(TOOL_WINDOW_ID,
                picoGui,
                ToolWindowAnchor.RIGHT);
        toolWindow.setIcon(IconHelper.getIcon(IconHelper.PICO_CONTAINER_ICON, false));

        ModuleManager moduleManager = ModuleManager.getInstance(project);
        final Module[] modules = moduleManager.getModules();
        for (int i = 0; i < modules.length; i++) {
            Module module = modules[i];
            final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            final String compilerOutputPathUrl = moduleRootManager.getCompilerOutputPathUrl();
            System.out.println("compilerOutputPathUrl = " + compilerOutputPathUrl);
            final VirtualFile[] files = moduleRootManager.getFiles(OrderRootType.CLASSES_AND_OUTPUT);
            for (int j = 0; j < files.length; j++) {
                VirtualFile file = files[j];
                String url = file.getUrl();
                System.out.println("url = " + url);
            }
        }
    }

    private ActionToolbar createToolBar() {
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup toolGroup = (DefaultActionGroup) registerAction(actionManager, new DefaultActionGroup(TOOL_WINDOW_TOOLBAR_ID, false), TOOL_WINDOW_TOOLBAR_ID);

        List actions = new ArrayList();
        actions.add(registerAction(actionManager, new StartContainer(new StartContainerAction("/actions/execute.png", tree)), START_CONTAINER_ACTION_ID));
        actions.add(registerAction(actionManager, new StopContainer(new StopContainerAction("/actions/suspend.png", tree)), STOP_CONTAINER_ACTION_ID));
        actions.add(Separator.getInstance());
        actions.add(registerAction(actionManager, new RegisterComponent(new RegisterComponentAction("/nodes/entryPoints.png", tree)), REGISTER_COMPONENT_ACTION_ID));
        actions.add(registerAction(actionManager, new AddContainer(new AddContainerAction(IconHelper.PICO_CONTAINER_ICON, tree)), ADD_CONTAINER_ACTION_ID));
        actions.add(registerAction(actionManager, new UnregisterComponent(new UnregisterComponentAction("/actions/exclude.png", tree)), UNREGISTER_COMPONENT_ACTION_ID));
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
