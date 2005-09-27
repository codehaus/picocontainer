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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

/**
 * A special ObjectInputStream to handle highly transient classes hosted
 * by containers that are juggling many classloaders.
 *
 * @author Paul Hammant
 */
public class ClassLoaderObjectInputStream extends ObjectInputStream {

    private ClassLoader m_classLoader;

    /**
     * Constructor ClassLoaderObjectInputStream
     *
     * @param classLoader the classloader that containes the classes that may be deserialized
     * @param byteArray   tye bytes for the thing to be deserialized.
     * @throws IOException              from super
     * @throws StreamCorruptedException from super
     */
    public ClassLoaderObjectInputStream(final ClassLoader classLoader, byte[] byteArray) throws IOException, StreamCorruptedException {

        super(new ByteArrayInputStream(byteArray));

        m_classLoader = classLoader;
    }


    /**
     * This method overrides the super and adds classloader aware deserialization
     *
     * @param objectStreamClass the class that we're looking for during deserialization
     * @return the class itself if found, null if not.
     * @throws IOException            some general problem classloading from files/resources.
     * @throws ClassNotFoundException this nor its super knowns about the class.
     */
    protected Class resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {

        Class clazz = null;

        try {
            clazz = m_classLoader.loadClass(objectStreamClass.getName());
        } catch (ClassNotFoundException cnfe) {
            // this may be OK, see below.
        }

        if (null != clazz) {
            return clazz;    // the classloader knows of the class
        } else {

            // classloader knows not of class, let the super classloader do it
            //printCLs();
            return super.resolveClass(objectStreamClass);
        }
    }
}
