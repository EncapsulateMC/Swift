package net.swift.launch;

import net.minecraft.launchwrapper.IClassTransformer;
import net.swift.asm.Order;
import net.swift.asm.SwiftASM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerTransformer implements IClassTransformer {
    private final Logger LOGGER = LogManager.getLogger("ServerTransformer");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("net.minecraft.server.MinecraftServer")) {
            LOGGER.info("Transforming MinecraftServer...");
            return new SwiftASM(basicClass)
                    .injectAt("l", "()V")
                    .set(Order.LAST, "net/swift/mod/Loader", "init", "(Ljava/lang/Object;)V", true)
                    .transform();
        }
        return basicClass;
    }
}
