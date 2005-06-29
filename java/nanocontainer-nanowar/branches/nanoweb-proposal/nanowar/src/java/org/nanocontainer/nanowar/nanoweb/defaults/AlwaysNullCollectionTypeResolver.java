package org.nanocontainer.nanowar.nanoweb.defaults;

import java.lang.reflect.Member;

import org.nanocontainer.nanowar.nanoweb.CollectionTypeResolver;


/**
 * Implementation of CollectionTypeResolver which always return null as collection.
 * 
 * @version $Id: AlwaysNullCollectionTypeResolver.java 2134 2005-06-29 00:49:09Z juze $
 */
public class AlwaysNullCollectionTypeResolver implements CollectionTypeResolver {

    /**
     * @return null
     */
    public Class getType(Member member) {
        return null;
    }

}
