package net.swift.launch;

import net.minecraft.launchwrapper.IClassTransformer;
import net.swift.asm.Order;
import net.swift.asm.SwiftASM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HookTransformer implements IClassTransformer {
    public static final Logger LOGGER = LogManager.getLogger("HookTransformer");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("net.minecraft.server.MinecraftServer")) {
            LOGGER.info("Transforming MinecraftServer...");
            return new SwiftASM(basicClass)
                    .injectAt("u", "()V")
                    .set(Order.BEGIN, "net/swift/mod/Loader", "callModEndEvent", "()V", false)
                    .transform();
        }
        return basicClass;
    }
}
