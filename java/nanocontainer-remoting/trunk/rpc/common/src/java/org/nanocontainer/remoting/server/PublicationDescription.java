/* ====================================================================
 * Copyright 2005 NanoContainer Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nanocontainer.remoting.server;

import java.util.ArrayList;

/**
 * Class PublicationDescription
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public final class PublicationDescription {

    /**
     * An array of interfaces to expose.
     */
    private ArrayList m_interfacesToExpose = new ArrayList();
    /**
     * An array of additional facades.
     */
    private ArrayList m_additionalFacades = new ArrayList();

    /**
     * Construct a publication description.
     *
     * @param interfaceToExpose the principal interface implemented by the lookupable bean.
     */
    public PublicationDescription(Class interfaceToExpose) {
        this(new Class[]{interfaceToExpose}, new Class[0]);
    }

    /**
     * Construct a publication description.
     *
     * @param interfaceToExpose the principal interface implemented by the lookupable bean.
     * @param additionalFacade  additional facade implemented by other beans that
     *                          would otherwise be serialized and treated as pass-by-value objects.
     */
    public PublicationDescription(Class interfaceToExpose, Class additionalFacade) {
        this(new Class[]{interfaceToExpose}, new Class[]{additionalFacade});
    }

    /**
     * Construct a publication description.
     *
     * @param interfaceToExpose the principal interface implemented by the lookupable bean.
     * @param additionalFacades additional facades implemented by other beans that
     *                          would otherwise be serialized and treated as pass-by-value objects.
     */
    public PublicationDescription(Class interfaceToExpose, Class[] additionalFacades) {
        this(new Class[]{interfaceToExpose}, additionalFacades);
    }

    /**
     * Construct a publication description.
     *
     * @param interfacesToExpose the principal interfaces implemented by the lookupable bean.
     */
    public PublicationDescription(Class[] interfacesToExpose) {
        this(interfacesToExpose, new Class[0]);
    }

    /**
     * Construct a publication description.
     *
     * @param interfacesToExpose the principal interfaces implemented by the lookupable bean.
     * @param additionalFacades  assitional facades implemented by other beans that
     *                           would otherwise be serialized and treated as pass-by-value objects.
     */
    public PublicationDescription(Class[] interfacesToExpose, Class[] additionalFacades) {
        addInterfacesToExpose(interfacesToExpose);
        addAdditionalFacadesToExpose(additionalFacades);

    }

    /**
     * Construct a publication description.
     *
     * @param interfaceToExpose the principal interface implemented by the lookupable bean.
     * @param classLoader       the classloader containing the classdefs (special cases)
     * @throws PublicationException if there is a problem publishing
     */
    public PublicationDescription(String interfaceToExpose, ClassLoader classLoader) throws PublicationException {
        this(makeClasses(new String[]{interfaceToExpose}, classLoader), new Class[0]);
    }

    /**
     * Construct a publication description.
     *
     * @param interfaceToExpose the principal interface implemented by the lookupable bean.
     * @param additionalFacade  assitional facade implemented by other beans that
     *                          would otherwise be serialized and treated as pass-by-value objects.
     * @param classLoader       the classloader containing the classdefs (special cases)
     * @throws PublicationException if there is a problem publishing
     */
    public PublicationDescription(String interfaceToExpose, String additionalFacade, ClassLoader classLoader) throws PublicationException {
        this(makeClasses(new String[]{interfaceToExpose}, classLoader), makeClasses(new String[]{additionalFacade}, classLoader));
    }

    /**
     * Construct a publication description.
     *
     * @param interfaceToExpose the principal interface implemented by the lookupable bean.
     * @param additionalFacades assitional facades implemented by other beans that
     *                          would otherwise be serialized and treated as pass-by-value objects.
     * @param classLoader       the classloader containing the classdefs (special cases)
     * @throws PublicationException if there is a problem publishing
     */
    public PublicationDescription(String interfaceToExpose, String[] additionalFacades, ClassLoader classLoader) throws PublicationException {
        this(makeClasses(new String[]{interfaceToExpose}, classLoader), makeClasses(additionalFacades, classLoader));
    }

    /**
     * Construct a publication description.
     *
     * @param interfacesToExpose the principal interfaces implemented by the lookupable bean.
     * @param classLoader        the classloader containing the classdefs (special cases)
     * @throws PublicationException if there is a problem publishing
     */
    public PublicationDescription(String[] interfacesToExpose, ClassLoader classLoader) throws PublicationException {
        this(makeClasses(interfacesToExpose, classLoader), new Class[0]);
    }

    /**
     * Construct a publication description.
     *
     * @param interfacesToExpose the principal interfaces implemented by the lookupable bean.
     * @param additionalFacades  assitional facades implemented by other beans that
     *                           would otherwise be serialized and treated as pass-by-value objects.
     * @param classLoader        the classloader containing the classdefs (special cases)
     * @throws PublicationException if there is a problem publishing
     */
    public PublicationDescription(String[] interfacesToExpose, String[] additionalFacades, ClassLoader classLoader) throws PublicationException {
        this(makeClasses(interfacesToExpose, classLoader), makeClasses(additionalFacades, classLoader));
    }

    public PublicationDescription() {
    }

    private static Class[] makeClasses(String[] classNames, ClassLoader classLoader) throws PublicationException {

        try {
            Class[] classes = new Class[classNames.length];

            for (int i = 0; i < classNames.length; i++) {
                String clsNam = classNames[i];

                classes[i] = classLoader.loadClass(clsNam);
            }

            return classes;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

            throw new PublicationException("Class not found during publication:" + e.getMessage() + " " + e.getException().getMessage());
        }
    }

    public void addInterfacesToExpose(Class[] interfacesToExpose) {
        for (int i = 0; i < interfacesToExpose.length; i++) {
            Class interfaceToExpose = interfacesToExpose[i];
            addInterfaceToExpose(new PublicationDescriptionItem(interfaceToExpose));
        }
    }

    public void addInterfaceToExpose(PublicationDescriptionItem publicationDescriptionItem) {
        addInterfacesToExpose(new PublicationDescriptionItem[]{publicationDescriptionItem});
    }

    public void addInterfacesToExpose(PublicationDescriptionItem[] publicationDescriptionItems) {
        for (int i = 0; i < publicationDescriptionItems.length; i++) {
            PublicationDescriptionItem publicationDescriptionItem = publicationDescriptionItems[i];
            if (publicationDescriptionItem == null) {
                throw new RuntimeException("'PubDescItem' cannot be null");
            }
            if (publicationDescriptionItem.getFacadeClass() == null) {
                throw new RuntimeException("'Class' cannot be null");
            }
            m_interfacesToExpose.add(publicationDescriptionItem);
        }
    }

    public void addAdditionalFacadesToExpose(Class[] additionalFacades) {
        for (int i = 0; i < additionalFacades.length; i++) {
            Class additionalFacade = additionalFacades[i];
            addAdditionalFacadeToExpose(new PublicationDescriptionItem(additionalFacade));
        }
    }

    public void addAdditionalFacadeToExpose(PublicationDescriptionItem publicationDescriptionItem) {
        addAdditionalFacadesToExpose(new PublicationDescriptionItem[]{publicationDescriptionItem});
    }

    public void addAdditionalFacadesToExpose(PublicationDescriptionItem[] publicationDescriptionItems) {
        for (int i = 0; i < publicationDescriptionItems.length; i++) {
            PublicationDescriptionItem publicationDescriptionItem = publicationDescriptionItems[i];
            if (publicationDescriptionItem == null) {
                throw new RuntimeException("'PubDescItem' cannot be null");
            }
            if (publicationDescriptionItem.getFacadeClass() == null) {
                throw new RuntimeException("'Class' cannot be null");
            }
            m_additionalFacades.add(publicationDescriptionItem);
        }
    }


    /**
     * Get the princiapal interfaces to expose.
     *
     * @return an array of those interfaces.
     */
    public PublicationDescriptionItem[] getInterfacesToExpose() {
        PublicationDescriptionItem[] items = new PublicationDescriptionItem[m_interfacesToExpose.size()];
        m_interfacesToExpose.toArray(items);
        return items;
    }

    /**
     * Get the additional facades.
     *
     * @return an array of those facades.
     */
    public PublicationDescriptionItem[] getAdditionalFacades() {
        PublicationDescriptionItem[] items = new PublicationDescriptionItem[m_additionalFacades.size()];
        m_additionalFacades.toArray(items);
        return items;

    }


    /**
     * Get the most derived type for a bean.
     *
     * @param beanImpl the implementation
     * @return an interface that is the most derived type.
     */
    public Class getMostDerivedType(Object beanImpl) {

        //TODO relies of an order leadin to most derived type being last?

        Class facadeRetVal = null;

        for (int i = 0; i < m_additionalFacades.size(); i++) {
            Class facadeClass = ((PublicationDescriptionItem) m_additionalFacades.get(i)).getFacadeClass();

            if (facadeClass.isAssignableFrom(beanImpl.getClass())) {
                if (facadeRetVal == null) {
                    facadeRetVal = facadeClass;
                } else if (facadeRetVal.isAssignableFrom(facadeClass)) {
                    facadeRetVal = facadeClass;
                }
            }
        }

        return facadeRetVal;
    }
}
