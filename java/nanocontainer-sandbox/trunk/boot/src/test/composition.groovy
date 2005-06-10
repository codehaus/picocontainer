import org.nanocontainer.reflection.ImplementationHidingNanoPicoContainer

builder = new org.nanocontainer.script.groovy.NanoContainerBuilder()

parent = builder.container(parent:parent,class:ImplementationHidingNanoPicoContainer) {
    classpathelement(path:"lib/api.jar")
        classpathelement(path:"lib/bearimpl.jar")
        classpathelement(path:"lib/honeyimpl.jar")
        component(class:"org.nanocontainer.boot.BrownBear")
        component(class:"org.nanocontainer.boot.BeeHiveHoney")
}
pico = parent.getPico()
pico.getComponentInstances()
