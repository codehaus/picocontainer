package org.nanocontainer.nanowar.nanoweb;

public interface Converter {

    public Object fromString(String value);

    public String toString(Object value);

}
