package org.picocontainer.defaults.issues;

import junit.framework.Assert;

public class DescriptorDep {
    public DescriptorDep(Descriptor.DescriptorData[] datas) {
        Assert.assertEquals(3, datas.length);
        Assert.assertNull(datas[0]);
        Assert.assertNull(datas[1]);
        Assert.assertNull(datas[2]);
    }
}
