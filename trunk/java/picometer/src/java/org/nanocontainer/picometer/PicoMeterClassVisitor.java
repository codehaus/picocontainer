package org.picoextras.picometer;

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
