/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.picometer;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.CodeVisitor;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoMeterClassVisitor implements ClassVisitor {
    private final CodeVisitor codeVisitor;

    public PicoMeterClassVisitor(CodeVisitor codeVisitor) {
        this.codeVisitor = codeVisitor;
    }

    public void visit(int access, String name, String superName, String[] interfaces, String sourceFile) {
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
    }

    public void visitAttribute(Attribute attribute) {
    }

    public void visitField(int access, String name, String desc, Object value, Attribute attribute) {
    }

    public CodeVisitor visitMethod(int i, String componentImplementationClassName, String componentImplementationClassName1, String[] strings, Attribute attribute) {
        return codeVisitor;
    }

    public void visitEnd() {
    }
}
