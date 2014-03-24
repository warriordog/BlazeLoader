package net.acomputerdog.BlazeLoader.event;

/**
 * Interface for mods that handle profiler events
 */
public interface ProfilerEventHandler {
    /**
     * Called when a profiler section is started.  Mods are notified BEFORE profiler.
     *
     * @param sectionName Name of the profiler section started.
     */
    public void eventProfilerStart(String sectionName);

    /**
     * Called when a profiler section is ended.  Mods are notified AFTER profiler.
     *
     * @param sectionName Name of the profiler section ended.
     */
    public void eventProfilerEnd(String sectionName);
}
