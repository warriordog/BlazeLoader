package net.minecraft.src;

import net.acomputerdog.BlazeLoader.mod.ModList;

import java.util.*;

/**
 * Profiles various Minecraft functions.  Replaces ProfilerProxy.
 */
public class Profiler
{
    /** List of parent sections */
    private final List sectionList = new ArrayList();

    /** List of timestamps (System.nanoTime) */
    private final List timestampList = new ArrayList();

    /** Flag profiling enabled */
    public boolean profilingEnabled;

    /** Current profiling section */
    private String profilingSection = "";

    /** Profiling map */
    private final Map profilingMap = new HashMap();

    /**
     * Clear profiling.
     */
    public void clearProfiling()
    {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
    }

    /**
     * Start section
     */
    public void startSection(String sectionName)
    {
        ModList.startSection(sectionName);

        if (this.profilingEnabled)
        {
            if (this.profilingSection.length() > 0)
            {
                this.profilingSection = this.profilingSection + ".";
            }

            this.profilingSection = this.profilingSection + sectionName;
            this.sectionList.add(this.profilingSection);
            this.timestampList.add(System.nanoTime());
        }
    }

    /**
     * End section
     */
    public void endSection()
    {
        if (this.profilingEnabled)
        {
            long nanoTime = System.nanoTime();
            long var3 = (Long) this.timestampList.remove(this.timestampList.size() - 1);
            this.sectionList.remove(this.sectionList.size() - 1);
            long var5 = nanoTime - var3;

            if (this.profilingMap.containsKey(this.profilingSection))
            {
                this.profilingMap.put(this.profilingSection, (Long) this.profilingMap.get(this.profilingSection) + var5);
            }
            else
            {
                this.profilingMap.put(this.profilingSection, var5);
            }

            if (var5 > 100000000L)
            {
                System.out.println("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox " + (double)var5 / 1000000.0D + " ms");
            }

            this.profilingSection = !this.sectionList.isEmpty() ? (String)this.sectionList.get(this.sectionList.size() - 1) : "";
        }
        ModList.endSection(getNameOfLastSection());
    }

    /**
     * Get profiling data
     */
    public List getProfilingData(String section)
    {
        if (!this.profilingEnabled)
        {
            return null;
        }
        else
        {
            long var3 = this.profilingMap.containsKey("root") ? (Long) this.profilingMap.get("root") : 0L;
            long var5 = this.profilingMap.containsKey(section) ? (Long) this.profilingMap.get(section) : -1L;
            ArrayList var7 = new ArrayList();

            if (section.length() > 0)
            {
                section = section + ".";
            }

            long var8 = 0L;

            for (Object o : this.profilingMap.keySet()) {
                String var11 = (String) o;

                if (var11.length() > section.length() && var11.startsWith(section) && var11.indexOf(".", section.length() + 1) < 0) {
                    var8 += (Long) this.profilingMap.get(var11);
                }
            }

            float var21 = (float)var8;

            if (var8 < var5)
            {
                var8 = var5;
            }

            if (var3 < var8)
            {
                var3 = var8;
            }

            Iterator var20 = this.profilingMap.keySet().iterator();
            String var12;

            while (var20.hasNext())
            {
                var12 = (String)var20.next();

                if (var12.length() > section.length() && var12.startsWith(section) && var12.indexOf(".", section.length() + 1) < 0)
                {
                    long var13 = (Long) this.profilingMap.get(var12);
                    double var15 = (double)var13 * 100.0D / (double)var8;
                    double var17 = (double)var13 * 100.0D / (double)var3;
                    String var19 = var12.substring(section.length());
                    var7.add(new ProfilerResult(var19, var15, var17));
                }
            }

            var20 = this.profilingMap.keySet().iterator();

            while (var20.hasNext())
            {
                var12 = (String)var20.next();
                this.profilingMap.put(var12, (Long) this.profilingMap.get(var12) * 999L / 1000L);
            }

            if ((float)var8 > var21)
            {
                var7.add(new ProfilerResult("unspecified", (double)((float)var8 - var21) * 100.0D / (double)var8, (double)((float)var8 - var21) * 100.0D / (double)var3));
            }

            Collections.sort(var7);
            var7.add(0, new ProfilerResult(section, 100.0D, (double)var8 * 100.0D / (double)var3));
            return var7;
        }
    }

    /**
     * End current section and start a new section
     */
    public void endStartSection(String section)
    {
        this.endSection();
        this.startSection(section);
    }

    public String getNameOfLastSection()
    {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String)this.sectionList.get(this.sectionList.size() - 1);
    }
}
