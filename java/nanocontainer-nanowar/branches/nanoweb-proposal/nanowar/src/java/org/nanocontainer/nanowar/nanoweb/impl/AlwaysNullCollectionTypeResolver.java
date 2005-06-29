package org.nanocontainer.nanowar.nanoweb.impl;

import java.lang.reflect.Member;

import org.nanocontainer.nanowar.nanoweb.CollectionTypeResolver;

/**
 * Implementation of CollectionTypeResolver which always return null as collection.
 * 
 * @version $Id$
 */
public class AlwaysNullCollectionTypeResolver implements CollectionTypeResolver {

    /**
     * @return null
     */
    public Class getType(Member member) {
        return null;
    }

}
