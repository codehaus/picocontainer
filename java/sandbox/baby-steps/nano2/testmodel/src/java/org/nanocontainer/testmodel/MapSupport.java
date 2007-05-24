package org.nanocontainer.testmodel;

import java.util.Map;

public class MapSupport
{
    private Map<String, Entity> aMapOfEntities;

    public MapSupport(Map<String, Entity> aMapOfEntities)
    {
        this.aMapOfEntities = aMapOfEntities;
    }

    public Map<String, Entity> getAMapOfEntities()
    {
        return aMapOfEntities;
    }
}
