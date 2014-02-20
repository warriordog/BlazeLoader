package net.acomputerdog.BlazeLoader.mod;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.mod.resource.BLModResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handles detecting and loading mods.
 */
public class ModLoader {
    private static Map<String, File> sourceMap = new HashMap<String, File>();
    private static final File workingDir = new File(System.getProperty("user.dir"));

    public static File getModSource(String clsName) {
        return sourceMap.get(clsName);
    }

    public static void loadMods(File searchFile, File parentFile) {
        if (!searchFile.exists() || !searchFile.isDirectory()) {
            BlazeLoader.getLogger().logWarning("Invalid mod search directory: " + searchFile.getAbsolutePath());
        } else {
            File[] contents = searchFile.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    if (f.isDirectory()) {
                        loadMods(f, parentFile);
                    } else {
                        loadClasses(f, parentFile);
                    }
                }
            } else {
                loadClasses(searchFile, parentFile);
            }
        }
    }

    private static void loadClasses(File clsFile, File parentFile) {
        List<Class<? extends Mod>> modList = ModList.getUnloadedMods();
        List<URL> loaderURLs = new ArrayList<URL>();
        List<String> modClassNames = new ArrayList<String>();
        String path = clsFile.getPath();
        if (path.toLowerCase().endsWith(".jar") || path.toLowerCase().endsWith(".zip")) {
            loadZip(clsFile, modClassNames, loaderURLs);
        } else if (path.toLowerCase().endsWith(".class")) {
            try {
                path = getRelativePath(parentFile, clsFile);
            } catch (IOException e) {
                throw new RuntimeException("Could not get relative path!", e);
            }
            //path = clsFile.toURI().relativize(parentFile.toURI()).getPath();
            String className = path.replaceAll(Pattern.quote(System.getProperty("file.separator")), ".").substring(0, path.length() - 6);
            System.out.println(path + " : " + className);
            modClassNames.add(className);
            sourceMap.put(className, clsFile);
        } else {
            return;
        }
        ClassLoader loader = new URLClassLoader(loaderURLs.toArray(new URL[loaderURLs.size()]), ModLoader.class.getClassLoader());
        for (String modClassName : modClassNames) {
            loadClass(modClassName, loader, modList);
        }
    }

    private static String getRelativePath(File base, File name) throws IOException {
        File parent = base.getParentFile();

        if (parent == null) {
            throw new IOException("No common directory");
        }

        String bpath = base.getCanonicalPath();
        String fpath = name.getCanonicalPath();

        if (fpath.startsWith(bpath)) {
            return fpath.substring(bpath.length() + 1);
        } else {
            return (".." + File.separator + getRelativePath(parent, name));
        }
    }

    public static void loadZip(File modZip, List<String> modClassNames, List<URL> loaderURLs) {
        try {
            loaderURLs.add(modZip.toURI().toURL());
            ZipFile zipFile = new ZipFile(modZip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String name = entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6);
                    modClassNames.add(name);
                    sourceMap.put(name, modZip);
                }
            }
        } catch (IOException e) {
            BlazeLoader.getLogger().logWarning("Skipping corrupt zip: " + modZip.getName());
        } catch (Exception e) {
            BlazeLoader.getLogger().logWarning("Skipping corrupt mod in: " + modZip.getName());
        }
    }

    private static void loadClass(String className, ClassLoader loader, List<Class<? extends Mod>> modList) {
        try {
            Class modClass = loader.loadClass(className);
            if (Mod.class.isAssignableFrom(modClass) && !Mod.class.equals(modClass)) {
                modList.add(modClass);
                BlazeLoader.getLogger().logDetail("Loaded mod: " + modClass.getName() + ".");
            }
        } catch (Exception e) {
            if (!(e instanceof ClassNotFoundException)) {
                BlazeLoader.getLogger().logWarning("Skipping corrupt mod.");
            }
        }
    }

    public static void loadModAsResourcePack(ModData mod) {
        List<IResourcePack> defaultResourcePacks = Minecraft.getMinecraft().getDefaultResourcePacks();
        BLModResourcePack pack = new BLModResourcePack(mod);
        if (!defaultResourcePacks.contains(pack))
            defaultResourcePacks.add(pack);
    }
}
