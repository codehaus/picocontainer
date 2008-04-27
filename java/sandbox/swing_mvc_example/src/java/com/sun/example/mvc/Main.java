/*
 * Main.java
 *
 * Created on January 10, 2007, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.example.mvc;

import com.sun.example.mvc.controller.DefaultController;
import com.sun.example.mvc.model.DocumentModel;
import com.sun.example.mvc.model.TextElementModel;
import com.sun.example.mvc.view.DisplayFrame;
import com.sun.example.mvc.view.DisplayViewPanel;
import com.sun.example.mvc.view.PropertiesDialog;
import com.sun.example.mvc.view.PropertiesViewPanel;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.behaviors.Caching;

import java.beans.PropertyChangeSupport;

/**
 *
 * @author Robert Eckstein
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {

        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching().wrap(new ConstructorInjection()));

        pico.addComponent(PropertyChangeSupport.class);

        pico.addComponent(TextElementModel.class);
        pico.addComponent(DocumentModel.class);

        pico.addComponent(DisplayViewPanel.class);
        pico.addComponent(PropertiesViewPanel.class);
        pico.addComponent(DisplayFrame.class);
        pico.addComponent(PropertiesDialog.class);

        pico.addComponent(DefaultController.class);

        DefaultController controller = pico.getComponent(DefaultController.class);

        controller.addView(pico.getComponent(DisplayViewPanel.class));
        controller.addView(pico.getComponent(PropertiesViewPanel.class));
        controller.addModel(pico.getComponent(TextElementModel.class));
        controller.addModel(pico.getComponent(DocumentModel.class));

        pico.start();
             
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
    }
    
}
