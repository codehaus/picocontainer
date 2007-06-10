package org.picocontainer.injectors;

import org.picocontainer.InjectionFactory;
import org.picocontainer.injectors.AnyInjectionFactory;
import org.picocontainer.injectors.SetterInjectionFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.injectors.AnnotationInjectionFactory;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: Jun 9, 2007
 * Time: 6:54:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class Injectors {

    public static InjectionFactory anyDI() {
        return new AnyInjectionFactory();
    }

    public static InjectionFactory SDI() {
        return new SetterInjectionFactory();
    }

    public static InjectionFactory CDI() {
        return new ConstructorInjectionFactory();
    }

    public static InjectionFactory ADI() {
        return new AnnotationInjectionFactory();
    }
    
}
