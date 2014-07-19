package net.acomputerdog.BlazeLoader.transformers;

import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;
import net.acomputerdog.BlazeLoader.util.obf.BLOBF;

import java.util.regex.Pattern;

public class BLEventInjectionTransformer extends EventInjectionTransformer {
    /**
     * Subclasses should register events here
     */
    @Override
    protected void addEvents() {
        BLOBF minecraftOBF = BLOBF.getClassMCP("net.minecraft.client.Minecraft");
        String[] loadWorldOBF = splitArgs(BLOBF.getMethodMCP("net.minecraft.client.Minecraft.loadWorld (Lnet.minecraft.client.multiplayer.WorldClient;Ljava.lang.String;)V").obf); //TODO get rid of parameters!
        //MethodInfo loadWorld = new MethodInfo(minecraftOBF, loadWorldOBF[0].replace(minecraftOBF + ".", ""), loadWorldOBF[1]);
        MethodInfo loadWorld = new MethodInfo(minecraftOBF, loadWorldOBF[0], loadWorldOBF[1]);
        InjectionPoint methodHead = new MethodHead();

        this.addEvent(Event.getOrCreate("Minecraft.loadWorld", false), loadWorld, methodHead)
                .addListener(new MethodInfo("net.acomputerdog.BlazeLoader.event.EventHandler", "eventLoadWorld"));

        String[] runGameLoop = splitArgs(BLOBF.getMethodMCP("net.minecraft.client.Minecraft.runGameLoop ()V").name);

        System.err.println("\"" + runGameLoop[0] + "\" : \"" + runGameLoop[1] + "\"");
        this.addEvent(Event.getOrCreate("Minecraft.runGameLoop", false), new MethodInfo(minecraftOBF, runGameLoop[0], runGameLoop[1]), methodHead)
                .addListener(new MethodInfo("net.acomputerdog.BlazeLoader.event.EventHandler", "runGameLoop"));

    }

    private String[] splitArgs(String method) {
        if (method == null) {
            throw new NullPointerException("Method is null!");
        }
        String[] parts = method.split(Pattern.quote(" "));
        String[] packages = parts[0].split(Pattern.quote("."));
        if (packages.length > 0) {
            parts[0] = packages[packages.length - 1];
        }
        return parts;
    }
}
