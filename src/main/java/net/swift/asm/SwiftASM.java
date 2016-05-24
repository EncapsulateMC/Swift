package net.swift.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

/**
 * Easier ASM for method injection.
 * - Static methods only.
 * - Only <b>void <methodName>()</b> or <b>void <methodName>(Object instanceShared)</b>
 *
 * @author PizzaCrust
 * @since 1.0-SNAPSHOT
 */
public class SwiftASM {
    private final byte[] originalClass;

    private MethodInjection injectionLoc;

    private MethodInjection injection;
    private Order injectionOrder;

    private boolean shareInstance;

    public SwiftASM(byte[] classBytes) {
        this.originalClass = classBytes;
    }

    public SwiftASM injectAt(String method, String desc) {
        injectionLoc = new MethodInjection("", method, desc);
        return this;
    }

    public SwiftASM set(Order order, String owner, String method, String desc, boolean shareInstance) {
        injection = new MethodInjection(owner, method, desc);
        injectionOrder = order;
        this.shareInstance = shareInstance;
        return this;
    }

    public byte[] transform() {
        ClassReader classReader = new ClassReader(originalClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(injectionLoc.methodName)
                    && methodNode.desc.equals(injectionLoc.methodDesc)) {
                MethodInsnNode methodInsnNode = new MethodInsnNode(INVOKESTATIC, injection.ownerClass.getName(), injection.methodName, injection.methodDesc);
                InsnList insnList = new InsnList();
                if (shareInstance) {
                    insnList.add(new VarInsnNode(INVOKESTATIC, 0));
                }
                insnList.add(methodInsnNode);
                if (this.injectionOrder == Order.BEGIN) {
                    methodNode.instructions.insert(insnList);
                }
                if (this.injectionOrder == Order.LAST) {
                    Iterator<AbstractInsnNode> insnNodeIterator = methodNode.instructions.iterator();
                    while (insnNodeIterator.hasNext()) {
                        AbstractInsnNode currentInsn = insnNodeIterator.next();
                        if (currentInsn.getOpcode() == RETURN) {
                            methodNode.instructions.insertBefore(currentInsn, insnList);
                        }
                    }
                }
            }
        }
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
