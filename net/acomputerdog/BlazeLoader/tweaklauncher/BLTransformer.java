package net.acomputerdog.BlazeLoader.tweaklauncher;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.src.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * A class transformer that injects BL classes into the game.
 */
public class BLTransformer implements IClassTransformer{
    public static final boolean isOBF = isGameOBF();

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) {
            return null;
        }else if(name == null){
            return bytes;
        }else{
            if(name.equals(EntityRenderer.class.getName())){
                return readClass(EntityRenderer.class.getName(), bytes);
            }else if(name.equals(IntegratedPlayerList.class.getName())){
                return readClass(IntegratedPlayerList.class.getName(), bytes);
            }else if(name.equals(IntegratedServer.class.getName())){
                return readClass(IntegratedServer.class.getName(), bytes);
            }else if(name.equals(Minecraft.class.getName())){
                return readClass(Minecraft.class.getName(), bytes);
            }else if(name.equals(Profiler.class.getName())){
                return readClass(Profiler.class.getName(), bytes);
            }else if(name.equals(WorldServer.class.getName())){
                return readClass(WorldServer.class.getName(), bytes);
            }else if(name.equals(EntityList.class.getName())){
                return readClass(EntityList.class.getName(), bytes);
            }else if(name.equals(NetClientHandler.class.getName())){
                return readClass(NetClientHandler.class.getName(), bytes);
            }else if(name.equals(EntityTrackerEntry.class.getName())){
                return readClass(EntityTrackerEntry.class.getName(), bytes);
            }else{
                return bytes;
            }
        }
    }

    public byte[] readClass(String name, byte[] original){
        try{
            InputStream in = getClass().getResourceAsStream((isOBF ? "/net/minecraft/src/" + name : name.replaceAll(Pattern.quote("."), "/")) + ".class");
            if(in != null){
                BufferedInputStream bin = new BufferedInputStream(in);
                byte[] bytes = new byte[bin.available()];
                if(bin.read(bytes, 0, bytes.length) != -1){
                    return bytes;
                }else{
                    System.out.println("End of stream while loading a class!");
                    return original;
                }
            }else{
                return original;
            }
        }catch(Exception e){
            System.out.println("Could not load a class!");
            e.printStackTrace();
            return original;
        }
    }

    private static boolean isGameOBF(){
        try{
            Class.forName("net.minecraft.src.Block");
            return false;
        }catch(Exception ignored){
            return true;
        }
    }
}
