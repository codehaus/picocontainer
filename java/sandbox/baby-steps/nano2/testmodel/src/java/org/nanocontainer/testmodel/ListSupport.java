package org.nanocontainer.testmodel;

import java.util.List;

/**
 * Test for list support.
 *
 * @author Jeff Steward
 */
public class ListSupport
{
    private List<Entity> aListOfEntityObjects;

    public ListSupport(List<Entity> aListOfEntityObjects)
    {
        this.aListOfEntityObjects = aListOfEntityObjects;
    }

    public List<Entity> getAListOfEntityObjects()
    {
        return aListOfEntityObjects;
    }
}