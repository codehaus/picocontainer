import org.nanocontainer.reflection.ImplementationHidingNanoPicoContainer
import org.nanocontainer.ClassNameKey

builder = new org.nanocontainer.script.groovy.NanoContainerBuilder()

parent = builder.container(parent:parent,class:ImplementationHidingNanoPicoContainer) {
    classPathElement(path:"lib/api.jar")
    classloader {
        classPathElement(path:"lib/honeyimpl.jar")
        component(classNameKey:"org.nanocontainer.boot.Honey", class:"org.nanocontainer.boot.BeeHiveHoney")
    }
    classloader {
        classPathElement(path:"lib/bearimpl.jar")
        component(class:"org.nanocontainer.boot.BrownBear")
    }
}
pico = parent.getPico()
pico.getComponentInstances()
