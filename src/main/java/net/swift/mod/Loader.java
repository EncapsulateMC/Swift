package net.swift.mod;

import com.google.common.eventbus.EventBus;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.jar.JarFile;

public class Loader {
    public static Object MINECRAFT_INSTANCE;
    public static final Logger LOGGER = LogManager.getLogger("SML");
    public static final File MODS_DIRECTORY = new File(System.getProperty("user.dir"), "mods");
    public static final EventBus EVENT_BUS = new EventBus();

    public static void init(Object instance) {
        MINECRAFT_INSTANCE = instance;
        LOGGER.info("SML v1.0-SNAPSHOT");
        LOGGER.info("Copyrighted 2016 by the Swift Team");
        LOGGER.info("http://pizzacrustrepo.gq");
        LOGGER.info("Searching for mods at " + MODS_DIRECTORY.getAbsolutePath());
        if (!MODS_DIRECTORY.exists()) {
            MODS_DIRECTORY.mkdir();
        }
        File[] mods = MODS_DIRECTORY.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        LOGGER.info("Found " + mods.length + " mods in the /mods directory.");
        for (File mod : mods) {
            LOGGER.info("Loading mod " + mod.getName() + "...");
            try {
                JarFile jarFile = new JarFile(mod);
                String modClassName = jarFile.getManifest().getMainAttributes().getValue("Mod-Class");
                ((LaunchClassLoader) Thread.currentThread().getContextClassLoader()).addURL(mod.toURI().toURL());
                Class<?> modClass = Class.forName(modClassName);
                Object instanceOfMod = modClass.newInstance();
                EVENT_BUS.register(instanceOfMod);
            } catch (Exception e) {
                LOGGER.error("Failed to load mod " + mod.getName());
                e.printStackTrace();
                continue;
            }
        }
    }
}
