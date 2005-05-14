/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/

// The class is in this package only to be able to access package private members from Label.
package org.objectweb.asm;

import org.nanocontainer.picometer.Instantiation;
import org.nanocontainer.picometer.PicoMeterClass;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Visits code and records instantiations of new objects.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoMeterCodeVisitor implements CodeVisitor {
    private final Collection instantiations;
    private final PicoMeterClass picoMeterClass;

    private final Map labelToInstantiationsMap = new HashMap();

    private String lastType;
    private Label currentLabel;

    public PicoMeterCodeVisitor(Collection instantiations, PicoMeterClass picoMeterClass) {
        this.instantiations = instantiations;
        this.picoMeterClass = picoMeterClass;
    }

    public void visitTypeInsn(int opcode, String desc) {
        if (Constants.NEW == opcode) {
            lastType = desc;
        } else {
            lastType = null;
        }
    }


    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        boolean isNew = (Constants.INVOKESPECIAL == opcode) && owner.equals(lastType) && "<init>".equals(name);
        if (isNew) {
            String className = owner.replace('/', '.');
            final Instantiation instantiation = new Instantiation(className, picoMeterClass);
            labelToInstantiationsMap.put(currentLabel, instantiation);
            instantiations.add(instantiation);
        }
    }

    public void visitLabel(Label label) {
        currentLabel = label;
    }

    public void visitLineNumber(int line, Label start) {
        Instantiation instantiation = (Instantiation) labelToInstantiationsMap.get(start);
        if (instantiation != null) {
            try {
                instantiation.setBytecodeLine(line);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public void visitAttribute(Attribute attribute) {
    }

    public void visitInsn(int opcode) {
    }

    public void visitIntInsn(int opcode, int operand) {
    }

    public void visitVarInsn(int opcode, int var) {
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    }

    public void visitJumpInsn(int opcode, Label label) {
    }

    public void visitLdcInsn(Object cst) {
    }

    public void visitIincInsn(int var, int increment) {
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label labels[]) {
    }

    public void visitLookupSwitchInsn(Label dflt, int keys[], Label labels[]) {
    }

    public void visitMultiANewArrayInsn(String desc, int dims) {
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    }

    public void visitMaxs(int maxStack, int maxLocals) {
    }

    public void visitLocalVariable(String name, String desc, Label start, Label end, int index) {
    }
}
