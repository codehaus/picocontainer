package org.picoextras.idea;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.Anchor;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;

/**
 * Main class for the PicoContainer Plugin for IDEA.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoPlugin implements ApplicationComponent {
    /** Logger */
    private static final Logger log = Logger.getInstance(PicoPlugin.class.getName());

    private DefaultActionGroup spg;
    private PicoToggleAction toggleAction;

    public PicoPlugin() {
        toggleAction = new PicoToggleAction();
        spg = new DefaultActionGroup();
        spg.addSeparator();
        spg.add(toggleAction);
    }

    /**
     * Method is called after plugin is already created and configured. Plugin can start to communicate with
     * other plugin only in this method.
     */
    public void initComponent() {
        final ActionManager actionManager = ActionManager.getInstance();
        // try register action
        if (actionManager.getAction(PicoToggleAction.ID) == null) {
            actionManager.registerAction(PicoToggleAction.ID, toggleAction);
        }

        // add toggle icon into IntelliJ UI, add to MainToolbar
        DefaultActionGroup mainToolBar = (DefaultActionGroup) actionManager.getAction(ActionID.MAIN_TOOLBAR);
        if (mainToolBar != null) {
            mainToolBar.add(spg);
        }
        else {
            log.info("Can't find " + ActionID.MAIN_TOOLBAR + " group");
        }

        // add to menu "Window"
        DefaultActionGroup menuWindow = (DefaultActionGroup) actionManager.getAction(ActionID.WINDOW_MENU);
        if (menuWindow != null) {
            menuWindow.add(toggleAction, new Constraints(Anchor.AFTER, ActionID.WINDOW_MENU_INSPECTION));
        }
        else {
            log.info("Can't find " + ActionID.WINDOW_MENU + " group");
        }
    }

    public void disposeComponent() {
        ActionManager actionManager = ActionManager.getInstance();
        // remove toggle icon from InteliJ UI
        DefaultActionGroup mainToolBar = (DefaultActionGroup) actionManager.getAction(ActionID.MAIN_TOOLBAR);
        if (mainToolBar != null) {
            mainToolBar.remove(spg);
        }

        DefaultActionGroup menuWindow = (DefaultActionGroup) actionManager.getAction(ActionID.WINDOW_MENU);
        if (menuWindow != null) {
            menuWindow.remove(toggleAction);
        }

        toggleAction.clear();
        if (actionManager.getAction(PicoToggleAction.ID) != null) {
            actionManager.unregisterAction(PicoToggleAction.ID);
        }
    }

    public String getComponentName() {
        return PicoPlugin.class.getName();
    }
}
