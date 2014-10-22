package com.blazeloader.api.client.api.client;

import com.blazeloader.api.client.util.ResourceLoc;
import org.lwjgl.opengl.Display;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * API functions for changing the client window.  Title branding, etc.
 */
public class ApiWindowClient {
    private static ArrayList<String> brands = new ArrayList<String>();

    /**
     * Sets the window icon from a mod resource pack
     * <p/>
     * icons are loaded from the following location:
     * assets/ {resource_pack_name} /icon/icon_16x16.png
     * assets/ {resource_pack_name} /icon/icon_32x32.png
     * <p/>
     * at least one of these images must be present
     *
     * @param resourcePack Name of resource pack
     */
    public static void setIcon(String resourcePack) {
        File icon16x = ResourceLoc.getResource(resourcePack, "icon/icon_16x16.png");
        File icon32x = ResourceLoc.getResource(resourcePack, "icon/icon_32x32.png");

        if (icon16x != null && icon32x == null) icon32x = icon16x;
        if (icon16x == null && icon32x != null) icon16x = icon32x;
        if (icon16x != null) {
            try {
                Display.setIcon(new ByteBuffer[]{getIcon(icon16x), getIcon(icon32x)});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the window title
     *
     * @param title The title of the window
     */
    public static void setTitle(String title) {
        Display.setTitle(title);
    }

    /**
     * Adds a mod to the client brand
     *
     * @param brand Brand Name
     */
    public static void setClientBrand(String brand) {
        if (brand != null && !brand.isEmpty() && !brands.contains(brand)) {
            brands.add(brand);
        }
    }

    /**
     * Gets the formatted Client brand name
     */
    public static String getClientBrand() {
        StringBuilder builder = new StringBuilder();
        builder.append("BlazeLoader");
        for (String str : brands) {
            builder.append(", ");
            builder.append(str);
        }
        return builder.toString();
    }

    private static ByteBuffer getIcon(File par1File) throws IOException {
        BufferedImage var2 = ImageIO.read(par1File);
        int[] var3 = var2.getRGB(0, 0, var2.getWidth(), var2.getHeight(), null, 0, var2.getWidth());
        ByteBuffer var4 = ByteBuffer.allocate(4 * var3.length);

        for (int var8 : var3) {
            var4.putInt(var8 << 8 | var8 >> 24 & 255);
        }

        var4.flip();
        return var4;
    }

}
