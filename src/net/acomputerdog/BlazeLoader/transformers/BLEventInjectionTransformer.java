package net.acomputerdog.BlazeLoader.transformers;

import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;
import net.acomputerdog.BlazeLoader.util.obf.BLMethodInfo;
import net.acomputerdog.BlazeLoader.util.obf.BLOBF;

import java.util.regex.Pattern;

/**
 * Injects events into MC classes
 */
public class BLEventInjectionTransformer extends EventInjectionTransformer {
    private static final String EVENT_HANDLER = "net.acomputerdog.BlazeLoader.event.EventHandler";
    private static final String PREFIX = Pattern.quote("BL.");

    /**
     * Subclasses should register events here
     */
    @Override
    protected void addEvents() {
        try {
            MethodInfo loadWorld = BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.client.Minecraft.loadWorld (Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V").getValue());
            MethodInfo startSection = BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.profiler.Profiler.startSection (Ljava/lang/String;)V").getValue());
            MethodInfo endSection = BLMethodInfo.create(BLOBF.getMethodMCP("net.minecraft.profiler.Profiler.endSection ()V").getValue());

            InjectionPoint methodHead = new MethodHead();

            this.addBLEvent("BL.eventLoadWorld", loadWorld, methodHead);
            this.addBLEvent("BL.eventProfilerStart", startSection, methodHead);
            this.addBLEvent("BL.eventProfilerEnd", endSection, methodHead);
        } catch (Exception e) {
            System.err.println("A fatal exception occurred while injecting BlazeLoader!  BlazeLoader will not be able to run!");
            throw new RuntimeException("Exception injecting BlazeLoader!", e);
        }
    }

    private void addBLEvent(String name, MethodInfo method, InjectionPoint injectionPoint) {
        this.addEvent(Event.getOrCreate(name, false), method, injectionPoint).addListener(new MethodInfo(EVENT_HANDLER, stripPrefix(name)));
    }

    private String stripPrefix(String name) {
        if (name == null) {
            return null;
        }
        return name.replaceFirst(PREFIX, "");
    }
}
