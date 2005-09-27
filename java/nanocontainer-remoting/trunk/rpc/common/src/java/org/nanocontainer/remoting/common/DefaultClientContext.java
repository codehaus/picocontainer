package org.nanocontainer.remoting.common;

import org.nanocontainer.remoting.ClientContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DefaultClientContext implements ClientContext, Externalizable {

    private long contextSeq;
    private int hashCode;

    public DefaultClientContext(long contextSeq) {
        this.contextSeq = contextSeq;
        setConstants();
    }

    // for externalization.
    public DefaultClientContext() {
    }

    private void setConstants() {
        hashCode = new Long(contextSeq).hashCode();
    }

    public int hashCode() {
        return hashCode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DefaultClientContext)) {
            return false;
        } else {
            return contextSeq == ((DefaultClientContext) obj).contextSeq;
        }
    }

    public String toString() {
        return "DefaultClientContext:" + contextSeq;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(contextSeq);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        contextSeq = in.readLong();
        setConstants();
    }
}
