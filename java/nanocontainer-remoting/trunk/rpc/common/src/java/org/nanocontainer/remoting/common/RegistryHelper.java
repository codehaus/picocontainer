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
 * RegistryHelper
 *
 * @author Paul Hammant
 */
public class RegistryHelper {

    /**
     * Put an item into the registry
     *
     * @param key    The key
     * @param binder The object to Bind
     */
    public void put(String key, Object binder) {
        try {
            Class registryClass = this.getClass().getClassLoader().loadClass("org.nanocontainer.remoting.registry.Registry");
            Object registry = registryClass.newInstance();
            Method put = registryClass.getMethod("put", new Class[]{String.class, Object.class});
            put.invoke(registry, new Object[]{key, binder});
        } catch (Exception e) {
            // If the registry is not found then c'est la vie.
        }
    }

    /**
     * Get an item from the registry
     *
     * @param key The key
     * @return The bound object
     */
    public Object get(String key) {
        try {
            Object registry = this.getClass().getClassLoader().loadClass("org.nanocontainer.remoting.registry.Registry").newInstance();
            //Binder binder = registry.get("/.nanocontainerRemoting/optimizations/" + uniqueID);
            Method method = registry.getClass().getMethod("get", new Class[]{String.class});
            return method.invoke(registry, new Object[]{key});
        } catch (Exception e) {
            return null;
        }
    }
}
