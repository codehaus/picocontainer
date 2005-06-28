import org.nanocontainer.reflection.ImplementationHidingNanoPicoContainer
import org.nanocontainer.ClassNameKey

builder = new org.nanocontainer.script.groovy.NanoContainerBuilder()

parent = builder.container(parent:parent,class:ImplementationHidingNanoPicoContainer) {
    classpathelement(path:"lib/api.jar")
    classloader {
        classpathelement(path:"lib/honeyimpl.jar")
        component(key:new ClassNameKey("org.nanocontainer.boot.Honey"), class:"org.nanocontainer.boot.BeeHiveHoney")
    }
    classloader {
        classpathelement(path:"lib/bearimpl.jar")
        component(class:"org.nanocontainer.boot.BrownBear")
    }
}
pico = parent.getPico()
pico.getComponentInstances()
