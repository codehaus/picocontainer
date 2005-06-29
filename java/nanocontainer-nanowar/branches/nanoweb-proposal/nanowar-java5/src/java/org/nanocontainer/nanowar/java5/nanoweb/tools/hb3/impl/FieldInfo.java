package org.nanocontainer.nanowar.java5.nanoweb.tools.hb3.impl;

import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

public class FieldInfo {

    public PersistentClass persistentClass;
    public Property property;
    public Column column;

    public FieldInfo(Configuration configuration, Class type, Object bean, String name) {
        this(configuration.getClassMapping(type.getName()), bean, name);
    }

    public FieldInfo(PersistentClass persistentClass, Object bean, String name) {
        this.persistentClass = persistentClass;
        property = persistentClass.getProperty(name);
        column = getColumn(property);
    }

    private static Column getColumn(Property p) {
        Iterator i = p.getColumnIterator();
        if (i.hasNext()) {
            return (Column) i.next();
        }

        return null;
    }
}
