import org.nanocontainer.reflection.ImplementationHidingNanoPicoContainer
import org.nanocontainer.ClassNameKey

builder = new org.nanocontainer.script.groovy.NanoContainerBuilder()

parent = builder.container(parent:parent,class:ImplementationHidingNanoPicoContainer) {
    component(instance:org.nanocontainer.DefaultNanoContainer) // a class defn
    classPathElement(path:"comps/api.jar")
    classLoader {
        classPathElement(path:"comps/honeyimpl.jar")
        component(classNameKey:"org.nanocontainer.boot.Honey", class:"org.nanocontainer.boot.BeeHiveHoney")
    }
    classLoader {
        classPathElement(path:"comps/bearimpl.jar")
        component(class:"org.nanocontainer.boot.BrownBear")
    }
}
pico = parent.getPico()
pico.getComponentInstances()
