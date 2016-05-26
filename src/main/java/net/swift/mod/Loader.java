package net.swift.mod;

import com.google.common.eventbus.EventBus;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.swift.event.ModStartEvent;
import net.swift.event.ModStopEvent;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
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
        double specificationVersion = Double.parseDouble(System.getProperty("java.specification.version"));
        if (specificationVersion < 1.8) {
            LOGGER.warn("Java 8 isn't being used. Please use Java 8 to have better compatibility with newer mods, better performance and security.");
        }
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
                ((LaunchClassLoader) Thread.currentThread().getContextClassLoader()).addURL(mod.toURI().toURL());
                Enumeration<JarEntry> entryEnumeration = jarFile.entries();
                List<Class<?>> candidates = new ArrayList<Class<?>>();
                while (entryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = entryEnumeration.nextElement();
                    if (jarEntry.getName().endsWith(".class")) {
                        Class<?> theClass = Thread.currentThread().getContextClassLoader().loadClass(FilenameUtils.removeExtension(jarEntry.getName()).replace('/', '.'));
                        if (theClass.isAnnotationPresent(Mod.class)) {
                            candidates.add(theClass);
                        }
                    }
                }
                if (candidates.size() < 1) {
                    LOGGER.error("No mod class candidates for mod file " + mod.getName() + "! (" + candidates.size() + ")");
                    continue;
                }
                if (candidates.size() > 1) {
                    LOGGER.error("Too many candidates for mod class! Using " + candidates.get(0));
                }
                Class<?> modClass = candidates.get(0);
                Object instanceOfMod = modClass.newInstance();
                EVENT_BUS.register(instanceOfMod);
            } catch (Exception e) {
                LOGGER.error("Failed to load mod " + mod.getName());
                e.printStackTrace();
                continue;
            }
        }
        LOGGER.info("Calling ModStartEvent on loaded mods...");
        EVENT_BUS.post(new ModStartEvent());
        LOGGER.info("SML has finished.");
    }

    public static void callModEndEvent() {
        LOGGER.info("Shutting down all mods...");
        EVENT_BUS.post(new ModStopEvent());
        LOGGER.info("SML has finished.");
    }
}
