package org.picocontainer.gui.model;

import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * A model for a bean that
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyModel {
    private final Map propertyMap = new HashMap();

    private final PropertyDescriptor[] propertyDescriptors;
    private final Object[] propertyValues;
    private final Class beanClass;

    public BeanPropertyModel(Class beanClass) throws IntrospectionException {
        this.beanClass = beanClass;
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        propertyDescriptors = beanInfo.getPropertyDescriptors();
        propertyValues = new Object[propertyDescriptors.length];
    }

    public PropertyDescriptor getPropertyDescriptor(int index) {
       return propertyDescriptors[index];
    }

    public Object getPropertyValue(int index) {
       return propertyValues[index];
    }

    public void setPropertyValue(Object value, int index) {
        // verify the type
        Class propertyType = getPropertyDescriptor(index).getPropertyType();
        if(!propertyType.isInstance(value)) throw new IllegalArgumentException("Bad value. Expected a " + propertyType.getName() + ". Got a " + value.getClass().getName());
        propertyValues[index] = value;

        // now set it in the map too.
        String propertyName = getPropertyDescriptor(index).getName();
        propertyMap.put(propertyName, value);
    }

    public int getSize() {
        return propertyDescriptors.length;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public Map getPropertyMap() {
        return Collections.unmodifiableMap(propertyMap);
    }
}
