package net.acomputerdog.BlazeLoader.tweaklauncher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.integrated.IntegratedPlayerList;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * A class transformer that injects BL classes into the game.
 */
public class BLTransformer implements IClassTransformer {
    public static final boolean isOBF = isGameOBF();

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) {
            return null;
        } else if (name == null) {
            return bytes;
        } else {
            if (name.equals(EntityRenderer.class.getName())) {
                return readClass(EntityRenderer.class, bytes);
            } else if (name.equals(IntegratedPlayerList.class.getName())) {
                return readClass(IntegratedPlayerList.class, bytes);
            } else if (name.equals(IntegratedServer.class.getName())) {
                return readClass(IntegratedServer.class, bytes);
            } else if (name.equals(Minecraft.class.getName())) {
                return readClass(Minecraft.class, bytes);
            } else if (name.equals(Profiler.class.getName())) {
                return readClass(Profiler.class, bytes);
            } else if (name.equals(WorldServer.class.getName())) {
                return readClass(WorldServer.class, bytes);
            } else if (name.equals(EntityList.class.getName())) {
                return readClass(EntityList.class, bytes);
            } else if (name.equals(NetHandlerPlayClient.class.getName())) {
                return readClass(NetHandlerPlayClient.class, bytes);
            } else if (name.equals(EntityTrackerEntry.class.getName())) {
                return readClass(EntityTrackerEntry.class, bytes);
            } else if (name.equals(CrashReport.class.getName())) {
                return readClass(CrashReport.class, bytes);
            } else if (name.equals(EntityTracker.class.getName())) {
                return readClass(EntityTracker.class, bytes);
            } else if (name.equals(RenderGlobal.class.getName())) {
                return readClass(RenderGlobal.class, bytes);
            } else if (name.equals(ChunkProviderServer.class.getName())) {
            	return readClass(ChunkProviderServer.class, bytes);
            } else {
                return bytes;
            }
        }
    }

    public byte[] readClass(Class cls, byte[] original) {
        String name = null;
        name = isOBF ? cls.getSimpleName() : cls.getName();
        TweakLauncher.logger.logDetail("Loading class: " + name);
        try {
            InputStream in = getClass().getResourceAsStream((isOBF ? "/net/minecraft/src/" + name : name.replaceAll(Pattern.quote("."), "/")) + ".class");
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

    private static boolean isGameOBF() {
        try {
            Class.forName("net.minecraft.client.Minecraft");
            TweakLauncher.logger.logDetail("Running in a non-obfuscated environment.");
            return false;
        } catch (Exception ignored) {
            TweakLauncher.logger.logDetail("Running in an obfuscated environment, this is the real deal!");
            return true;
        }
    }
}
