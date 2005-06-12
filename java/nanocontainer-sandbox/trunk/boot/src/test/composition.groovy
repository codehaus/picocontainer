import org.nanocontainer.reflection.ImplementationHidingNanoPicoContainer
import org.nanocontainer.ClassNameKey

builder = new org.nanocontainer.script.groovy.NanoContainerBuilder()

parent = builder.container(parent:parent,class:ImplementationHidingNanoPicoContainer) {
    classpathelement(path:"lib/api.jar")
    hidden() {
        classpathelement(path:"lib/honeyimpl.jar")
        component(key:new ClassNameKey("org.nanocontainer.boot.Honey"), class:"org.nanocontainer.boot.BeeHiveHoney")
    }
    hidden() {
        classpathelement(path:"lib/bearimpl.jar")
        component(class:"org.nanocontainer.boot.BrownBear")
    }
}
pico = parent.getPico()
pico.getComponentInstances()
