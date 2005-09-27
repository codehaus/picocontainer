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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class SerializationHelper
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class SerializationHelper {

    /**
     * Get bytes from instance
     *
     * @param instance the object to turn in to serialized byte array
     * @return the byte array
     */
    public static byte[] getBytesFromInstance(Object instance) {

        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        ObjectOutputStream oOs;

        byte[] aBytes = new byte[0];
        try {
            oOs = new ObjectOutputStream(bAOS);

            oOs.writeObject(instance);
            oOs.flush();

            aBytes = bAOS.toByteArray();

            oOs.close();
            bAOS.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NanoContainerRemotingRuntimeException("Really out of the ordinary IOException", e);
        }

        return aBytes;
    }

    /**
     * Get instance from bytes.
     *
     * @param byteArray to turn into an instance
     * @return the instance
     * @throws ClassNotFoundException if the class-def can't be resolved.
     */
    public static Object getInstanceFromBytes(byte[] byteArray) throws ClassNotFoundException {
        return getInstanceFromBytes(byteArray, SerializationHelper.class.getClassLoader());
    }

    /**
     * Get instance from bytes.
     *
     * @param byteArray   to turn into an instance
     * @param classLoader the classloader that can resolve the class-def
     * @return the instance
     * @throws ClassNotFoundException if the class-def can't be resolved.
     */
    public static Object getInstanceFromBytes(byte[] byteArray, ClassLoader classLoader) throws ClassNotFoundException {

        try {
            ObjectInputStream oIs = new ClassLoaderObjectInputStream(classLoader, byteArray);
            Object obj = oIs.readObject();
            oIs.close();
            return obj;
        } catch (IOException ioe) {
            if (ioe.getCause() instanceof RuntimeException) {
                throw (RuntimeException) ioe.getCause();
            }
            return null;
        }
    }
}
