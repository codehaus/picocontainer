package org.nanocontainer.nanowar.nanoweb;


public class CarConverter implements Converter {

    public Object fromString(String value) {
        return new Car(value);
    }

    public String toString(Object value) {
        return ((Car) value).getName();
    }
    
}
