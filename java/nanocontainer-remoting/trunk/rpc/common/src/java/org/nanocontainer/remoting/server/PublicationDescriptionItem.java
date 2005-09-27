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

import org.nanocontainer.remoting.common.MethodNameHelper;

import java.lang.reflect.Method;
import java.util.Vector;

/**
 * Class PublicationDescriptionItem
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public class PublicationDescriptionItem {

    private final Class m_facadeClass;
    private final Vector m_asyncMethods = new Vector();
    private final Vector m_commitMethods = new Vector();
    private final Vector m_rollbackMethods = new Vector();

    public PublicationDescriptionItem(Class facadeClass) {
        m_facadeClass = facadeClass;
        Method[] methods = facadeClass.getDeclaredMethods();
        try {
            AttributeHelper attributeHelper = new AttributeHelper();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (attributeHelper.isMethodAsync(method)) {
                    m_asyncMethods.add(MethodNameHelper.getMethodSignature(method));
                }
                if (attributeHelper.isMethodAsyncCommit(method)) {
                    m_commitMethods.add(MethodNameHelper.getMethodSignature(method));
                }
                if (attributeHelper.isMethodAsyncRollback(method)) {
                    m_rollbackMethods.add(MethodNameHelper.getMethodSignature(method));
                }
            }
        } catch (NoClassDefFoundError ncdfe) {
            // TODO a soft check for Atributes / CommonsLogger missing?
            //System.out.println("--> ncdfe " + ncdfe.getMessage());
            //ncdfe.printStackTrace();
            // attribute jars are missing.
            // This allowed for when there is no Async functionality.
        } catch (RuntimeException re) {
            if (!re.getClass().getName().equals("org.apache.commons.attributes.AttributesException")) {
                throw re;
            }
        }
    }

    public PublicationDescriptionItem(Class facadeClass, String[] asyncMethods, String[] commitMethods, String[] rollbackMethods) throws PublicationException {
        m_facadeClass = facadeClass;
        if (facadeClass == null) {
            throw new RuntimeException("Facade class nust not be null");
        }

        for (int i = 0; i < asyncMethods.length; i++) {
            String asyncMethod = asyncMethods[i];
            testAsyncMethodType(facadeClass.getMethods(), asyncMethod);
            m_asyncMethods.add(asyncMethod);
        }

        for (int i = 0; i < commitMethods.length; i++) {
            String commitMethod = commitMethods[i];
            testAsyncMethodType(facadeClass.getMethods(), commitMethod);
            m_commitMethods.add(commitMethod);
        }

        for (int i = 0; i < rollbackMethods.length; i++) {
            String rollbackMethod = rollbackMethods[i];
            testAsyncMethodType(facadeClass.getMethods(), rollbackMethod);
            m_rollbackMethods.add(rollbackMethod);
        }
    }

    private void testAsyncMethodType(Method[] methods, String methodSignature) throws PublicationException {
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (MethodNameHelper.getMethodSignature(method).equals(methodSignature)) {
                if (method.getReturnType().toString().equals("void") != true) {
                    throw new PublicationException("Only 'void' returning methods are " + "eligible for asynchronous methods.");
                }

                Class[] exceptions = method.getExceptionTypes();

                if (exceptions.length != 0) {
                    throw new PublicationException("Only methods without exceptions " + "are eligible for asynchronous behavior ");
                }
            }
        }
    }


    public Class getFacadeClass() {
        return m_facadeClass;
    }

    public boolean isCommit(Method method) {
        String mthSig = MethodNameHelper.getMethodSignature(method);
        for (int i = 0; i < m_commitMethods.size(); i++) {
            String asyncMethod = (String) m_commitMethods.elementAt(i);
            if (asyncMethod.equals(mthSig)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRollback(Method method) {
        String mthSig = MethodNameHelper.getMethodSignature(method);
        for (int i = 0; i < m_rollbackMethods.size(); i++) {
            String asyncMethod = (String) m_rollbackMethods.elementAt(i);
            if (asyncMethod.equals(mthSig)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAsync(Method method) {
        String mthSig = MethodNameHelper.getMethodSignature(method);
        for (int i = 0; i < m_asyncMethods.size(); i++) {
            String asyncMethod = (String) m_asyncMethods.elementAt(i);
            if (asyncMethod.equals(mthSig)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAsyncBehavior() {
        return (m_asyncMethods.size() != 0 | m_commitMethods.size() != 0 | m_rollbackMethods.size() != 0);
    }

}
