package org.picocontainer.gems.adapters;

import java.io.IOException;

public class ElephantProxy implements Elephant {
    private transient Elephant delegate;

    public ElephantProxy(Elephant delegate) {
        this.delegate = delegate;
    }

    public String objects(String one, String two) throws IOException {
        return delegate.objects(one, two);
    }

    public String[] objectsArray(String[] one, String[] two) throws IOException {
        return delegate.objectsArray(one, two);
    }

    public int iint(int a, int b) {
        return delegate.iint(a, b);
    }

    public long llong(long a, long b) {
        return delegate.llong(a, b);
    }

    public byte bbyte(byte a, byte b, byte c) {
        return delegate.bbyte(a, b, c);
    }

    public float ffloat(float a, float b, float c, float d) {
        return delegate.ffloat(a, b, c, d);
    }

    public double ddouble(double a, double b) {
        return delegate.ddouble(a, b);
    }

    public char cchar(char a, char b) {
        return delegate.cchar(a, b);
    }

    public short sshort(short a, short b) {
        return delegate.sshort(a, b);
    }

    public boolean bboolean(boolean a, boolean b) {
        return delegate.bboolean(a, b);
    }

    public boolean[] bbooleanArray(boolean[] a, boolean b[]) {
        return delegate.bbooleanArray(a, b);
    }
}