package org.nanocontainer.nanoweb.defaults;

import java.io.File;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;

public class JaninoClassBodyActionFactory extends AbstractFileBasedActionFactory {

    public JaninoClassBodyActionFactory(final String rootPath, final String extension) {
        super(rootPath, extension);
    }

    public JaninoClassBodyActionFactory(final String rootPath) {
        super(rootPath, "janino");
    }

    protected Class getClass(final File actionFile) throws Exception {
        ClassBodyEvaluator classBodyEvaluator;
        classBodyEvaluator = new ClassBodyEvaluator(new Scanner(actionFile), "Action", null, new Class[] {}, null);
        return classBodyEvaluator.evaluate();
    }

}
