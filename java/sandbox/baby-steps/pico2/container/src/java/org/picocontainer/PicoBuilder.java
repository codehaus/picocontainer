package org.picocontainer;

public class PicoBuilder {

    private boolean withCDI;
    private boolean withSDI;

    public PicoBuilder withCDI() {
        withCDI = true;
        return this;
    }

    public PicoBuilder withSDI() {
        withSDI = true;
        return this;
    }

//    public MutablePicoContainer build() {
//        if (withCDI) {
//            ConstructorInjectionComponentAdapterFactory cicaf = new ConstructorInjectionComponentAdapterFactory();
//        }
//    }



}
