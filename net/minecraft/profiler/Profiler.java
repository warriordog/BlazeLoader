package net.minecraft.profiler;

import net.acomputerdog.BlazeLoader.mod.ModList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Profiles various Minecraft functions.  Replaces ProfilerProxy.
 */
public class Profiler
{
    private static final Logger field_151234_b = LogManager.getLogger();

    /** List of parent sections */
    private final List sectionList = new ArrayList();

    /** List of timestamps (System.nanoTime) */
    private final List timestampList = new ArrayList();

    /** Flag profiling enabled */
    public boolean profilingEnabled;

    /** Current profiling section */
    private String profilingSection = "";

    /** Profiling map */
    public final Map profilingMap = new HashMap();

    private static final String __OBFID = "CL_00001497";
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
                field_151234_b.warn("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox " + (double)var5 / 1000000.0D + " ms");
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
                    var7.add(new Profiler.Result(var19, var15, var17));
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
                var7.add(new Profiler.Result("unspecified", (double)((float)var8 - var21) * 100.0D / (double)var8, (double)((float)var8 - var21) * 100.0D / (double)var3));
            }

            Collections.sort(var7);
            var7.add(0, new Profiler.Result(section, 100.0D, (double)var8 * 100.0D / (double)var3));
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

    public static final class Result implements Comparable
    {
        public double field_76332_a;
        public double field_76330_b;
        public String field_76331_c;
        private static final String __OBFID = "CL_00001498";

        public Result(String par1Str, double par2, double par4)
        {
            this.field_76331_c = par1Str;
            this.field_76332_a = par2;
            this.field_76330_b = par4;
        }

        public int compareTo(Profiler.Result par1ProfilerResult)
        {
            return par1ProfilerResult.field_76332_a < this.field_76332_a ? -1 : (par1ProfilerResult.field_76332_a > this.field_76332_a ? 1 : par1ProfilerResult.field_76331_c.compareTo(this.field_76331_c));
        }

        public int func_76329_a()
        {
            return (this.field_76331_c.hashCode() & 11184810) + 4473924;
        }

        public int compareTo(Object par1Obj)
        {
            return this.compareTo((Profiler.Result)par1Obj);
        }
    }
}
