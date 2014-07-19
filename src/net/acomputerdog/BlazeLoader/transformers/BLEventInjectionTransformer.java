package net.acomputerdog.BlazeLoader.transformers;

import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;
import net.acomputerdog.BlazeLoader.util.obf.BLMethodInfo;
import net.acomputerdog.BlazeLoader.util.obf.BLOBF;

/**
 * Injects events into MC classes
 */
public class BLEventInjectionTransformer extends EventInjectionTransformer {
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

            this.addEvent(Event.getOrCreate("BL.loadWorld", false), loadWorld, methodHead)
                    .addListener(new MethodInfo("net.acomputerdog.BlazeLoader.event.EventHandler", "eventLoadWorld"));
            this.addEvent(Event.getOrCreate("BL.profilerStart", false), startSection, methodHead)
                    .addListener(new MethodInfo("net.acomputerdog.BlazeLoader.event.EventHandler", "eventProfilerStart"));
            this.addEvent(Event.getOrCreate("BL.profilerEnd", false), endSection, methodHead)
                    .addListener(new MethodInfo("net.acomputerdog.BlazeLoader.event.EventHandler", "eventProfilerEnd"));
        } catch (Exception e) {
            System.err.println("A fatal exception occurred while injecting BlazeLoader!  BlazeLoader will not be able to run!");
            throw new RuntimeException("Exception injecting BlazeLoader!", e);
        }
    }
}
