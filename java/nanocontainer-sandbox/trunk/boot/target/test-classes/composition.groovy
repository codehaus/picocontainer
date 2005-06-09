builder = new org.nanocontainer.script.groovy.NanoContainerBuilder()
parent = builder.container {
    classpathelement(path:"lib/api.jar")
    container() {
        classpathelement(path:"lib/bearimpl.jar")
        component(class:"org.nanocontainer.boot.BrownBear")
    }
    container() {
        classpathelement(path:"lib/honeyimpl.jar")
        component(class:"org.nanocontainer.boot.BeeHiveHoney")
    }
}