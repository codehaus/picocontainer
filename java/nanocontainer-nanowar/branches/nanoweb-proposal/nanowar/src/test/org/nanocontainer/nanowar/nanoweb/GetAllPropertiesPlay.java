package org.nanocontainer.nanowar.nanoweb;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class GetAllPropertiesPlay {

    public static void main(String[] args) {

        try {
            BeanInfo bi = Introspector.getBeanInfo(MyAction.class);
            PropertyDescriptor[] pd = bi.getPropertyDescriptors();

            for (int i = 0; i < pd.length; i++) {
                System.out.println(pd[i].getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
