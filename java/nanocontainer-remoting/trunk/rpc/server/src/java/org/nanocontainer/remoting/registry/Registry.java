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
package org.nanocontainer.remoting.registry;

import java.util.HashMap;

/**
 * A simple registry for binding.
 *
 * @author Paul Hammant
 */
public class Registry {

    private static HashMap c_map = new HashMap();

    /**
     * Get a Bound object.
     *
     * @param key the key required.
     * @return thebound object
     */
    public Binder get(String key) {
        synchronized (c_map) {
            return (Binder) c_map.get(key);
        }
    }

    /**
     * Bind an object
     *
     * @param key    the key
     * @param object the object
     */
    public void put(String key, Object object) {
        synchronized (c_map) {
            c_map.put(key, object);
        }
    }

}
