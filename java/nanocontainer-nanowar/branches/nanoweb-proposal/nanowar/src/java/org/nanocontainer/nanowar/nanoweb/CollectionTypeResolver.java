package org.nanocontainer.nanowar.nanoweb;

import java.lang.reflect.Member;


public interface CollectionTypeResolver {

    public Class getType(Member member);
    
}
