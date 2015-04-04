package com.blazeloader.api.client;

import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * Tools for locating resources
 */
public class ResourceLoc {

    /**
     * Gets the .jar location
     */
    public static String getPath() {
        Package pack = MinecraftServer.class.getPackage();
        String path = (pack == null ? "" : pack.getName().replace('.', '/') + "/") + "MinecraftServer.class";
        URL MODL = MinecraftServer.class.getClassLoader().getResource(path);

        if (MODL != null) {
            path = MODL.getPath().replace("/" + path, "");
            if (path.endsWith("?")) {
                path = path.substring(0, path.length() - 1);
            }
            if (path.startsWith("file:")) {
                path = path.substring(5);
            }
        } else {
            path = "NULL";
        }
        return path;
    }

    /**
     * Gets a file representing the current .jar
     */
    public static File getClassPath() {
        return new File(getPath());
    }

    /**
     * Gets a file from the internal assets folder
     * <p><i>.jar/assets/{pack}/{name}</i>
     *
     * @param pack Name of resource pack
     * @param name Name/Path to file
     * @return File object representing the file found in the .jar or null if not found
     */
    public static File getResource(String pack, String name) {
        URI uri = getResourceURI(pack, name);
        return uri == null ? null : new File(uri);
    }

    /**
     * Gets a URI path to a file in the internal assets folder
     * <p><i>.jar/assets/{pack}/{name}</i>
     *
     * @param pack Name of resource pack
     * @param name Name/Path to file
     * @return URI object pointing to the file found in the .jar or null if not found
     */
    public static URI getResourceURI(String pack, String name) {
        return getURI("assets/" + (pack == null || pack.isEmpty() ? "minecraft" : pack) + "/" + name);
    }

    private static URI getURI(String path) {
        try {
            URL url = ResourceLoc.class.getClassLoader().getResource(path);
            if (url == null) {
                return null;
            }
            return url.toURI();
        } catch (Exception e) {
            throw new RuntimeException("Exception getting URI!", e);
        }
    }
}
