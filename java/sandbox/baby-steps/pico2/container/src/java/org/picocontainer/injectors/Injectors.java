package org.picocontainer.injectors;

import org.picocontainer.InjectionFactory;
import org.picocontainer.injectors.AnyInjectionFactory;
import org.picocontainer.injectors.SetterInjectionFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.injectors.MethodAnnotationInjectionFactory;

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

    public static InjectionFactory MADI() {
        return new MethodAnnotationInjectionFactory();
    }

    public static InjectionFactory FADI() {
        return new FieldAnnotationInjectionFactory();
    }

}
