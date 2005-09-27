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
package org.nanocontainer.remoting.server.classretrievers;

import org.nanocontainer.remoting.server.ClassRetrievalException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class PlainClassRetriever
 *
 * @author Paul Hammant
 * @author Vinay Chandrasekharan <a href="mailto:vinay_chandran@users.sourceforge.net">
 *         vinay_chandran@users.sourceforge.net</a>
 * @version $Revision: 1.2 $
 */
public class PlainClassRetriever extends AbstractClassRetriever {

    private ClassLoader m_classLoader;

    /**
     * Constructor PlainClassRetriever
     */
    public PlainClassRetriever() {
        m_classLoader = this.getClass().getClassLoader();
    }

    /**
     * Create a plain clasretriever from a classloader.
     *
     * @param classLoader the classloader.
     */
    public PlainClassRetriever(ClassLoader classLoader) {
        m_classLoader = classLoader;
    }

    /**
     * Get the classfile byte array for the thing name
     *
     * @param thingName the things name
     * @return the byte array of the thing.
     * @throws ClassRetrievalException if the bytes are not available.
     */
    protected byte[] getThingBytes(String thingName) throws ClassRetrievalException {

        InputStream is = null;

        is = m_classLoader.getResourceAsStream(thingName + ".class");

        if (is == null) {
            throw new ClassRetrievalException("Generated class for " + thingName + " not found in specified classloader ");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;

        try {
            while (-1 != (i = is.read())) {
                baos.write(i);
            }

            is.close();
        } catch (IOException e) {
            throw new ClassRetrievalException("Error retrieving generated class bytes : " + e.getMessage());
        }

        return baos.toByteArray();
    }
}
