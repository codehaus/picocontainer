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
package org.nanocontainer.remoting.common;


import java.lang.reflect.Method;


/**
 * Class AbstractMethodHandler
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class AbstractMethodHandler {

    /**
     * Generate a string representation of a method signature
     *
     * @param method The method that needs a signature
     * @return String representing method sig
     */
    protected String getMethodSignature(Method method) {

        StringBuffer methodSignature = new StringBuffer();

        methodSignature.append(method.getName()).append("(");

        Class[] params = method.getParameterTypes();

        for (int i = 0; i < params.length; i++) {
            methodSignature.append(params[i].getName());

            if (i + 1 < params.length) {
                methodSignature.append(", ");
            }
        }

        methodSignature.append(")");

        return methodSignature.toString().intern();
    }

    /**
     * Encode a classname - i.e. escapes dots for dollars in package/class name
     *
     * @param clazz class that has name that needs encoding.
     * @return String encoded class name
     */
    protected String encodeClassName(Class clazz) {
        return encodeClassName(clazz.getName());
    }

    /**
     * Encode a classname - i.e. escapes dots for dollars in package/class name
     *
     * @param className class name that needs encoding.
     * @return String encoded class name
     */
    protected String encodeClassName(String className) {
        return className.replace('.', '$');
    }
}
