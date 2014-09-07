package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.core.base.mod.BLMod;
import net.minecraft.profiler.Profiler;

/**
 * Interface for mods that handle profiler events
 */
public interface ProfilerEventClientHandler extends BLMod {
    /**
     * Called when a profiler section is started.  Mods are notified BEFORE profiler.
     *
     * @param profiler    The profiler who's section is starting.
     * @param sectionName Name of the profiler section started.
     */
    public void eventProfilerStart(Profiler profiler, String sectionName);

    /**
     * Called when a profiler section is ended.  Mods are notified BEfORE profiler.
     *
     * @param profiler    The profiler who's section is starting.
     * @param sectionName Name of the profiler section ended.
     */
    public void eventProfilerEnd(Profiler profiler, String sectionName);
}
