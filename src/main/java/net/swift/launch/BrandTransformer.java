package net.swift.launch;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class BrandTransformer implements IClassTransformer {
    public static final Logger LOGGER = LogManager.getLogger("BrandTransformer");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("net.minecraft.server.MinecraftServer")) {
            LOGGER.info("Transforming MinecraftServer...");
            ClassReader classReader = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);
            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.name.equals("getServerModName")) {
                    LOGGER.info("Found MinecraftServer#getServerModName");
                    Iterator<AbstractInsnNode> insnNodeIterator = methodNode.instructions.iterator();
                    while (insnNodeIterator.hasNext()) {
                        AbstractInsnNode abstractInsnNode = insnNodeIterator.next();
                        if (abstractInsnNode instanceof LdcInsnNode) {
                            LOGGER.info("Found LdcInsnNode");
                            methodNode.instructions.set(abstractInsnNode, new LdcInsnNode("swift"));
                        }
                    }
                }
            }
            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return basicClass;
    }
}
