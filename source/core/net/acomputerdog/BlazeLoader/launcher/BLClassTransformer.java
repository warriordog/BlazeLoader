package net.acomputerdog.BlazeLoader.launcher;

import net.acomputerdog.BlazeLoader.main.Version;
import net.minecraft.launchwrapper.IClassTransformer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A class transformer that injects BL classes into the game.
 */
public class BLClassTransformer implements IClassTransformer {
    public static final boolean isOBF = Version.isGameObfuscated();
    private static final List<String> overrideClasses = createOverrideList();

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (!isOBF) {
            return bytes;
        }
        if (overrideClasses.contains(name)) {
            return readClass(name, bytes);
        } else {
            return bytes;
        }
    }

    public byte[] readClass(String name, byte[] original) {
        TweakLauncher.logger.logDetail("Loading class: " + name);
        try {
            InputStream in = getClass().getResourceAsStream((isOBF ? "/bl_classes_vanilla/" + name : name.replaceAll(Pattern.quote("."), "/")) + ".class");
            if (in != null) {
                BufferedInputStream bin = new BufferedInputStream(in);
                byte[] bytes = new byte[bin.available()];
                if (bin.read(bytes, 0, bytes.length) != -1) {
                    return bytes;
                } else {
                    TweakLauncher.logger.logError("End of stream while loading a class!");
                    return original;
                }
            } else {
                return original;
            }
        } catch (Exception e) {
            TweakLauncher.logger.logError("Could not load a class!");
            e.printStackTrace();
            return original;
        }
    }

    private static List<String> createOverrideList() {
        List<String> ol = new ArrayList<String>();
        CodeSource source = BLClassTransformer.class.getProtectionDomain().getCodeSource();
        if (source != null) {
            try {
                String path = BLClassTransformer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                String decodedPath = URLDecoder.decode(path, "UTF-8");
                ZipFile zf = new ZipFile(new File(decodedPath));
                Enumeration<? extends ZipEntry> entries = zf.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith("bl_classes_vanilla/") && name.endsWith(".class")) {
                        ol.add(name.substring(18, name.length() - 6));
                    }
                }
            } catch (IOException e) {
                TweakLauncher.logger.logFatal("Exception preparing to inject BlazeLoader!");
                e.printStackTrace();
            }
        } else {
            TweakLauncher.logger.logFatal("BLTransformer could not access the CodeSource!  BlazeLoader cannot be injected into minecraft!");
            TweakLauncher.logger.logFatal("The game should still run, however BlazeLoader may not function or load at all!");
        }
        return ol;
    }

}
