package picocontainer;

public interface ComponentDecorator {

    Object decorateComponent(Class compType, Object instance);
    //Object decorateObejcts(Object[] instances);

}
